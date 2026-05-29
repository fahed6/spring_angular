// Matches backend: ProgressResponse record
export interface Progress {
  id?: number;
  studentId: number;
  studentName?: string;
  category: string;         // CODE | CONDUITE | GENERAL
  hoursCompleted: number;   // BigDecimal → number
  hoursRequired: number;    // BigDecimal → number
  score?: number;           // 0-100
  updatedAt?: string;
  updatedBy?: string | null;
}
