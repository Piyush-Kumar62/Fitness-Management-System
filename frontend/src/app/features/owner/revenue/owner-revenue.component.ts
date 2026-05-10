import { Component, OnInit, inject, signal } from '@angular/core';

import { OwnerService } from '../../../core/services/owner.service';

@Component({
  selector: 'app-owner-revenue',
  standalone: true,
  imports: [],
  templateUrl: './owner-revenue.component.html',
})
export class OwnerRevenueComponent implements OnInit {
  private ownerService = inject(OwnerService);

  isLoading = signal(false);
  summary = signal<any>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.ownerService.getRevenueSummary().subscribe({
      next: (res) => this.summary.set(res),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  asInr(value: number): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value || 0);
  }
}
