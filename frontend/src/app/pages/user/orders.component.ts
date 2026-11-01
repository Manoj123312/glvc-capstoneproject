import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Order } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { OrderService } from '../../core/services/api.services';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [DatePipe],
  template: `
    <h2>My order history</h2>
    @if (error) { <p class="error">{{ error }}</p> }
    @if (orders.length === 0) { <p class="muted">You have not placed any orders yet.</p> }
    @for (o of orders; track o.id) {
      <div class="card">
        <div class="row">
          <strong>Order #{{ o.id }}</strong>
          <span class="badge {{ o.status }}">{{ o.status.replaceAll('_', ' ') }}</span>
        </div>
        <p class="muted small">{{ o.createdAt | date: 'medium' }} · {{ o.deliveryAddress }}</p>
        <ul>
          @for (i of o.items; track $index) { <li>{{ i.quantity }} × {{ i.dishName }} ({{ i.restaurantName }}) — ₹{{ i.unitPrice * i.quantity }}</li> }
        </ul>
        <div class="row">
          <strong>Total ₹{{ o.totalAmount }}</strong>
          @if (o.status === 'PLACED' || o.status === 'CONFIRMED') {
            <button class="btn danger" (click)="cancel(o)">Cancel order</button>
          }
        </div>
      </div>
    }
  `
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  error = '';

  constructor(private svc: OrderService) {}

  ngOnInit(): void { this.load(); }

  load(): void { this.svc.myOrders().subscribe(o => this.orders = o); }

  cancel(o: Order): void {
    this.svc.cancel(o.id).subscribe({ next: () => this.load(), error: e => this.error = errorMessage(e) });
  }
}
