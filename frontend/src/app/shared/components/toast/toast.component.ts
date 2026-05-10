import { Component, inject } from '@angular/core';

import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [],
  templateUrl: './toast.component.html',
  styles: [
    `
      .toast-item {
        animation:
          slideIn 0.3s ease,
          fadeOut 0.3s ease 4.7s;
      }

      @keyframes slideIn {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }

      @keyframes fadeOut {
        to {
          opacity: 0;
          transform: translateX(100%);
        }
      }
    `,
  ],
})
export class ToastComponent {
  private toastService = inject(ToastService);
  toasts: any[] = [];

  getColorClasses(type: string): string {
    switch (type) {
      case 'success':
        return 'bg-green-50 border-green-500 text-green-800 dark:bg-green-900/30 dark:text-green-200';
      case 'error':
        return 'bg-red-50 border-red-500 text-red-800 dark:bg-red-900/30 dark:text-red-200';
      case 'warning':
        return 'bg-yellow-50 border-yellow-500 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-200';
      default:
        return 'bg-blue-50 border-blue-500 text-blue-800 dark:bg-blue-900/30 dark:text-blue-200';
    }
  }

  remove(id: string): void {
    // Stub
  }
}
