export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  trainerId?: string;
  gymId?: string;
  active?: boolean;
  emailVerified?: boolean;
  profileComplete?: boolean;
  createdAt?: string;
  updatedAt?: string;
  profileImageUrl?: string;
}

export enum UserRole {
  MEMBER = 'MEMBER',
  TRAINER = 'TRAINER',
  OWNER = 'OWNER',
  ADMIN = 'ADMIN',
}

export interface UserProfile extends User {
  phoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  avatar?: string;
}
