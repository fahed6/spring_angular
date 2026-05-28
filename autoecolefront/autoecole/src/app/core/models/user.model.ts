export class UserInfo {
  id?: number;
  fullName: string = '';
  email: string = '';
  password?: string = '';
  role: string = 'STAFF';
  isActive?: boolean = true;
  createdAt?: string;

  static empty(): UserInfo {
    return Object.assign(new UserInfo(), {
      fullName: '', email: '', password: '', role: 'STAFF', isActive: true
    });
  }
}
