import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OwnerService } from '../../../core/services/owner.service';
import { User } from '../../../core/models/user.model';

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
}
