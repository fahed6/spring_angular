// Matches backend: UserResponse record
export interface UserInfo {
  id?: number;
  fullName: string;
  email: string;
  password?: string;      // only sent on create / update, never returned
  role: string;           // ADMIN | STAFF
  isActive?: boolean;
  createdAt?: string;     // LocalDateTime → ISO string
}
