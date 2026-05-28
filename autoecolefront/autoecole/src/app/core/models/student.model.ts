export class Student {
  id?: number;
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  phone: string = '';
  dateOfBirth: string = '';
  address: string = '';
  status: string = 'ACTIVE';
  registeredAt?: string;
  registeredBy?: string;

  get fullName(): string {
    return `${this.firstName} ${this.lastName}`.trim();
  }

  static empty(): Student {
    return Object.assign(new Student(), {
      firstName: '', lastName: '', email: '', phone: '',
      dateOfBirth: '', address: '', status: 'ACTIVE'
    });
  }
}
