package com.project.fitness.security;

import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.model.User;
import com.project.fitness.model.UserRole;
import com.project.fitness.repository.UserRepository;
import java.util.List;
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
      throw new OAuth2AuthenticationException(ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
        registrationId,
        oauth2User.getAttributes()
    );

    if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
      throw new BadRequestException("Email not found from OAuth2 provider");
    }

    String email = oAuth2UserInfo.getEmail().toLowerCase();
    User user = userRepository.findByEmail(email);

    if (user != null) {
      if (!user.getProvider().equals(registrationId)) {
        throw new BadRequestException(
            "You're already registered with " + user.getProvider() + 
            ". Please use that account to sign in."
        );
      }
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      user = registerNewUser(registrationId, oAuth2UserInfo);
    }

    return new DefaultOAuth2User(
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
        oauth2User.getAttributes(),
        "email"
    );
  }

  @Transactional
  private User registerNewUser(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
    User user = new User();
    user.setProvider(registrationId);
    user.setProviderId(oAuth2UserInfo.getId());
    user.setEmail(oAuth2UserInfo.getEmail().toLowerCase());
    user.setRole(UserRole.USER);
    user.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
    user.setPassword(null);

    String name = oAuth2UserInfo.getName();
    if (StringUtils.hasText(name)) {
      String[] parts = name.split(" ", 2);
      user.setFirstName(parts[0]);
      user.setLastName(parts.length > 1 ? parts[1] : "");
    } else {
      user.setFirstName(oAuth2UserInfo.getEmail().split("@")[0]);
      user.setLastName("");
    }

    return userRepository.save(user);
  }

  @Transactional
  private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
    existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());

    String name = oAuth2UserInfo.getName();
    if (StringUtils.hasText(name)) {
      String[] parts = name.split(" ", 2);
      existingUser.setFirstName(parts[0]);
      existingUser.setLastName(parts.length > 1 ? parts[1] : "");
    }

    return userRepository.save(existingUser);
  }
}
