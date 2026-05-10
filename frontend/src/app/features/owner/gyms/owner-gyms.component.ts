import { Component, OnInit, inject, signal } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { GymService } from '../../../core/services/gym.service';
import { ToastService } from '../../../core/services/toast.service';
import { GymInfo } from '../../../core/models/subscription.model';

@Component({
  selector: 'app-owner-gyms',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './owner-gyms.component.html',
})
export class OwnerGymsComponent implements OnInit {
  private gymService = inject(GymService);
  private toast = inject(ToastService);

  gyms = signal<GymInfo[]>([]);
  isLoading = signal(false);
  isSubmitting = signal(false);
  editingGymId = signal<string | null>(null);

  form = {
    name: '',
    address: '',
    contact: '',
  };

  editForm = {
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

  startEdit(gym: GymInfo): void {
    this.editingGymId.set(gym.id);
    this.editForm = {
      name: gym.name ?? '',
      address: gym.address ?? '',
      contact: gym.contact ?? '',
    };
  }

  cancelEdit(): void {
    this.editingGymId.set(null);
    this.editForm = { name: '', address: '', contact: '' };
  }

  saveEdit(gymId: string): void {
    if (!this.editForm.name.trim() || !this.editForm.address.trim() || !this.editForm.contact.trim()) {
      this.toast.warning('Please fill all gym details');
      return;
    }

    this.gymService
      .updateGym(gymId, {
        name: this.editForm.name.trim(),
        address: this.editForm.address.trim(),
        contact: this.editForm.contact.trim(),
      })
      .subscribe({
        next: (updatedGym) => {
          this.toast.success('Gym updated successfully');
          this.gyms.update((current) =>
            current.map((gym) => (gym.id === gymId ? updatedGym : gym)),
          );
          this.cancelEdit();
        },
        error: () => this.toast.error('Failed to update gym'),
      });
  }
}
