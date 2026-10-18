type UserRole = 'Admin' | 'Operator'
export type User = { id: number; name: string; email: string; active: boolean; role: UserRole }
