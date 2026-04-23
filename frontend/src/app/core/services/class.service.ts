import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Attendance, ClassBooking, CreateClassRequest, GymClass } from '../models/class.model';

@Injectable({ providedIn: 'root' })
export class ClassService {
  private api = inject(ApiService);

  getAvailableClasses(): Observable<GymClass[]> {
    return this.api.get<GymClass[]>('member/classes/available');
  }

  bookClass(classId: string): Observable<ClassBooking> {
    return this.api.post<ClassBooking>('member/classes/book', { classId });
  }

  getMyBookings(): Observable<ClassBooking[]> {
    return this.api.get<ClassBooking[]>('member/classes/bookings');
  }

  createClass(payload: CreateClassRequest): Observable<GymClass> {
    return this.api.post<GymClass>('trainer/classes', payload);
  }

  getTrainerClasses(): Observable<GymClass[]> {
    return this.api.get<GymClass[]>('trainer/classes');
  }

  markAttendance(
    classId: string,
    memberId: string,
    status: 'PRESENT' | 'ABSENT',
  ): Observable<Attendance> {
    return this.api.post<Attendance>('trainer/classes/attendance', { classId, memberId, status });
  }

  getClassAttendance(classId: string): Observable<Attendance[]> {
    return this.api.get<Attendance[]>(`trainer/classes/${classId}/attendance`);
  }

  getClassBookings(classId: string): Observable<ClassBooking[]> {
    return this.api.get<ClassBooking[]>(`trainer/classes/${classId}/bookings`);
  }
}
