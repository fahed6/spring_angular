// Matches backend: PaymentResponse record
export interface Payment {
  id?: number;
  studentId: number;
  studentName?: string;
  amount: number;           // BigDecimal → number
  paidAt?: string;          // LocalDate → "YYYY-MM-DD"
  method: string;           // CASH | CARD | TRANSFER
  status: string;           // PAID | PENDING | PARTIAL
  description?: string;
  recordedBy?: string | null;
  createdAt?: string;
}
