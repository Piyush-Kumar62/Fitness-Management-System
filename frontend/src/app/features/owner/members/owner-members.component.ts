import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { OwnerService } from '../../../core/services/owner.service';
import { User } from '../../../core/models/user.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-owner-members',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-members.component.html',
})
export class OwnerMembersComponent implements OnInit {
  private ownerService = inject(OwnerService);

  members = signal<User[]>([]);
  isLoading = signal(false);
  page = signal(0);
  size = signal(10);
  totalElements = signal(0);

  totalPages = computed(() => Math.ceil(this.totalElements() / this.size()));

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.ownerService.getMembers(this.page(), this.size()).subscribe({
      next: (response) => {
        this.members.set(response.content ?? []);
        this.totalElements.set(response.totalElements ?? 0);
      },
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  nextPage(): void {
    if ((this.page() + 1) * this.size() < this.totalElements()) {
      this.page.update((p) => p + 1);
      this.load();
    }
  }

  prevPage(): void {
    if (this.page() > 0) {
      this.page.update((p) => p - 1);
      this.load();
    }
  }
}
