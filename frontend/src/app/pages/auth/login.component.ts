import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card narrow">
      <h2>Login</h2>
      <form (ngSubmit)="submit()">
        <label>Email</label>
        <input name="email" type="email" [(ngModel)]="email" required />
        <label>Password</label>
        <input name="password" type="password" [(ngModel)]="password" required />
        @if (error) { <p class="error">{{ error }}</p> }
        <button class="btn" type="submit" [disabled]="loading">Login</button>
      </form>
      <p class="muted">New here? <a routerLink="/register">Create an account</a></p>
      <p class="muted small">Demo: admin&#64;foodie.com / admin123 &nbsp;|&nbsp; user&#64;foodie.com / user123</p>
    </div>
  `
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.loading = true;
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: r => this.router.navigate([r.role === 'ADMIN' ? '/admin' : '/']),
      error: e => { this.loading = false; this.error = e.status === 401 ? 'Invalid email or password' : errorMessage(e); }
    });
  }
}
