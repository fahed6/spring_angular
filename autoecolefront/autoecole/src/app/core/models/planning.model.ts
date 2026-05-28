export class Planning {
  id?: number;
  studentId: number = 0;
  studentName?: string;
  instructorId?: number;
  instructorName?: string;
  scheduledAt: string = '';
  type: string = 'CONDUITE';
  status: string = 'SCHEDULED';
  notes?: string;

  static empty(): Planning {
    return Object.assign(new Planning(), {
      studentId: 0, scheduledAt: '', type: 'CONDUITE', status: 'SCHEDULED', notes: ''
    });
  }
}
