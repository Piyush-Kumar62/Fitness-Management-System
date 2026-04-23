import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GymService } from '../../../core/services/gym.service';
import { ToastService } from '../../../core/services/toast.service';
import { GymInfo } from '../../../core/models/subscription.model';

@Component({
  selector: 'app-owner-gyms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './owner-gyms.component.html',
})
export class OwnerGymsComponent implements OnInit {
  private gymService = inject(GymService);
  private toast = inject(ToastService);

  gyms = signal<GymInfo[]>([]);
  isLoading = signal(false);
  isSubmitting = signal(false);

  form = {
    name: '',
    address: '',
    contact: '',
  };

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    this.gymService.getMyGyms().subscribe({
      next: (rows) => this.gyms.set(rows),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  createGym(): void {
    if (!this.form.name.trim() || !this.form.address.trim() || !this.form.contact.trim()) {
      this.toast.warning('Please fill all gym details');
      return;
    }
    this.isSubmitting.set(true);
    this.gymService
      .createGym({
        name: this.form.name.trim(),
        address: this.form.address.trim(),
        contact: this.form.contact.trim(),
      })
      .subscribe({
        next: () => {
          this.toast.success('Gym created successfully');
          this.form = { name: '', address: '', contact: '' };
          this.load();
        },
        error: () => {
          this.toast.error('Failed to create gym');
          this.isSubmitting.set(false);
        },
        complete: () => this.isSubmitting.set(false),
      });
  }

  async deleteGym(gymId: string): Promise<void> {
    const confirmed = await this.toast.confirm('Delete Gym', 'This action cannot be undone.', 'Delete');
    if (!confirmed) {
      return;
    }
    this.gymService.deleteGym(gymId).subscribe({
      next: () => {
        this.toast.success('Gym deleted');
        this.load();
      },
      error: () => this.toast.error('Failed to delete gym'),
    });
  }
}
