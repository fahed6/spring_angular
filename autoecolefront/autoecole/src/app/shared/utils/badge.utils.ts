// Centralised status → CSS class mapping.
// Import wherever a status badge is rendered instead of redefining inline.

export function studentStatusClass(status: string): string {
  return ({
    ACTIVE:    'bg-green-50 text-green-800 border-green-200',
    COMPLETED: 'bg-blue-50 text-blue-800 border-blue-200',
    SUSPENDED: 'bg-red-50 text-red-800 border-red-200',
  } as Record<string, string>)[status] ?? 'bg-slate-50 text-slate-600 border-slate-200';
}

export function sessionStatusClass(status: string): string {
  return ({
    SCHEDULED: 'bg-sky-50 text-sky-800 border-sky-200',
    COMPLETED: 'bg-green-50 text-green-800 border-green-200',
    CANCELLED: 'bg-red-50 text-red-800 border-red-200',
  } as Record<string, string>)[status] ?? 'bg-slate-50 text-slate-600 border-slate-200';
}

export function sessionTypeClass(type: string): string {
  return type === 'CODE'
    ? 'bg-purple-50 text-purple-800 border-purple-200'
    : 'bg-blue-50 text-blue-800 border-blue-200';
}

export function paymentStatusClass(status: string): string {
  return ({
    PAID:    'bg-green-50 text-green-800 border-green-200',
    PENDING: 'bg-amber-50 text-amber-800 border-amber-200',
    PARTIAL: 'bg-orange-50 text-orange-800 border-orange-200',
  } as Record<string, string>)[status] ?? 'bg-slate-50 text-slate-600 border-slate-200';
}

export function userRoleClass(role: string): string {
  return role === 'ADMIN'
    ? 'bg-purple-50 text-purple-800 border-purple-200'
    : 'bg-blue-50 text-blue-800 border-blue-200';
}

export function userActiveClass(active: boolean): string {
  return active
    ? 'bg-green-50 text-green-800 border-green-200'
    : 'bg-slate-50 text-slate-600 border-slate-200';
}
