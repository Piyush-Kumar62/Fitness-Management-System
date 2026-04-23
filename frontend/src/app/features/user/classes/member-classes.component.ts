import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClassService } from '../../../core/services/class.service';
import { ClassBooking, GymClass } from '../../../core/models/class.model';

@Component({
  selector: 'app-member-classes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './member-classes.component.html',
})
export class MemberClassesComponent implements OnInit {
  private classService = inject(ClassService);

  isLoading = signal(false);
  classes = signal<GymClass[]>([]);
  bookings = signal<ClassBooking[]>([]);

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.isLoading.set(true);
    this.classService.getAvailableClasses().subscribe((rows) => this.classes.set(rows));
    this.classService.getMyBookings().subscribe({
      next: (rows) => this.bookings.set(rows),
      complete: () => this.isLoading.set(false),
      error: () => this.isLoading.set(false),
    });
  }

  book(classId: string): void {
    this.isLoading.set(true);
    this.classService.bookClass(classId).subscribe({
      next: () => this.refresh(),
      error: () => this.isLoading.set(false),
    });
  }
}
