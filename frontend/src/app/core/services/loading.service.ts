import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private loadingCount = 0;
  isLoading = signal<boolean>(false);

  /**
   * Show loading spinner
   */
  show(): void {
    this.loadingCount++;
    this.isLoading.set(true);
  }

  /**
   * Hide loading spinner
   */
  hide(): void {
    this.loadingCount = Math.max(0, this.loadingCount - 1);
    if (this.loadingCount === 0) {
      this.isLoading.set(false);
    }
  }

  /**
   * Force hide loading
   */
  forceHide(): void {
    this.loadingCount = 0;
    this.isLoading.set(false);
  }
}
