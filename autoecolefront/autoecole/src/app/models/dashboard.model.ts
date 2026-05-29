// Matches backend: DashboardResponse record (admin)
export interface DashboardStats {
  totalStudents: number;
  activeStudents: number;
  totalUsers: number;
  newStudentsThisMonth: number;
  completedStudents: number;
  suspendedStudents: number;
  enrollmentLabels: string[];   // ["janv. 2026", ...]
  enrollmentData: number[];     // [3, 5, ...]
  cashPayments: number;
  cardPayments: number;
  transferPayments: number;
  scheduledSessions: number;
  completedSessions: number;
  cancelledSessions: number;
}

// Matches backend: StaffDashboardResponse record (staff)
export interface StaffDashboardStats {
  totalStudents: number;
  activeStudents: number;
  scheduledSessions: number;
  completedSessions: number;
  cancelledSessions: number;
  codeSessions: number;
  conduiteSessions: number;
  paidPayments: number;
  pendingPayments: number;
  partialPayments: number;
}
