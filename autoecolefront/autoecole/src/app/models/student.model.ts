// Matches backend: StudentResponse record
export interface Student {
  id?: number;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  dateOfBirth?: string;   // LocalDate  → "YYYY-MM-DD"
  address?: string;
  status: string;         // ACTIVE | COMPLETED | SUSPENDED
  registeredAt?: string;  // LocalDateTime → ISO string
  registeredBy?: string | null;
}
