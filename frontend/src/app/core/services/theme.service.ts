import { Injectable, signal, effect, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly THEME_KEY = 'fitness_theme';
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  theme = signal<Theme>(this.getInitialTheme());

  constructor() {
    effect(() => {
      if (this.isBrowser) {
        const theme = this.theme();
        this.applyTheme(theme);
        localStorage.setItem(this.THEME_KEY, theme);
      }
    });
  }

  /**
   * Toggle between light and dark theme
   */
  toggleTheme(): void {
    this.theme.update((current) => (current === 'light' ? 'dark' : 'light'));
  }

  /**
   * Set specific theme
   */
  setTheme(theme: Theme): void {
    this.theme.set(theme);
  }

  /**
   * Get initial theme from localStorage or system preference
   */
  private getInitialTheme(): Theme {
    if (!this.isBrowser) {
      return 'light'; // Default for SSR
    }

    const stored = localStorage.getItem(this.THEME_KEY) as Theme;
    if (stored) {
      return stored;
    }

    // Check system preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }

    return 'light';
  }

  /**
   * Apply theme to document
   */
  private applyTheme(theme: Theme): void {
    if (!this.isBrowser) {
      return; // Skip for SSR
    }

    const root = document.documentElement;
    if (theme === 'dark') {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }
}
