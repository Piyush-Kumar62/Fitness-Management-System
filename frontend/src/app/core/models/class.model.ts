export interface GymClass {
  id: string;
  gymId: string;
  trainerId: string;
  trainerName: string;
  className: string;
  startTime: string;
  endTime: string;
  capacity: number;
  bookedCount: number;
  availableSlots: number;
}

export interface ClassBooking {
  id: string;
  classId: string;
  className: string;
  memberId: string;
  memberName: string;
  status: 'BOOKED' | 'CANCELLED' | 'ATTENDED' | 'NO_SHOW';
  bookedAt: string;
}

export interface Attendance {
  id: string;
  classId: string;
  memberId: string;
  memberName: string;
  markedBy: string;
  date: string;
  status: 'PRESENT' | 'ABSENT';
}

export interface CreateClassRequest {
  className: string;
  startTime: string;
  endTime: string;
  capacity: number;
}
