import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container fixed top-4 right-4 z-50 space-y-2">
      @for (toast of toasts(); track toast.id) {
        <div
          class="toast-item flex items-start space-x-3 p-4 rounded-lg border-l-4 shadow-lg min-w-[320px] max-w-md backdrop-blur-sm animate-slideIn"
          [ngClass]="getColorClasses(toast.type)"
        >
          <div class="flex-shrink-0 pt-0.5">
            <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              @switch (toast.type) {
                @case ('success') {
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M5 13l4 4L19 7"
                  ></path>
                }
                @case ('error') {
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  ></path>
                }
                @case ('warning') {
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  ></path>
                }
                @default {
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  ></path>
                }
              }
            </svg>
          </div>
          <div class="flex-1">
            <p class="text-sm font-medium">{{ toast.message }}</p>
          </div>
          <button
            (click)="remove(toast.id)"
            class="flex-shrink-0 ml-4 text-current opacity-70 hover:opacity-100 transition-opacity"
          >
            <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              ></path>
            </svg>
          </button>
        </div>
      }
    </div>
  `,
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
  toasts = this.toastService.toasts;

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
    this.toastService.remove(id);
  }
}
