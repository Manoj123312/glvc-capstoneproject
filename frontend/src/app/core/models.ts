export interface AuthResponse { token: string; id: number; name: string; email: string; role: 'ADMIN' | 'USER'; }

export interface User {
  id: number; name: string; email: string; phone?: string; address?: string;
  role: 'ADMIN' | 'USER'; active: boolean;
}

export interface Restaurant {
  id?: number; name: string; cuisine?: string; city?: string; address?: string; imageUrl?: string; rating: number;
}

export interface Dish {
  id?: number; name: string; description?: string; price: number; category?: string; veg: boolean;
  imageUrl?: string; available: boolean; restaurant?: Restaurant;
}

export interface DishRequest {
  restaurantId: number; name: string; description?: string; price: number; category?: string;
  veg: boolean; imageUrl?: string; available: boolean;
}

export interface CartItem { dish: Dish; quantity: number; }

export type OrderStatus = 'PLACED' | 'CONFIRMED' | 'PREPARING' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';
export const ORDER_STATUSES: OrderStatus[] =
  ['PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];

export interface OrderItem { dishId: number; dishName: string; restaurantName: string; unitPrice: number; quantity: number; }

export interface Order {
  id: number; userId: number; userName: string; status: OrderStatus; totalAmount: number;
  deliveryAddress: string; createdAt: string; items: OrderItem[];
}
