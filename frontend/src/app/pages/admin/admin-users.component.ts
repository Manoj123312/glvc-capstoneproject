import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { User } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/api.services';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [FormsModule],
  template: `
    <h2>User management</h2>
    @if (error) { <p class="error">{{ error }}</p> }
    <table>
      <tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Active</th><th></th></tr>
      @for (u of users; track u.id) {
        <tr>
          <td>{{ u.id }}</td><td>{{ u.name }}</td><td>{{ u.email }}</td><td>{{ u.phone }}</td>
          <td>
            <select [ngModel]="u.role" (ngModelChange)="changeRole(u, $event)" [disabled]="isSelf(u)">
              <option value="USER">USER</option><option value="ADMIN">ADMIN</option>
            </select>
          </td>
          <td>{{ u.active ? 'Yes' : 'Disabled' }}</td>
          <td>
            <button class="btn secondary" [disabled]="isSelf(u)" (click)="toggle(u)">{{ u.active ? 'Disable' : 'Enable' }}</button>
            <button class="btn danger" [disabled]="isSelf(u)" (click)="remove(u)">Delete</button>
          </td>
        </tr>
      }
    </table>
  `
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  error = '';

  constructor(private svc: UserService, private auth: AuthService) {}

  ngOnInit(): void { this.load(); }

  load(): void { this.svc.getAll().subscribe(u => this.users = u); }

  isSelf(u: User): boolean { return u.email === this.auth.session()?.email; }

  changeRole(u: User, role: 'ADMIN' | 'USER'): void { this.update(u, { ...u, role }); }

  toggle(u: User): void { this.update(u, { ...u, active: !u.active }); }

  private update(u: User, body: User): void {
    this.svc.update(u.id, body).subscribe({ next: () => { this.error = ''; this.load(); }, error: e => { this.error = errorMessage(e); this.load(); } });
  }

  remove(u: User): void {
    if (!confirm(`Delete user ${u.email}?`)) return;
    this.svc.delete(u.id).subscribe({ next: () => this.load(), error: e => this.error = errorMessage(e) });
  }
}
