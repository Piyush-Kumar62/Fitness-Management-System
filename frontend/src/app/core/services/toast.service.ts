import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  duration?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toasts = signal<Toast[]>([]);
  private toastCounter = 0;

  /**
   * Show success toast
   */
  success(message: string, duration: number = 5000): void {
    this.show(message, 'success', duration);
  }

  /**
   * Show error toast
   */
  error(message: string, duration: number = 5000): void {
    this.show(message, 'error', duration);
  }

  /**
   * Show warning toast
   */
  warning(message: string, duration: number = 5000): void {
    this.show(message, 'warning', duration);
  }

  /**
   * Show info toast
   */
  info(message: string, duration: number = 5000): void {
    this.show(message, 'info', duration);
  }

  /**
   * Show generic toast
   */
  private show(message: string, type: Toast['type'], duration: number): void {
    const id = `toast-${++this.toastCounter}`;
    const toast: Toast = { id, message, type, duration };

    this.toasts.update((toasts) => [...toasts, toast]);

    if (duration > 0) {
      setTimeout(() => this.remove(id), duration);
    }
  }

  /**
   * Remove toast by ID
   */
  remove(id: string): void {
    this.toasts.update((toasts) => toasts.filter((t) => t.id !== id));
  }

  /**
   * Clear all toasts
   */
  clear(): void {
    this.toasts.set([]);
  }
}
