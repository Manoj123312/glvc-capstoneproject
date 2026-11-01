import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card narrow">
      <h2>Create account</h2>
      <form (ngSubmit)="submit()">
        <label>Name</label><input name="name" [(ngModel)]="f.name" required />
        <label>Email</label><input name="email" type="email" [(ngModel)]="f.email" required />
        <label>Password (min 6)</label><input name="password" type="password" [(ngModel)]="f.password" required minlength="6" />
        <label>Phone</label><input name="phone" [(ngModel)]="f.phone" />
        <label>Address</label><input name="address" [(ngModel)]="f.address" />
        @if (error) { <p class="error">{{ error }}</p> }
        <button class="btn" type="submit" [disabled]="loading">Register</button>
      </form>
      <p class="muted">Already registered? <a routerLink="/login">Login</a></p>
    </div>
  `
})
export class RegisterComponent {
  f = { name: '', email: '', password: '', phone: '', address: '' };
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.loading = true;
    this.error = '';
    this.auth.register(this.f).subscribe({
      next: () => this.router.navigate(['/']),
      error: e => { this.loading = false; this.error = errorMessage(e); }
    });
  }
}
