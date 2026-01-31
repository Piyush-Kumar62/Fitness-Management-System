import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

interface AnalyticsData {
  label: string;
  value: number;
  change: number;
  trend: 'up' | 'down';
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss'],
})
export class AnalyticsComponent implements OnInit {
  metrics = signal<AnalyticsData[]>([]);
  isLoading = signal(true);

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading.set(true);

    // Mock data
    setTimeout(() => {
      this.metrics.set([
        { label: 'Active Users', value: 342, change: 12.5, trend: 'up' },
        { label: 'New Sign-ups', value: 47, change: 8.3, trend: 'up' },
        { label: 'Total Activities', value: 8567, change: 15.7, trend: 'up' },
        { label: 'Avg. Session Time', value: 24, change: -3.2, trend: 'down' },
      ]);
      this.isLoading.set(false);
    }, 1000);
  }

  formatNumber(num: number): string {
    return num.toLocaleString();
  }
}
