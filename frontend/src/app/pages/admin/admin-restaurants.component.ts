import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Restaurant } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { RestaurantService } from '../../core/services/api.services';

@Component({
  selector: 'app-admin-restaurants',
  standalone: true,
  imports: [FormsModule],
  template: `
    <h2>Restaurant management</h2>
    <div class="card">
      <h3>{{ form.id ? 'Edit restaurant #' + form.id : 'Add restaurant' }}</h3>
      <form (ngSubmit)="save()" class="form-grid">
        <input name="name" placeholder="Name" [(ngModel)]="form.name" required />
        <input name="cuisine" placeholder="Cuisine" [(ngModel)]="form.cuisine" />
        <input name="city" placeholder="City" [(ngModel)]="form.city" />
        <input name="address" placeholder="Address" [(ngModel)]="form.address" />
        <input name="imageUrl" placeholder="Image URL" [(ngModel)]="form.imageUrl" />
        <input name="rating" type="number" min="0" max="5" step="0.1" placeholder="Rating" [(ngModel)]="form.rating" />
        <div>
          <button class="btn" type="submit">{{ form.id ? 'Update' : 'Add' }}</button>
          @if (form.id) { <button class="btn secondary" type="button" (click)="reset()">Cancel</button> }
        </div>
      </form>
      @if (error) { <p class="error">{{ error }}</p> }
    </div>
    <table>
      <tr><th>ID</th><th>Name</th><th>Cuisine</th><th>City</th><th>Rating</th><th></th></tr>
      @for (r of restaurants; track r.id) {
        <tr>
          <td>{{ r.id }}</td><td>{{ r.name }}</td><td>{{ r.cuisine }}</td><td>{{ r.city }}</td><td>{{ r.rating }}</td>
          <td>
            <button class="btn secondary" (click)="edit(r)">Edit</button>
            <button class="btn danger" (click)="remove(r)">Delete</button>
          </td>
        </tr>
      }
    </table>
  `
})
export class AdminRestaurantsComponent implements OnInit {
  restaurants: Restaurant[] = [];
  form: Restaurant = this.blank();
  error = '';

  constructor(private svc: RestaurantService) {}

  ngOnInit(): void { this.load(); }

  load(): void { this.svc.getAll().subscribe(r => this.restaurants = r); }

  blank(): Restaurant { return { name: '', cuisine: '', city: '', address: '', imageUrl: '', rating: 4 }; }

  edit(r: Restaurant): void { this.form = { ...r }; }

  reset(): void { this.form = this.blank(); }

  save(): void {
    this.error = '';
    const req = this.form.id ? this.svc.update(this.form.id, this.form) : this.svc.create(this.form);
    req.subscribe({ next: () => { this.reset(); this.load(); }, error: e => this.error = errorMessage(e) });
  }

  remove(r: Restaurant): void {
    if (!confirm(`Delete ${r.name} and all its dishes?`)) return;
    this.svc.delete(r.id!).subscribe({ next: () => this.load(), error: e => this.error = errorMessage(e) });
  }
}
