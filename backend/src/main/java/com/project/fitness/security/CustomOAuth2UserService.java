package com.project.fitness.security;

import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.repository.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
  private static final String GITHUB_EMAILS_API = "https://api.github.com/user/emails";
  private final UserRepository userRepository;
  private final RestClient restClient;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.restClient = RestClient.create();
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    try {
      return processOAuth2User(userRequest, oauth2User);
    } catch (Exception ex) {
      log.error("OAuth2 authentication error: {}", ex.getMessage(), ex);
      throw new OAuth2AuthenticationException(ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    
    log.info("Processing OAuth2 login - Provider: {}", registrationId);
    log.debug("OAuth2 user attributes: {}", oauth2User.getAttributes());
    
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
        registrationId,
        oauth2User.getAttributes()
    );

    String email = oAuth2UserInfo.getEmail();

    if ("github".equalsIgnoreCase(registrationId) && !StringUtils.hasText(email)) {
      email = fetchGithubPrimaryEmail(userRequest.getAccessToken().getTokenValue());
    }
    
    log.info("Extracted email from OAuth2 provider: {}", email);
    
    // Improved validation for email
    if (email == null || email.trim().isEmpty()) {
      log.error("Email not found from OAuth2 provider: {}", registrationId);
      throw new BadRequestException(
          "Email not found from OAuth2 provider. Please ensure your email is public in your " + 
          registrationId + " account settings."
      );
    }
    
    // Validate email format (basic check)
    if (!email.contains("@") || !email.contains(".")) {
      log.error("Invalid email format received: {}", email);
      throw new BadRequestException("Invalid email format received from OAuth2 provider");
    }

    email = email.toLowerCase().trim();
    User user = userRepository.findByEmail(email);

    if (user != null) {
      log.info("Existing user found with email: {}", email);
      user = updateExistingUser(user, registrationId, oAuth2UserInfo);
      log.info("Updated existing user: {}", user.getId());
    } else {
      log.info("Registering new user with email: {}", email);
      user = registerNewUser(registrationId, oAuth2UserInfo, email);
      log.info("Successfully registered new user: {} with provider: {}", user.getId(), registrationId);
    }

    Map<String, Object> enrichedAttributes = new HashMap<>(oauth2User.getAttributes());
    enrichedAttributes.put("email", user.getEmail());
    enrichedAttributes.put("name", (user.getFirstName() + " " + user.getLastName()).trim());
    enrichedAttributes.put("profileImageUrl", user.getProfileImageUrl());
    enrichedAttributes.put("provider", user.getProvider());
    enrichedAttributes.put("providerId", user.getProviderId());

    String nameAttributeKey = userRequest.getClientRegistration()
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUserNameAttributeName();

    if (!StringUtils.hasText(nameAttributeKey) || !enrichedAttributes.containsKey(nameAttributeKey)) {
      nameAttributeKey = "email";
    }

    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getEffectiveRole().name())),
        enrichedAttributes,
        nameAttributeKey
    );
  }

  @Transactional
  private User registerNewUser(String registrationId, OAuth2UserInfo oAuth2UserInfo, String email) {
    log.info("Creating new user - Provider: {}, Email: {}", registrationId, email);
    
    User user = new User();
    user.setProvider(registrationId);
    user.setProviderId(oAuth2UserInfo.getId());
    user.setEmail(email.toLowerCase());
    user.setRole(null);
    user.setStatus(AccountStatus.PENDING);
    user.setEmailVerified(false);
    user.setActive(true);
    user.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
    user.setPassword(null); // OAuth2 users don't have passwords

    String name = oAuth2UserInfo.getName();
    if (StringUtils.hasText(name)) {
      String[] parts = name.split(" ", 2);
      user.setFirstName(parts[0]);
      user.setLastName(parts.length > 1 ? parts[1] : "");
    } else {
      user.setFirstName(email.split("@")[0]);
      user.setLastName("");
    }

    User savedUser = userRepository.save(user);
    log.info("User saved successfully - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
    return savedUser;
  }

  @Transactional
  private User updateExistingUser(
      User existingUser,
      String registrationId,
      OAuth2UserInfo oAuth2UserInfo
  ) {
    log.info("Updating existing user - ID: {}, Email: {}", existingUser.getId(), existingUser.getEmail());

    if (!StringUtils.hasText(existingUser.getProvider())) {
      existingUser.setProvider(registrationId);
      existingUser.setProviderId(oAuth2UserInfo.getId());
    } else if (StringUtils.hasText(oAuth2UserInfo.getId())) {
      existingUser.setProviderId(oAuth2UserInfo.getId());
    }

    existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());

    String name = oAuth2UserInfo.getName();
    if (StringUtils.hasText(name)) {
      String[] parts = name.split(" ", 2);
      existingUser.setFirstName(parts[0]);
      existingUser.setLastName(parts.length > 1 ? parts[1] : "");
    }

    User savedUser = userRepository.save(existingUser);
    log.info("User updated successfully - ID: {}", savedUser.getId());
    return savedUser;
  }

  private String fetchGithubPrimaryEmail(String accessToken) {
    try {
      List<Map<String, Object>> emails = restClient.get()
          .uri(GITHUB_EMAILS_API)
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/vnd.github+json")
          .retrieve()
          .body(new ParameterizedTypeReference<>() {});

      if (emails == null || emails.isEmpty()) {
        return null;
      }

      for (Map<String, Object> entry : emails) {
        if (Boolean.TRUE.equals(entry.get("primary")) && Boolean.TRUE.equals(entry.get("verified"))) {
          Object value = entry.get("email");
          if (value instanceof String str && StringUtils.hasText(str)) {
            return str;
          }
        }
      }

      for (Map<String, Object> entry : emails) {
        if (Boolean.TRUE.equals(entry.get("verified"))) {
          Object value = entry.get("email");
          if (value instanceof String str && StringUtils.hasText(str)) {
            return str;
          }
        }
      }

      for (Map<String, Object> entry : emails) {
        Object value = entry.get("email");
        if (value instanceof String str && StringUtils.hasText(str)) {
          return str;
        }
      }
    } catch (Exception ex) {
      log.warn("Unable to fetch GitHub email from {}: {}", GITHUB_EMAILS_API, ex.getMessage());
    }

    return null;
  }
}
