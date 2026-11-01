import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { CartService } from './core/services/cart.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <a class="brand" routerLink="/">🍔 Foodie</a>
      <div class="links">
        @if (auth.isAdmin()) {
          <a routerLink="/admin/restaurants" routerLinkActive="active">Restaurants</a>
          <a routerLink="/admin/dishes" routerLinkActive="active">Dishes</a>
          <a routerLink="/admin/users" routerLinkActive="active">Users</a>
          <a routerLink="/admin/orders" routerLinkActive="active">Orders</a>
        } @else {
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Menu</a>
          @if (auth.isLoggedIn()) {
            <a routerLink="/orders" routerLinkActive="active">My Orders</a>
            <a routerLink="/cart" routerLinkActive="active">Cart ({{ cart.count() }})</a>
            <a routerLink="/profile" routerLinkActive="active">Profile</a>
          }
        }
      </div>
      <div class="auth">
        @if (auth.session(); as s) {
          <span class="muted">{{ s.name }} ({{ s.role }})</span>
          <button class="btn secondary" (click)="auth.logout()">Logout</button>
        } @else {
          <a routerLink="/login" class="btn">Login</a>
          <a routerLink="/register" class="btn secondary">Register</a>
        }
      </div>
    </nav>
    <main class="container"><router-outlet /></main>
  `
})
export class AppComponent {
  constructor(public auth: AuthService, public cart: CartService) {}
}
