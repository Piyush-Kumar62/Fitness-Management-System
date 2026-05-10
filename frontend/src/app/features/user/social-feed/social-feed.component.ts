import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocialService } from '../../../core/services/social.service';
import { Activity } from '../../../core/models/activity.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-social-feed',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './social-feed.component.html',
})
export class SocialFeedComponent implements OnInit {
  private socialService = inject(SocialService);
  private toastService = inject(ToastService);

  feedItems = signal<any[]>([]);
  isLoading = signal(false);

  ngOnInit() {
    this.loadFeed();
  }

  loadFeed() {
    this.isLoading.set(true);
    this.socialService.getFeed().subscribe({
      next: (items) => {
        this.feedItems.set(items);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load feed:', error);
        this.toastService.error('Failed to load social feed');
        this.isLoading.set(false);
      }
    });
  }

  formatDate(date: string | Date | undefined): string {
    if (!date) return '';
    return new Date(date).toLocaleString();
  }
}
