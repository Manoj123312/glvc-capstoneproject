import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../config';
import { AuthResponse } from '../models';

const KEY = 'foodie_auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _session = signal<AuthResponse | null>(this.restore());
  readonly session = this._session.asReadonly();
  readonly isLoggedIn = computed(() => !!this._session());
  readonly isAdmin = computed(() => this._session()?.role === 'ADMIN');

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, { email, password }).pipe(tap(r => this.save(r)));
  }

  register(body: { name: string; email: string; password: string; phone?: string; address?: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_URL}/auth/register`, body).pipe(tap(r => this.save(r)));
  }

  logout(): void {
    localStorage.removeItem(KEY);
    this._session.set(null);
    this.router.navigate(['/login']);
  }

  get token(): string | null { return this._session()?.token ?? null; }

  private save(r: AuthResponse): void {
    localStorage.setItem(KEY, JSON.stringify(r));
    this._session.set(r);
  }

  private restore(): AuthResponse | null {
    try { return JSON.parse(localStorage.getItem(KEY) ?? 'null'); } catch { return null; }
  }
}
