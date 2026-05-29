// Matches backend: PlanningResponse record
export interface Planning {
  id?: number;
  studentId: number;
  studentName?: string;
  instructorId?: number;
  instructorName?: string;
  scheduledAt: string;    // LocalDateTime → "YYYY-MM-DDTHH:mm:ss"
  type: string;           // CODE | CONDUITE
  status: string;         // SCHEDULED | COMPLETED | CANCELLED
  notes?: string;
  createdAt?: string;
}
