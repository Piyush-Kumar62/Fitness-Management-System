import { User } from './user.model';

export interface WorkoutExercise {
  id?: string;
  name: string;
  sets?: number;
  reps?: number;
  durationMinutes?: number;
  day?: string;
  restSeconds?: number;
  notes?: string;
}

export interface WorkoutPlan {
  id: string;
  trainerId: string;
  trainerName: string;
  title: string;
  description?: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  durationWeeks: number;
  exerciseCount: number;
  exercises: WorkoutExercise[];
}

export interface DietMeal {
  id?: string;
  mealType: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
  name: string;
  calories?: number;
  description?: string;
}

export interface DietPlan {
  id: string;
  trainerId: string;
  trainerName: string;
  title: string;
  description?: string;
  targetCalories: number;
  targetProtein: number;
  targetCarbs: number;
  targetFat: number;
  mealCount: number;
  meals: DietMeal[];
}

export interface AssignPlanRequest {
  memberId: string;
  workoutPlanId?: string;
  dietPlanId?: string;
}

export interface MemberPlanResponse {
  id: string;
  memberId: string;
  memberName: string;
  workoutPlanId?: string;
  workoutPlanTitle?: string;
  dietPlanId?: string;
  dietPlanTitle?: string;
  assignedBy: string;
  assignedByName: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  assignedAt: string;
}

export interface MemberProgress {
  member: User;
  activePlans: MemberPlanResponse[];
  totalActivities: number;
}

export interface MemberCurrentPlansResponse {
  workoutPlan?: { id: string; title: string; description?: string };
  dietPlan?: { id: string; title: string; description?: string };
}
