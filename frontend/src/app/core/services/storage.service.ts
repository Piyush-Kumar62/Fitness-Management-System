import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export enum StorageKey {
  ACCESS_TOKEN = 'fitness_access_token',
  REFRESH_TOKEN = 'fitness_refresh_token',
  USER = 'fitness_user',
  THEME = 'fitness_theme',
  REMEMBER_ME = 'fitness_remember_me',
}

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  /**
   * Set item in localStorage
   */
  set<T>(key: StorageKey, value: T): void {
    if (!this.isBrowser) return;

    try {
      const serialized = JSON.stringify(value);
      localStorage.setItem(key, serialized);
    } catch (error) {
      console.error(`Error storing ${key}:`, error);
    }
  }

  /**
   * Get item from localStorage
   */
  get<T>(key: StorageKey): T | null {
    if (!this.isBrowser) return null;

    try {
      const item = localStorage.getItem(key);

      // Handle null, undefined, or empty values
      if (!item || item === 'null' || item === 'undefined') {
        return null;
      }

      return JSON.parse(item);
    } catch (error) {
      console.error(`Error retrieving ${key}:`, error);
      // Clear invalid data
      this.remove(key);
      return null;
    }
  }

  /**
   * Remove item from localStorage
   */
  remove(key: StorageKey): void {
    if (!this.isBrowser) return;

    try {
      localStorage.removeItem(key);
    } catch (error) {
      console.error(`Error removing ${key}:`, error);
    }
  }

  /**
   * Clear all storage
   */
  clear(): void {
    if (!this.isBrowser) return;

    try {
      localStorage.clear();
    } catch (error) {
      console.error('Error clearing storage:', error);
    }
  }

  /**
   * Check if key exists
   */
  has(key: StorageKey): boolean {
    if (!this.isBrowser) return false;
    return localStorage.getItem(key) !== null;
  }

  /**
   * Get token
   */
  getToken(): string | null {
    return this.get<string>(StorageKey.ACCESS_TOKEN);
  }

  /**
   * Set token
   */
  setToken(token: string): void {
    this.set(StorageKey.ACCESS_TOKEN, token);
  }

  /**
   * Remove token
   */
  removeToken(): void {
    this.remove(StorageKey.ACCESS_TOKEN);
    this.remove(StorageKey.REFRESH_TOKEN);
  }
}
