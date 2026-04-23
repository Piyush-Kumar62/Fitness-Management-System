import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OwnerService } from '../../../core/services/owner.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-owner-trainers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-trainers.component.html',
})
export class OwnerTrainersComponent implements OnInit {
  private ownerService = inject(OwnerService);

  trainers = signal<User[]>([]);
  isLoading = signal(false);
  page = signal(0);
  size = signal(10);
  totalElements = signal(0);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.ownerService.getTrainers(this.page(), this.size()).subscribe({
      next: (response) => {
        this.trainers.set(response.content ?? []);
        this.totalElements.set(response.totalElements ?? 0);
      },
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }
}
