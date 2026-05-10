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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
    if (!"google".equalsIgnoreCase(registrationId)) {
      throw new BadRequestException("Login with " + registrationId + " is not supported. Please use Google.");
    }
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oauth2User.getAttributes());
    String email = validateAndNormalizeEmail(userInfo.getEmail(), registrationId);
    User user = resolveUser(email, registrationId, userInfo);
    Map<String, Object> attributes = buildEnrichedAttributes(oauth2User.getAttributes(), user);
    String nameKey = resolveNameAttributeKey(userRequest, attributes);
    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getEffectiveRole().name())),
        attributes, nameKey);
  }

  private String validateAndNormalizeEmail(String email, String provider) {
    if (email == null || email.trim().isEmpty()) {
      throw new BadRequestException(
          "Email not found from OAuth2 provider. Please ensure your email is public in your " + provider + " account settings.");
    }
    if (!email.contains("@") || !email.contains(".")) {
      throw new BadRequestException("Invalid email format received from OAuth2 provider");
    }
    return email.toLowerCase().trim();
  }

  private User resolveUser(String email, String registrationId, OAuth2UserInfo userInfo) {
    User user = userRepository.findByEmail(email);
    if (user != null) {
      log.info("Existing user found: {}", email);
      return updateExistingUser(user, registrationId, userInfo);
    }
    log.info("Registering new user: {}", email);
    return registerNewUser(registrationId, userInfo, email);
  }

  private Map<String, Object> buildEnrichedAttributes(Map<String, Object> original, User user) {
    Map<String, Object> enriched = new HashMap<>(original);
    enriched.put("email", user.getEmail());
    enriched.put("name", (user.getFirstName() + " " + user.getLastName()).trim());
    enriched.put("profileImageUrl", user.getProfileImageUrl());
    enriched.put("provider", user.getProvider());
    enriched.put("providerId", user.getProviderId());
    return enriched;
  }

  private String resolveNameAttributeKey(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
    String key = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    return (StringUtils.hasText(key) && attributes.containsKey(key)) ? key : "email";
  }

  @Transactional
  private User registerNewUser(String registrationId, OAuth2UserInfo userInfo, String email) {
    User user = new User();
    user.setProvider(registrationId);
    user.setProviderId(userInfo.getId());
    user.setEmail(email);
    user.setRole(null);
    user.setStatus(AccountStatus.PENDING);
    user.setEmailVerified(false);
    user.setActive(true);
    user.setProfileImageUrl(userInfo.getImageUrl());
    user.setPassword(null);
    applyNameFromOAuth2(user, userInfo.getName(), email);
    User saved = userRepository.save(user);
    log.info("Registered new user ID={}", saved.getId());
    return saved;
  }

  @Transactional
  private User updateExistingUser(User user, String registrationId, OAuth2UserInfo userInfo) {
    if (!StringUtils.hasText(user.getProvider())) {
      user.setProvider(registrationId);
      user.setProviderId(userInfo.getId());
    } else if (StringUtils.hasText(userInfo.getId())) {
      user.setProviderId(userInfo.getId());
    }
    user.setProfileImageUrl(userInfo.getImageUrl());
    applyNameFromOAuth2(user, userInfo.getName(), user.getEmail());
    return userRepository.save(user);
  }

  private void applyNameFromOAuth2(User user, String name, String email) {
    if (StringUtils.hasText(name)) {
      String[] parts = name.split(" ", 2);
      user.setFirstName(parts[0]);
      user.setLastName(parts.length > 1 ? parts[1] : "");
    } else {
      user.setFirstName(email.split("@")[0]);
      user.setLastName("");
    }
  }
}
