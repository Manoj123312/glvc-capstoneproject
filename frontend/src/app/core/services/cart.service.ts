import { Injectable, computed, signal } from '@angular/core';
import { CartItem, Dish } from '../models';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly _items = signal<CartItem[]>(JSON.parse(localStorage.getItem('foodie_cart') ?? '[]'));
  readonly items = this._items.asReadonly();
  readonly count = computed(() => this._items().reduce((s, i) => s + i.quantity, 0));
  readonly total = computed(() => this._items().reduce((s, i) => s + i.quantity * i.dish.price, 0));

  add(dish: Dish): void {
    const items = [...this._items()];
    const found = items.find(i => i.dish.id === dish.id);
    if (found) found.quantity++; else items.push({ dish, quantity: 1 });
    this.set(items);
  }

  setQuantity(dishId: number | undefined, qty: number): void {
    this.set(this._items().map(i => i.dish.id === dishId ? { ...i, quantity: qty } : i).filter(i => i.quantity > 0));
  }

  remove(dishId: number | undefined): void { this.set(this._items().filter(i => i.dish.id !== dishId)); }

  clear(): void { this.set([]); }

  private set(items: CartItem[]): void {
    this._items.set(items);
    localStorage.setItem('foodie_cart', JSON.stringify(items));
  }
}
