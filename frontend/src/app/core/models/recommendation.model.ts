export interface Recommendation {
  id: string;
  userId: string;
  activityId: string;
  type: string;
  recommendation: string;
  improvements?: string[];
  suggestions?: string[];
  safety?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateRecommendationRequest {
  activityId: string;
  type: string;
  recommendation: string;
  improvements?: string[];
  suggestions?: string[];
  safety?: string[];
}

export interface GenerateRecommendationRequest {
  activityId: string;
  focusArea?: 'performance' | 'safety' | 'nutrition' | 'recovery';
}
