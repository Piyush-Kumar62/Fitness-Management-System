package com.project.fitness.domain.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "workout_exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "workout_plan_id", nullable = false)
  @JsonIgnore
  private WorkoutPlan workoutPlan;

  @Column(nullable = false)
  private String name;

  private int sets;
  private int reps;
  private int durationMinutes;
  @Column(name = "workout_day")
  private String day; // e.g., "Monday", "Day 1"
  private int restSeconds;
  private String notes;
}
