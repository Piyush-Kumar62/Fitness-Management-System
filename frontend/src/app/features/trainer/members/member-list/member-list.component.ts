import { Component, OnInit, inject, signal } from '@angular/core';

import { RouterLink } from '@angular/router';
import { TrainerService } from '../../../../core/services/trainer.service';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-member-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './member-list.component.html',
})
export class MemberListComponent implements OnInit {
  private trainerService = inject(TrainerService);
  isLoading = signal(false);
  members = signal<User[]>([]);

  ngOnInit() {
    this.loadMembers();
  }

  private loadMembers() {
    this.isLoading.set(true);
    this.trainerService.getAssignedMembers().subscribe({
      next: (members) => this.members.set(members),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }
}
