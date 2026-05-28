export class Payment {
  id?: number;
  studentId: number = 0;
  studentName?: string;
  amount: number = 0;
  paidAt: string = '';
  method: string = 'CASH';
  status: string = 'PAID';
  description?: string;
  recordedBy?: string;

  static empty(): Payment {
    return Object.assign(new Payment(), {
      studentId: 0, amount: 0,
      paidAt: new Date().toISOString().slice(0, 10),
      method: 'CASH', status: 'PAID', description: ''
    });
  }
}
