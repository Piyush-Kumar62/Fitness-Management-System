package com.project.fitness.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${app.cors.allowed-origins:http://localhost:4200}")
  private String allowedOrigins;

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
  private final ClientRegistrationRepository clientRegistrationRepository;
  private final RateLimitFilter rateLimitFilter;
  private final XssFilter xssFilter;

  public SecurityConfig(
      JwtAuthenticationFilter jwtAuthenticationFilter,
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
      OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
      @Nullable ClientRegistrationRepository clientRegistrationRepository,
      RateLimitFilter rateLimitFilter,
      XssFilter xssFilter
  ) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.customOAuth2UserService = customOAuth2UserService;
    this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.rateLimitFilter = rateLimitFilter;
    this.xssFilter = xssFilter;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain oauthSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/oauth2/**", "/login/oauth2/**")
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    if (clientRegistrationRepository != null) {
      http.oauth2Login(oauth2 -> oauth2
          .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
          .successHandler(oAuth2AuthenticationSuccessHandler)
          .failureHandler(oAuth2AuthenticationFailureHandler)
      );
    }

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.deny())
            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' https://js.stripe.com; frame-src 'self' https://js.stripe.com; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; connect-src 'self' https://api.stripe.com ws: wss:;"))
            .contentTypeOptions(contentTypeOptions -> {})
            .xssProtection(xss -> xss.disable())
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000))
            .referrerPolicy(referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(permissions -> permissions.policy(
                "camera=(), microphone=(), geolocation=(), payment=(self)"))
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
            ).permitAll()
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers("/actuator/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
            .requestMatchers("/api/v1/auth/complete-profile").authenticated()
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
            .requestMatchers("/ws/**").permitAll()
            .requestMatchers("/api/v1/stripe/webhook").permitAll()

            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/trainer/**").hasRole("TRAINER")
            .requestMatchers("/api/v1/owner/**").hasRole("OWNER")
            .requestMatchers("/api/v1/gyms/**").hasAnyRole("ADMIN", "OWNER")

            // Versioned + legacy compatibility endpoints
            .requestMatchers(
                "/api/v1/membership-plans/**", "/api/v1/memberships/**", "/api/v1/payments/**", "/api/v1/stripe/**",
                "/api/membership-plans/**", "/api/memberships/**", "/api/payments/**"
            ).hasAnyRole("MEMBER", "TRAINER", "OWNER", "ADMIN")

            .requestMatchers("/api/v1/member/**").hasRole("MEMBER")

            .requestMatchers(
                "/api/v1/activities/**",
                "/api/v1/goals/**",
                "/api/v1/measurements/**",
                "/api/v1/files/**",
                "/api/v1/users/search",
                "/api/v1/users/profile",
                "/api/v1/users/profile/**",
                "/api/v1/users/change-password",
                "/api/v1/recommendations/**"
            ).hasAnyRole("MEMBER", "TRAINER", "OWNER", "ADMIN")

            .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );

    http.addFilterBefore(
        rateLimitFilter,
        UsernamePasswordAuthenticationFilter.class
    );

    http.addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter.class
    );

    http.addFilterAfter(
        xssFilter,
        JwtAuthenticationFilter.class
    );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .collect(Collectors.toList());
    if (origins.contains("*")) {
      configuration.setAllowedOriginPatterns(List.of("*"));
      configuration.setAllowCredentials(false);
    } else {
      configuration.setAllowedOrigins(origins);
      configuration.setAllowCredentials(true);
    }
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
    configuration.setExposedHeaders(List.of("Authorization"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
