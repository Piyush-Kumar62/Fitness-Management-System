package com.project.fitness.security;

import com.project.fitness.model.User;
import com.project.fitness.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
  
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;

  @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}")
  private String redirectUri;

  public OAuth2AuthenticationSuccessHandler(JwtUtils jwtUtils, UserRepository userRepository) {
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
    
    if (email == null || email.isEmpty()) {
      log.error("Email not found in OAuth2 user attributes");
      String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("error", "Email not found from OAuth2 provider")
          .build()
          .toUriString();
      getRedirectStrategy().sendRedirect(request, response, errorUrl);
      return;
    }

    User user = userRepository.findByEmail(email.toLowerCase());

    // Retry logic for transaction timing
    if (user == null) {
      try {
        Thread.sleep(300);
        user = userRepository.findByEmail(email.toLowerCase());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    if (user == null) {
      log.error("User not found after OAuth2 authentication: {}", email);
      String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("error", "Authentication failed. Please try again.")
          .build()
          .toUriString();
      getRedirectStrategy().sendRedirect(request, response, errorUrl);
      return;
    }

    // Generate JWT token
    String token = jwtUtils.generateToken(user.getId(), user.getRole().name());

    // Redirect to frontend with token
    String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("token", token)
        .build()
        .toUriString();

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
