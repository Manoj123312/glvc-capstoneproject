import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ORDER_STATUSES, Order, OrderStatus } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { OrderService } from '../../core/services/api.services';

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [FormsModule, DatePipe],
  template: `
    <h2>All orders</h2>
    @if (error) { <p class="error">{{ error }}</p> }
    <table>
      <tr><th>#</th><th>Customer</th><th>Items</th><th>Total</th><th>Placed</th><th>Status</th></tr>
      @for (o of orders; track o.id) {
        <tr>
          <td>{{ o.id }}</td><td>{{ o.userName }}</td>
          <td>@for (i of o.items; track $index) { <div>{{ i.quantity }} × {{ i.dishName }}</div> }</td>
          <td>₹{{ o.totalAmount }}</td>
          <td>{{ o.createdAt | date: 'short' }}</td>
          <td>
            <select [ngModel]="o.status" (ngModelChange)="setStatus(o, $event)">
              @for (s of statuses; track s) { <option [value]="s">{{ s }}</option> }
            </select>
          </td>
        </tr>
      }
    </table>
  `
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  statuses = ORDER_STATUSES;
  error = '';

  constructor(private svc: OrderService) {}

  ngOnInit(): void { this.load(); }

  load(): void { this.svc.allOrders().subscribe(o => this.orders = o); }

  setStatus(o: Order, status: OrderStatus): void {
    this.svc.updateStatus(o.id, status).subscribe({ next: () => this.load(), error: e => this.error = errorMessage(e) });
  }
}
