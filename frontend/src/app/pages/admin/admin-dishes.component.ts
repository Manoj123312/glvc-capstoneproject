import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Dish, DishRequest, Restaurant } from '../../core/models';
import { errorMessage } from '../../core/guards/auth.interceptor';
import { DishService, RestaurantService } from '../../core/services/api.services';

@Component({
  selector: 'app-admin-dishes',
  standalone: true,
  imports: [FormsModule],
  template: `
    <h2>Product (dish) management</h2>
    <div class="card">
      <h3>{{ editingId ? 'Edit dish #' + editingId : 'Add dish' }}</h3>
      <form (ngSubmit)="save()" class="form-grid">
        <select name="restaurantId" [(ngModel)]="form.restaurantId" required>
          <option [ngValue]="0" disabled>Select restaurant</option>
          @for (r of restaurants; track r.id) { <option [ngValue]="r.id">{{ r.name }}</option> }
        </select>
        <input name="name" placeholder="Dish name" [(ngModel)]="form.name" required />
        <input name="price" type="number" min="0" placeholder="Price" [(ngModel)]="form.price" required />
        <input name="category" placeholder="Category" [(ngModel)]="form.category" />
        <input name="imageUrl" placeholder="Image URL" [(ngModel)]="form.imageUrl" />
        <input name="description" placeholder="Description" [(ngModel)]="form.description" />
        <label><input type="checkbox" name="veg" [(ngModel)]="form.veg" /> Veg</label>
        <label><input type="checkbox" name="available" [(ngModel)]="form.available" /> Available</label>
        <div>
          <button class="btn" type="submit">{{ editingId ? 'Update' : 'Add' }}</button>
          @if (editingId) { <button class="btn secondary" type="button" (click)="reset()">Cancel</button> }
        </div>
      </form>
      @if (error) { <p class="error">{{ error }}</p> }
    </div>
    <table>
      <tr><th>ID</th><th>Name</th><th>Restaurant</th><th>Category</th><th>Price</th><th>Veg</th><th>Available</th><th></th></tr>
      @for (d of dishes; track d.id) {
        <tr>
          <td>{{ d.id }}</td><td>{{ d.name }}</td><td>{{ d.restaurant?.name }}</td><td>{{ d.category }}</td>
          <td>₹{{ d.price }}</td><td>{{ d.veg ? 'Yes' : 'No' }}</td><td>{{ d.available ? 'Yes' : 'No' }}</td>
          <td>
            <button class="btn secondary" (click)="edit(d)">Edit</button>
            <button class="btn danger" (click)="remove(d)">Delete</button>
          </td>
        </tr>
      }
    </table>
  `
})
export class AdminDishesComponent implements OnInit {
  dishes: Dish[] = [];
  restaurants: Restaurant[] = [];
  form: DishRequest = this.blank();
  editingId: number | null = null;
  error = '';

  constructor(private dishSvc: DishService, private restaurantSvc: RestaurantService) {}

  ngOnInit(): void {
    this.restaurantSvc.getAll().subscribe(r => this.restaurants = r);
    this.load();
  }

  load(): void { this.dishSvc.search().subscribe(d => this.dishes = d); }

  blank(): DishRequest { return { restaurantId: 0, name: '', price: 0, category: '', description: '', imageUrl: '', veg: true, available: true }; }

  edit(d: Dish): void {
    this.editingId = d.id!;
    this.form = { restaurantId: d.restaurant!.id!, name: d.name, price: d.price, category: d.category, description: d.description,
                  imageUrl: d.imageUrl, veg: d.veg, available: d.available };
  }

  reset(): void { this.editingId = null; this.form = this.blank(); }

  save(): void {
    this.error = '';
    if (!this.form.restaurantId) { this.error = 'Please select a restaurant'; return; }
    const req = this.editingId ? this.dishSvc.update(this.editingId, this.form) : this.dishSvc.create(this.form);
    req.subscribe({ next: () => { this.reset(); this.load(); }, error: e => this.error = errorMessage(e) });
  }

  remove(d: Dish): void {
    if (!confirm(`Delete ${d.name}?`)) return;
    this.dishSvc.delete(d.id!).subscribe({ next: () => this.load(), error: e => this.error = errorMessage(e) });
  }
}
