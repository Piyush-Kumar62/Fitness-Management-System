export interface ApiResponse<T = any> {
  data?: T;
  message?: string;
  success: boolean;
  timestamp?: string;
  errors?: Record<string, string[]>;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ErrorResponse {
  message: string;
  status: number;
  timestamp: string;
  path?: string;
  errors?: Record<string, string[]>;
}
