import { User, UserRole } from './user.model';

// Backend DTOs - Match Spring Boot exactly
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  role: 'MEMBER' | 'OWNER';
  phone?: string;
  dob?: string;
  gender?: string;
  gymId?: string; // For MEMBER joining
  gymName?: string; // For OWNER creation
  gymAddress?: string; // For OWNER creation
  gymContact?: string; // For OWNER creation
}

export interface CompleteProfileRequest {
  role: UserRole;
}

export interface AuthResponse {
  token: string;
  user: User;
  passwordResetRequired?: boolean;
}

export interface JwtPayload {
  sub: string; // User ID
  roles?: string[];
  iat: number; // Issued at
  exp: number; // Expiration
}
