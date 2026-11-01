import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { CartService } from '../../core/services/cart.service';
import { OrderService, UserService } from '../../core/services/api.services';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <h2>Your cart</h2>
    @if (cart.items().length === 0) {
      <p class="muted">Your cart is empty. <a routerLink="/">Browse the menu</a></p>
    } @else {
      <table>
        <tr><th>Dish</th><th>Price</th><th>Qty</th><th>Subtotal</th><th></th></tr>
        @for (i of cart.items(); track i.dish.id) {
          <tr>
            <td>{{ i.dish.name }}<br /><span class="muted small">{{ i.dish.restaurant?.name }}</span></td>
            <td>₹{{ i.dish.price }}</td>
            <td><input class="qty" type="number" min="1" [ngModel]="i.quantity" (ngModelChange)="cart.setQuantity(i.dish.id, $event)" /></td>
            <td>₹{{ i.dish.price * i.quantity }}</td>
            <td><button class="btn danger" (click)="cart.remove(i.dish.id)">Remove</button></td>
          </tr>
        }
      </table>
      <h3>Total: ₹{{ cart.total() }}</h3>
      <div class="card narrow">
        <label>Delivery address</label>
        <input [(ngModel)]="address" name="address" />
        @if (error) { <p class="error">{{ error }}</p> }
        <button class="btn" (click)="checkout()" [disabled]="loading">Place order</button>
      </div>
    }
  `
})
export class CartComponent {
  address = '';
  error = '';
  loading = false;

  constructor(public cart: CartService, private orders: OrderService, private users: UserService, private router: Router) {
    this.users.me().subscribe(u => this.address = u.address ?? '');
  }

  checkout(): void {
    if (!this.address.trim()) { this.error = 'Please enter a delivery address'; return; }
    this.loading = true;
    this.error = '';
    const items = this.cart.items().map(i => ({ dishId: i.dish.id!, quantity: i.quantity }));
    this.orders.place(this.address, items).subscribe({
      next: () => { this.cart.clear(); this.router.navigate(['/orders']); },
      error: e => { this.loading = false; this.error = errorMessage(e); }
    });
  }
}
