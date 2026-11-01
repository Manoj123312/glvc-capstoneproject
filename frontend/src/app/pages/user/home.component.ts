import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Dish, Restaurant } from '../../core/models';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { DishService, RestaurantService } from '../../core/services/api.services';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule],
  template: `
    <h2>Restaurants</h2>
    <div class="chips">
      <span class="chip" [class.on]="!filter.restaurantId" (click)="selectRestaurant(null)">All</span>
      @for (r of restaurants; track r.id) {
        <span class="chip" [class.on]="filter.restaurantId === r.id" (click)="selectRestaurant(r.id!)">
          {{ r.name }} · {{ r.cuisine }} · ⭐{{ r.rating }}
        </span>
      }
    </div>

    <div class="filters card">
      <input placeholder="Search dishes..." [(ngModel)]="filter.q" (ngModelChange)="load()" />
      <select [(ngModel)]="vegChoice" (ngModelChange)="load()">
        <option value="">Veg & Non-veg</option>
        <option value="true">Veg only</option>
        <option value="false">Non-veg only</option>
      </select>
      <select [(ngModel)]="filter.category" (ngModelChange)="load()">
        <option value="">All categories</option>
        @for (c of categories; track c) { <option [value]="c">{{ c }}</option> }
      </select>
      <input type="number" min="0" placeholder="Max price" [(ngModel)]="filter.maxPrice" (ngModelChange)="load()" />
    </div>

    @if (message) { <p class="success">{{ message }}</p> }
    @if (dishes.length === 0) { <p class="muted">No dishes match your filters.</p> }
    <div class="grid">
      @for (d of dishes; track d.id) {
        <div class="card dish">
          @if (d.imageUrl) { <img [src]="d.imageUrl" [alt]="d.name" /> }
          <h3>{{ d.name }} <span [class]="d.veg ? 'veg' : 'nonveg'">●</span></h3>
          <p class="muted">{{ d.restaurant?.name }} · {{ d.category }}</p>
          <p>{{ d.description }}</p>
          <div class="row">
            <strong>₹{{ d.price }}</strong>
            <button class="btn" [disabled]="!d.available" (click)="add(d)">{{ d.available ? 'Add to cart' : 'Unavailable' }}</button>
          </div>
        </div>
      }
    </div>
  `
})
export class HomeComponent implements OnInit {
  restaurants: Restaurant[] = [];
  dishes: Dish[] = [];
  categories: string[] = [];
  filter: { restaurantId: number | null; category: string; maxPrice: number | null; q: string } =
    { restaurantId: null, category: '', maxPrice: null, q: '' };
  vegChoice = '';
  message = '';

  constructor(private restaurantSvc: RestaurantService, private dishSvc: DishService, private cart: CartService,
              private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.restaurantSvc.getAll().subscribe(r => this.restaurants = r);
    this.dishSvc.search().subscribe(all => this.categories = [...new Set(all.map(d => d.category).filter((c): c is string => !!c))]);
    this.load();
  }

  selectRestaurant(id: number | null): void { this.filter.restaurantId = id; this.load(); }

  load(): void {
    this.dishSvc.search({ ...this.filter, veg: this.vegChoice === '' ? null : this.vegChoice === 'true' })
      .subscribe(d => this.dishes = d);
  }

  add(d: Dish): void {
    if (!this.auth.isLoggedIn()) { this.router.navigate(['/login']); return; }
    this.cart.add(d);
    this.message = `${d.name} added to cart`;
    setTimeout(() => this.message = '', 2000);
  }
}
