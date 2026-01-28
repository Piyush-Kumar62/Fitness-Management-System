import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (isLoading()) {
      <div
        class="loading-overlay fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      >
        <div class="loading-spinner">
          <div class="spinner"></div>
          <p class="text-white mt-4 text-sm font-medium">Loading...</p>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .loading-overlay {
        animation: fadeIn 0.2s ease;
      }

      .spinner {
        width: 50px;
        height: 50px;
        border: 4px solid rgba(255, 255, 255, 0.3);
        border-top-color: #fff;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
      }

      @keyframes fadeIn {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }

      @keyframes spin {
        to {
          transform: rotate(360deg);
        }
      }
    `,
  ],
})
export class LoadingSpinnerComponent {
  private loadingService = inject(LoadingService);
  isLoading = this.loadingService.isLoading;
}
