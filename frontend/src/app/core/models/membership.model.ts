export interface MembershipPlan {
  id: string;
  gymId: string;
  name: string;
  price: number;
  durationDays: number;
  features?: string;
  active: boolean;
}

export interface Membership {
  id: string;
  memberId: string;
  memberName: string;
  planId: string;
  planName: string;
  startDate: string;
  endDate: string;
  status: 'PENDING_PAYMENT' | 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  autoRenew: boolean;
  createdAt: string;
}

export interface Payment {
  id: string;
  memberId: string;
  membershipId: string;
  amount: number;
  method: 'UPI' | 'CARD' | 'NETBANKING' | 'CASH';
  status: 'CREATED' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  transactionId: string;
  gateway: string;
  createdAt: string;
}

export interface MembershipPurchaseResponse {
  membership: Membership;
  payment: Payment;
}

export interface CreateMembershipPlanRequest {
  gymId: string;
  name: string;
  price: number;
  durationDays: number;
  features?: string;
}

export interface BuyMembershipRequest {
  memberId?: string;
  planId: string;
  paymentMethod: 'UPI' | 'CARD' | 'NETBANKING' | 'CASH';
}
