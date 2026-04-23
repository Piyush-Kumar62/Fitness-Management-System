package com.project.fitness.domain.fitness.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "diet_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String trainerId;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000)
  private String description;

  private int targetCalories;
  private int targetProtein;  // grams
  private int targetCarbs;    // grams
  private int targetFat;      // grams

  @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<DietMeal> meals = new ArrayList<>();

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
