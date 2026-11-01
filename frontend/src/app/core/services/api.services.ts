import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config';
import { Dish, DishRequest, Order, OrderStatus, Restaurant, User } from '../models';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  private url = `${API_URL}/restaurants`;
  constructor(private http: HttpClient) {}
  getAll(): Observable<Restaurant[]> { return this.http.get<Restaurant[]>(this.url); }
  get(id: number): Observable<Restaurant> { return this.http.get<Restaurant>(`${this.url}/${id}`); }
  create(r: Restaurant): Observable<Restaurant> { return this.http.post<Restaurant>(this.url, r); }
  update(id: number, r: Restaurant): Observable<Restaurant> { return this.http.put<Restaurant>(`${this.url}/${id}`, r); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

export interface DishFilter { restaurantId?: number | null; category?: string; veg?: boolean | null; maxPrice?: number | null; q?: string; }

@Injectable({ providedIn: 'root' })
export class DishService {
  private url = `${API_URL}/dishes`;
  constructor(private http: HttpClient) {}

  search(f: DishFilter = {}): Observable<Dish[]> {
    let params = new HttpParams();
    if (f.restaurantId) params = params.set('restaurantId', f.restaurantId);
    if (f.category) params = params.set('category', f.category);
    if (f.veg !== null && f.veg !== undefined) params = params.set('veg', f.veg);
    if (f.maxPrice) params = params.set('maxPrice', f.maxPrice);
    if (f.q) params = params.set('q', f.q);
    return this.http.get<Dish[]>(this.url, { params });
  }
  create(d: DishRequest): Observable<Dish> { return this.http.post<Dish>(this.url, d); }
  update(id: number, d: DishRequest): Observable<Dish> { return this.http.put<Dish>(`${this.url}/${id}`, d); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.url}/${id}`); }
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private http: HttpClient) {}
  place(deliveryAddress: string, items: { dishId: number; quantity: number }[]): Observable<Order> {
    return this.http.post<Order>(`${API_URL}/orders`, { deliveryAddress, items });
  }
  myOrders(): Observable<Order[]> { return this.http.get<Order[]>(`${API_URL}/orders/my`); }
  cancel(id: number): Observable<Order> { return this.http.put<Order>(`${API_URL}/orders/${id}/cancel`, {}); }
  allOrders(): Observable<Order[]> { return this.http.get<Order[]>(`${API_URL}/admin/orders`); }
  updateStatus(id: number, status: OrderStatus): Observable<Order> {
    return this.http.put<Order>(`${API_URL}/admin/orders/${id}/status`, { status });
  }
}

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}
  getAll(): Observable<User[]> { return this.http.get<User[]>(`${API_URL}/admin/users`); }
  update(id: number, u: Partial<User>): Observable<User> { return this.http.put<User>(`${API_URL}/admin/users/${id}`, u); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${API_URL}/admin/users/${id}`); }
  me(): Observable<User> { return this.http.get<User>(`${API_URL}/users/me`); }
  updateMe(u: Partial<User>): Observable<User> { return this.http.put<User>(`${API_URL}/users/me`, u); }
}
