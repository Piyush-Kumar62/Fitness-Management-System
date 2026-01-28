export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  createdAt?: string;
  updatedAt?: string;
}

export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export interface UserProfile extends User {
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  avatar?: string;
}
