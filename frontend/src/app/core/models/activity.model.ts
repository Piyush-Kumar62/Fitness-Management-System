export interface Activity {
  id: string;
  userId: string;
  type: ActivityType;
  duration: number; // minutes
  caloriesBurned: number;
  date?: string;
  startTime?: string;
  distance?: number;
  intensity?: string;
  notes?: string;
  additionalMetrics?: Record<string, any>;
  createdAt?: string;
  updatedAt?: string;
}

export enum ActivityType {
  RUNNING = 'RUNNING',
  WALKING = 'WALKING',
  CYCLING = 'CYCLING',
  SWIMMING = 'SWIMMING',
  YOGA = 'YOGA',
  GYM = 'GYM',
  SPORTS = 'SPORTS',
  OTHER = 'OTHER',
}

export interface CreateActivityRequest {
  type: ActivityType;
  duration: number;
  caloriesBurned: number;
  date?: Date;
  startTime?: string;
  distance?: number;
  intensity?: string;
  notes?: string;
  additionalMetrics?: Record<string, any>;
}

export interface UpdateActivityRequest {
  type?: ActivityType;
  duration?: number;
  caloriesBurned?: number;
  startTime?: string;
  additionalMetrics?: Record<string, any>;
}

export interface ActivityStats {
  totalActivities: number;
  totalDuration: number;
  totalCalories: number;
  averageDuration: number;
  averageCalories: number;
  mostCommonType: ActivityType;
}
