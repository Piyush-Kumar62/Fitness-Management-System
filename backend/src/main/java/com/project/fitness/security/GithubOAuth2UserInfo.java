package com.project.fitness.security;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

  public GithubOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  @Override
  public String getName() {
    String name = (String) attributes.get("name");
    // Fallback to login username if name is not provided
    if (name == null || name.trim().isEmpty()) {
      name = (String) attributes.get("login");
    }
    return name;
  }

  @Override
  public String getEmail() {
    // GitHub often omits email on /user when private.
    // The service layer fetches verified emails from /user/emails using access token.
    return (String) attributes.get("email");
  }

  @Override
  public String getImageUrl() {
    return (String) attributes.get("avatar_url");
  }
}
