import { User, UserRole } from './user.model';

// Backend DTOs - Match Spring Boot exactly
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: UserRole;
  gymId?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface JwtPayload {
  sub: string; // User ID
  roles?: string[];
  iat: number; // Issued at
  exp: number; // Expiration
}
