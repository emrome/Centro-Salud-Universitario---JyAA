import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  role: string;
  enabled: boolean;
  exp: number;
  firstName?: string;
  lastName?: string;
}

export class TokenService {
  static getToken(): string | null {
    return localStorage.getItem('token');
  }

  static setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  static clear(): void {
    localStorage.removeItem('token');
  }

  static getDecoded(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode<JwtPayload>(token);
    } catch {
      return null;
    }
  }

  static getUserId(): string | null {
    return this.getDecoded()?.sub ?? null;
  }

  static getRole(): string | null {
    return this.getDecoded()?.role ?? null;
  }

  static isEnabled(): boolean {
    return this.getDecoded()?.enabled ?? false;
  }

  static isAuthenticated(): boolean {
    const payload = this.getDecoded();
    return !!payload && payload.exp * 1000 > Date.now();
  }

  static getUserName(): string {
    const payload = this.getDecoded();
    return payload ? `${payload.firstName ?? ''} ${payload.lastName ?? ''}`.trim() : '';
  }
}

