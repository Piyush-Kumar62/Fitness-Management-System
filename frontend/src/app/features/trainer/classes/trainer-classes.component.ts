import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Attendance, ClassBooking, GymClass } from '../../../core/models/class.model';
import { ClassService } from '../../../core/services/class.service';

@Component({
  selector: 'app-trainer-classes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './trainer-classes.component.html',
})
export class TrainerClassesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private classService = inject(ClassService);

  isLoading = signal(false);
  classes = signal<GymClass[]>([]);
  attendance = signal<Attendance[]>([]);
  bookings = signal<ClassBooking[]>([]);
  selectedClassId = signal<string | null>(null);

  form = this.fb.group({
    className: ['', [Validators.required]],
    startTime: ['', [Validators.required]],
    endTime: ['', [Validators.required]],
    capacity: [20, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.refreshClasses();
  }

  createClass(): void {
    if (this.form.invalid) {
      return;
    }
    this.isLoading.set(true);
    this.classService.createClass(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.form.patchValue({ className: '' });
        this.refreshClasses();
      },
      error: () => this.isLoading.set(false),
    });
  }

  refreshClasses(): void {
    this.classService.getTrainerClasses().subscribe({
      next: (rows) => this.classes.set(rows),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  loadClassDetails(classId: string): void {
    this.selectedClassId.set(classId);
    this.classService.getClassBookings(classId).subscribe((rows) => this.bookings.set(rows));
    this.classService.getClassAttendance(classId).subscribe((rows) => this.attendance.set(rows));
  }

  mark(memberId: string, status: 'PRESENT' | 'ABSENT'): void {
    const classId = this.selectedClassId();
    if (!classId) {
      return;
    }
    this.classService
      .markAttendance(classId, memberId, status)
      .subscribe(() => this.loadClassDetails(classId));
  }
}
