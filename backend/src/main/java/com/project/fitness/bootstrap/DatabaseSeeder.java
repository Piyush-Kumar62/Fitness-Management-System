package com.project.fitness.bootstrap;

import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.gym.model.GymStatus;
import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Bootstraps essential data on startup. Auto-provisions a default ADMIN user if none exists in the database.
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final GymRepository gymRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    log.info("Ensuring demo users for all roles are present...");

    String adminEmail = ensureDemoUser("admin@fitness.com", "admin123", "System", "Admin", UserRole.ADMIN);
    String ownerEmail = ensureDemoUser("owner.demo@fitness.com", "owner123", "Demo", "Owner", UserRole.OWNER);
    String trainerEmail = ensureDemoUser("trainer.demo@fitness.com", "trainer123", "Demo", "Trainer", UserRole.TRAINER);
    String memberEmail = ensureDemoUser("member.demo@fitness.com", "member123", "Demo", "Member", UserRole.MEMBER);

    log.info("===== DEMO LOGIN CREDENTIALS =====");
    log.info("ADMIN   -> {} / admin123", adminEmail);
    log.info("OWNER   -> {} / owner123", ownerEmail);
    log.info("TRAINER -> {} / trainer123", trainerEmail);
    log.info("MEMBER  -> {} / member123", memberEmail);
    log.info("==================================");

    ensureOwnerDemoGym(ownerEmail);
  }

  private String ensureDemoUser(
      String email,
      String rawPassword,
      String firstName,
      String lastName,
      UserRole role
  ) {
    User existing = userRepository.findByEmail(email);
    if (existing == null) {
      User created = User.builder()
          .email(email)
          .password(passwordEncoder.encode(rawPassword))
          .firstName(firstName)
          .lastName(lastName)
          .role(role)
          .provider("local")
          .active(true)
          .status(AccountStatus.APPROVED)
          .emailVerified(true)
          .profileComplete(true)
          .build();
      userRepository.save(created);
      log.info("Provisioned demo user {} ({})", email, role);
      return email;
    }

    boolean updated = false;
    if (existing.getProvider() == null || !"local".equalsIgnoreCase(existing.getProvider())) {
      existing.setProvider("local");
      updated = true;
    }
    if (!existing.isActive()) {
      existing.setActive(true);
      updated = true;
    }
    if (!existing.isEmailVerified()) {
      existing.setEmailVerified(true);
      updated = true;
    }
    if (existing.getStatus() != AccountStatus.APPROVED) {
      existing.setStatus(AccountStatus.APPROVED);
      updated = true;
    }
    if (existing.getRole() != role) {
      existing.setRole(role);
      updated = true;
    }
    // Keep a deterministic known password for test/demo accounts.
    existing.setPassword(passwordEncoder.encode(rawPassword));
    updated = true;

    if (updated) {
      userRepository.save(existing);
      log.info("Updated demo user {} ({})", email, role);
    } else {
      log.info("Demo user {} ({}) already valid", email, role);
    }
    return email;
  }

  private void ensureOwnerDemoGym(String ownerEmail) {
    User owner = userRepository.findByEmail(ownerEmail);
    if (owner == null) {
      log.warn("Owner demo user not found, skipping demo gym provisioning");
      return;
    }

    boolean hasGym = !gymRepository.findByOwnerId(owner.getId()).isEmpty();
    if (hasGym) {
      return;
    }

    String baseName = "Demo Owner Gym";
    String gymName = baseName;
    int index = 2;
    while (gymRepository.existsByNameIgnoreCase(gymName)) {
      gymName = baseName + " " + index;
      index++;
    }

    Gym gym = Gym.builder()
        .name(gymName)
        .ownerId(owner.getId())
        .address("MG Road, Bengaluru")
        .contact("+91 9876543210")
        .status(GymStatus.ACTIVE)
        .build();

    gymRepository.save(gym);
    log.info("Provisioned demo gym '{}' for owner {}", gym.getName(), ownerEmail);
  }
}
