export interface GymSubscriptionPlan {
  id: string;
  name: string;
  monthlyPrice: number;
  maxMembers: number;
  maxTrainers: number;
  features?: string;
  active: boolean;
}

export interface GymSubscription {
  id: string;
  gymId: string;
  gymName: string;
  planId: string;
  planName: string;
  startDate: string;
  endDate: string;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  autoRenew: boolean;
}

export interface GymInfo {
  id: string;
  name: string;
}
