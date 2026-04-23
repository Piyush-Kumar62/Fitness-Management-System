package com.project.fitness.security;

import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
  
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;

  @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}")
  private String redirectUri;

  public OAuth2AuthenticationSuccessHandler(
      JwtUtils jwtUtils,
      UserRepository userRepository
  ) {
    this.jwtUtils = jwtUtils;
    this.userRepository = userRepository;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {

    if (response.isCommitted()) {
      log.debug("Response has already been committed. Unable to redirect.");
      return;
    }

    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
    
    // Get email from OAuth2User attributes
    String email = oauth2User.getAttribute("email");
    if (!StringUtils.hasText(email)) {
      email = authentication.getName();
    }
    
    log.info("OAuth2 authentication success for authenticated principal");
    
    if (!StringUtils.hasText(email)) {
      log.error("Email not found in OAuth2 user attributes");
      String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("error", "Email not found from OAuth2 provider")
          .build()
          .toUriString();
      getRedirectStrategy().sendRedirect(request, response, errorUrl);
      return;
    }

    String normalizedEmail = email.toLowerCase().trim();
    User user;
    try {
      user = resolveOrCreateUser(authentication, oauth2User, normalizedEmail);
    } catch (IllegalStateException ex) {
      log.error("OAuth2 account linking error for {}: {}", normalizedEmail, ex.getMessage());
      String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("error", ex.getMessage())
          .build()
          .toUriString();
      getRedirectStrategy().sendRedirect(request, response, errorUrl);
      return;
    }

    if (user.getRole() == null) {
      String onboardingToken = jwtUtils.generateToken(user.getId(), user.getEffectiveRole().name());
      String targetUrl = buildFragmentRedirect("status=ROLE_SELECTION_REQUIRED&token="
          + UriUtils.encode(onboardingToken, java.nio.charset.StandardCharsets.UTF_8));
      getRedirectStrategy().sendRedirect(request, response, targetUrl);
      return;
    }

    try {
      assertLoginAllowed(user);
    } catch (IllegalStateException ex) {
      String blockedUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("error", ex.getMessage())
          .build()
          .toUriString();
      getRedirectStrategy().sendRedirect(request, response, blockedUrl);
      return;
    }

    String token = jwtUtils.generateToken(user.getId(), user.getEffectiveRole().name());

    String targetUrl = buildFragmentRedirect("token="
        + UriUtils.encode(token, java.nio.charset.StandardCharsets.UTF_8));

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  private String buildFragmentRedirect(String fragment) {
    return UriComponentsBuilder.fromUriString(redirectUri)
        .fragment(fragment)
        .build(true)
        .toUriString();
  }

  private User resolveOrCreateUser(Authentication authentication, OAuth2User oauth2User, String email) {
    User existing = userRepository.findByEmail(email);
    String provider = resolveProvider(authentication);
    String providerId = resolveProviderId(provider, oauth2User);

    if (existing != null) {
      if (StringUtils.hasText(provider) && !StringUtils.hasText(existing.getProvider())) {
        existing.setProvider(provider);
      }
      if (StringUtils.hasText(providerId)) {
        existing.setProviderId(providerId);
      }
      updateProfileFields(existing, oauth2User);
      return userRepository.save(existing);
    }

    User created = new User();
    created.setEmail(email);
    created.setRole(null);
    created.setStatus(AccountStatus.PENDING);
    created.setEmailVerified(false);
    created.setActive(true);
    created.setPassword(null);
    created.setProvider(StringUtils.hasText(provider) ? provider : "oauth2");
    created.setProviderId(providerId);
    updateProfileFields(created, oauth2User);
    return userRepository.save(created);
  }

  private void updateProfileFields(User user, OAuth2User oauth2User) {
    String imageUrl = firstNonBlank(
        asString(oauth2User.getAttribute("picture")),
        asString(oauth2User.getAttribute("avatar_url")),
        asString(oauth2User.getAttribute("profileImageUrl"))
    );
    if (StringUtils.hasText(imageUrl)) {
      user.setProfileImageUrl(imageUrl);
    }

    String name = firstNonBlank(
        asString(oauth2User.getAttribute("name")),
        asString(oauth2User.getAttribute("login"))
    );
    if (StringUtils.hasText(name)) {
      String[] parts = name.trim().split("\\s+", 2);
      user.setFirstName(parts[0]);
      user.setLastName(parts.length > 1 ? parts[1] : "");
      return;
    }

    if (!StringUtils.hasText(user.getFirstName()) && StringUtils.hasText(user.getEmail())) {
      user.setFirstName(user.getEmail().split("@")[0]);
      if (!StringUtils.hasText(user.getLastName())) {
        user.setLastName("");
      }
    }
  }

  private String resolveProvider(Authentication authentication) {
    if (authentication instanceof OAuth2AuthenticationToken token) {
      return token.getAuthorizedClientRegistrationId();
    }
    return null;
  }

  private String resolveProviderId(String provider, OAuth2User oauth2User) {
    Map<String, Object> attributes = oauth2User.getAttributes();
    if ("google".equalsIgnoreCase(provider)) {
      return asString(attributes.get("sub"));
    }
    if ("github".equalsIgnoreCase(provider)) {
      return asString(attributes.get("id"));
    }
    return firstNonBlank(asString(attributes.get("sub")), asString(attributes.get("id")));
  }

  private String asString(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (StringUtils.hasText(value)) {
        return value;
      }
    }
    return null;
  }

  private void assertLoginAllowed(User user) {
    if (!user.isActive()) {
      throw new IllegalStateException("Account is deactivated. Please contact an administrator.");
    }
    if (!user.isEmailVerified()) {
      throw new IllegalStateException("Email not verified. Please verify your email before logging in.");
    }
    AccountStatus status = user.getStatus() == null ? AccountStatus.APPROVED : user.getStatus();
    if (status != AccountStatus.APPROVED) {
      throw new IllegalStateException("Account is pending approval.");
    }
  }
}
