import {effect, Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemCount = signal<number>(0);

  constructor() {
    const storedCount = localStorage.getItem('cartCount');
    if (storedCount) {
      this.cartItemCount.set(parseInt(storedCount, 10));
    }

    effect(() => {
      localStorage.setItem('cartCount', this.cartItemCount().toString());
    });
  }

  addToCart(quantity: number) {
    this.cartItemCount.update(count => count + quantity);
  }

  removeFromCart(quantity: number) {
    this.cartItemCount.update(count => Math.max(count - quantity, 0));
  }

  clearCart() {
    this.cartItemCount.set(0);
  }

  getCartItemCount() {
    return this.cartItemCount;
  }
}
