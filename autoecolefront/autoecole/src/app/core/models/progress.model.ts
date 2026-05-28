export class Progress {
  id?: number;
  studentId: number = 0;
  studentName?: string;
  category: string = 'CODE';
  hoursCompleted: number = 0;
  hoursRequired: number = 20;
  score?: number;
  updatedAt?: string;
  updatedBy?: string;

  get percent(): number {
    return this.hoursRequired > 0
      ? Math.min(100, Math.round((this.hoursCompleted / this.hoursRequired) * 100))
      : 0;
  }

  static empty(): Progress {
    return Object.assign(new Progress(), {
      studentId: 0, category: 'CODE', hoursCompleted: 0, hoursRequired: 20
    });
  }
}
