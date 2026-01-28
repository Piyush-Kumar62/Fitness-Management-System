import { JwtPayload } from '../models/auth.model';

export class JwtUtil {
  /**
   * Decode JWT token (without verification - done by backend)
   */
  static decode(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        return null;
      }

      const payload = parts[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch (error) {
      console.error('JWT decode error:', error);
      return null;
    }
  }

  /**
   * Check if token is expired
   */
  static isExpired(token: string): boolean {
    const payload = this.decode(token);
    if (!payload || !payload.exp) {
      return true;
    }

    const expirationDate = new Date(payload.exp * 1000);
    return expirationDate <= new Date();
  }

  /**
   * Get token expiration date
   */
  static getExpirationDate(token: string): Date | null {
    const payload = this.decode(token);
    if (!payload || !payload.exp) {
      return null;
    }

    return new Date(payload.exp * 1000);
  }

  /**
   * Get time until token expires (in milliseconds)
   */
  static getTimeUntilExpiry(token: string): number {
    const expirationDate = this.getExpirationDate(token);
    if (!expirationDate) {
      return 0;
    }

    return Math.max(0, expirationDate.getTime() - Date.now());
  }

  /**
   * Check if token needs refresh (within 5 minutes of expiry)
   */
  static needsRefresh(token: string, thresholdMs: number = 300000): boolean {
    const timeUntilExpiry = this.getTimeUntilExpiry(token);
    return timeUntilExpiry > 0 && timeUntilExpiry < thresholdMs;
  }

  /**
   * Extract user role from token
   */
  static getRole(token: string): string | null {
    const payload = this.decode(token);
    return payload?.role || null;
  }

  /**
   * Extract user ID from token
   */
  static getUserId(token: string): string | null {
    const payload = this.decode(token);
    return payload?.sub || null;
  }

  /**
   * Extract user email from token
   */
  static getEmail(token: string): string | null {
    const payload = this.decode(token);
    return payload?.email || null;
  }
}
