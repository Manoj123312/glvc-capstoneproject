import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { UserService } from '../../core/services/api.services';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="card narrow">
      <h2>My profile</h2>
      @if (user) {
        <form (ngSubmit)="save()">
          <label>Email</label><input [value]="user.email" disabled />
          <label>Name</label><input name="name" [(ngModel)]="user.name" required />
          <label>Phone</label><input name="phone" [(ngModel)]="user.phone" />
          <label>Address</label><input name="address" [(ngModel)]="user.address" />
          @if (msg) { <p class="success">{{ msg }}</p> }
          @if (error) { <p class="error">{{ error }}</p> }
          <button class="btn" type="submit">Save</button>
        </form>
      }
    </div>
  `
})
export class ProfileComponent implements OnInit {
  user?: User;
  msg = '';
  error = '';

  constructor(private svc: UserService) {}

  ngOnInit(): void { this.svc.me().subscribe(u => this.user = u); }

  save(): void {
    this.msg = this.error = '';
    this.svc.updateMe(this.user!).subscribe({
      next: u => { this.user = u; this.msg = 'Profile updated'; },
      error: e => this.error = errorMessage(e)
    });
  }
}
