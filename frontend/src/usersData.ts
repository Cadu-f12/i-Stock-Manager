type UserRole = 'Admin' | 'Operador'
export type User = { id: number; name: string; email: string; active: boolean; role: UserRole }
