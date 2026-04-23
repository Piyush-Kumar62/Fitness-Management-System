package com.project.fitness.domain.user.model;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.fitness.model.Recommendation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(unique = true, nullable = false)
  private String email;
  private String password;
  private String firstName;
  private String lastName;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private AccountStatus status = AccountStatus.APPROVED;

  // Trainer assignment — which trainer a member is assigned to
  private String trainerId;

  // Tenant isolation key
  private String gymId;

  // Account status fields
  @Builder.Default
  private boolean emailVerified = false;
  @Builder.Default
  private boolean profileComplete = false;
  @Builder.Default
  private boolean active = true;

  // Extended profile fields
  private LocalDate dob;
  private String gender;
  private String phone;

  // OAuth2 fields
  private String provider; // "google", "github", "local"
  private String providerId; // OAuth2 provider user ID
  private String profileImageUrl;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @NotAudited
  private List<Activity> activities = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @SuppressWarnings("java:S3437") // Lombok @Builder.Default + Envers @NotAudited on sibling field – IDE false positive
  @Builder.Default
  @NotAudited
  private List<Recommendation> recommendations = new ArrayList<>();

  public UserRole getEffectiveRole() {
    return role == null ? UserRole.MEMBER : role;
  }
}
