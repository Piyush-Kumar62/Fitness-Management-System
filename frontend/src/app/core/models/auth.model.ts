import { User } from './user.model';

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
}

export interface AuthResponse {
  token: string;
  refreshToken?: string;
  user: User;
  expiresIn?: number;
}

export interface JwtPayload {
  sub: string; // User ID
  email: string;
  role: string;
  iat: number; // Issued at
  exp: number; // Expiration
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface TokenRefreshResponse {
  token: string;
  refreshToken?: string;
}
