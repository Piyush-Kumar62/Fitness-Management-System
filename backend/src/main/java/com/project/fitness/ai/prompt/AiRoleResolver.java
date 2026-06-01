package com.project.fitness.ai.prompt;

import com.project.fitness.domain.user.model.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AiRoleResolver {

  private static final List<UserRole> ROLE_PRIORITY = List.of(
      UserRole.ADMIN,
      UserRole.OWNER,
      UserRole.TRAINER,
      UserRole.MEMBER
  );

  public UserRole resolve(Authentication authentication) {
    if (authentication == null || authentication.getAuthorities() == null) {
      return UserRole.MEMBER;
    }
    for (UserRole role : ROLE_PRIORITY) {
      Optional<GrantedAuthority> match = authentication.getAuthorities().stream()
          .filter(auth -> auth.getAuthority().equalsIgnoreCase("ROLE_" + role.name()))
          .findFirst();
      if (match.isPresent()) {
        return role;
      }
    }
    return UserRole.MEMBER;
  }
}
