package com.project.fitness.domain.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diet_meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietMeal {

  public enum MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "diet_plan_id", nullable = false)
  @JsonIgnore
  private DietPlan dietPlan;

  @Enumerated(EnumType.STRING)
  private MealType mealType;

  @Column(nullable = false)
  private String name;

  private int calories;

  @Column(length = 500)
  private String description;
}
