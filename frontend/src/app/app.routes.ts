import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/user/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/auth/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/auth/register.component').then(m => m.RegisterComponent) },
  { path: 'cart', canActivate: [authGuard], loadComponent: () => import('./pages/user/cart.component').then(m => m.CartComponent) },
  { path: 'orders', canActivate: [authGuard], loadComponent: () => import('./pages/user/orders.component').then(m => m.OrdersComponent) },
  { path: 'profile', canActivate: [authGuard], loadComponent: () => import('./pages/user/profile.component').then(m => m.ProfileComponent) },
  {
    path: 'admin', canActivate: [adminGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'restaurants' },
      { path: 'restaurants', loadComponent: () => import('./pages/admin/admin-restaurants.component').then(m => m.AdminRestaurantsComponent) },
      { path: 'dishes', loadComponent: () => import('./pages/admin/admin-dishes.component').then(m => m.AdminDishesComponent) },
      { path: 'users', loadComponent: () => import('./pages/admin/admin-users.component').then(m => m.AdminUsersComponent) },
      { path: 'orders', loadComponent: () => import('./pages/admin/admin-orders.component').then(m => m.AdminOrdersComponent) }
    ]
  },
  { path: '**', redirectTo: '' }
];
