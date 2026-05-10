import { Component, OnInit, inject, signal } from '@angular/core';

import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { OwnerService } from '../../../core/services/owner.service';
import { GymService } from '../../../core/services/gym.service';
import { ToastService } from '../../../core/services/toast.service';
import { User } from '../../../core/models/user.model';
import { GymInfo } from '../../../core/models/subscription.model';

@Component({
  selector: 'app-owner-trainers',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './owner-trainers.component.html',
})
export class OwnerTrainersComponent implements OnInit {
  private ownerService = inject(OwnerService);
  private gymService = inject(GymService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  trainers = signal<User[]>([]);
  gyms = signal<GymInfo[]>([]);
  isLoading = signal(false);
  isSubmitting = signal(false);
  showCreateForm = signal(false);
  
  page = signal(0);
  size = signal(10);
  totalElements = signal(0);

  trainerForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    gymId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.load();
    this.loadGyms();
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

  loadGyms(): void {
    this.gymService.getMyGyms().subscribe({
      next: (gyms) => {
        this.gyms.set(gyms);
        if (gyms.length > 0) {
          this.trainerForm.patchValue({ gymId: gyms[0].id });
        }
      }
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm.set(!this.showCreateForm());
    if (!this.showCreateForm()) {
      this.trainerForm.reset();
      if (this.gyms().length > 0) {
        this.trainerForm.patchValue({ gymId: this.gyms()[0].id });
      }
    }
  }

  onSubmit(): void {
    if (this.trainerForm.invalid) {
      this.trainerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.ownerService.createTrainer(this.trainerForm.value).subscribe({
      next: () => {
        this.toast.success('Trainer created successfully! An email with temporary credentials has been sent.');
        this.toggleCreateForm();
        this.load();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to create trainer');
      },
      complete: () => this.isSubmitting.set(false)
    });
  }

  assignTrainer(trainerId: string, event: Event): void {
    const gymId = (event.target as HTMLSelectElement).value;
    if (!gymId) return;

    this.ownerService.assignTrainerToGym(trainerId, gymId).subscribe({
      next: () => {
        this.toast.success('Trainer reassigned successfully');
        this.load();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to reassign trainer');
      }
    });
  }

  async deleteTrainer(trainerId: string): Promise<void> {
    const confirmed = await this.toast.confirm(
      'Remove Trainer',
      'Are you sure you want to remove this trainer? They will be deactivated and unassigned from the gym.',
      'Remove'
    );

    if (!confirmed) return;

    this.ownerService.deleteTrainer(trainerId).subscribe({
      next: () => {
        this.toast.success('Trainer removed successfully');
        this.load();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to remove trainer');
      }
    });
  }
}
