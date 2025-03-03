import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Component, computed, effect, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../features/cart/cart.service';
import { MatAnchor, MatButtonModule } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatBadgeModule,
    MatAnchor,
    RouterLink,
    MatMenuItem,
    MatMenu,
    MatMenuTrigger
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  showUserMenu = signal<boolean>(false);
  cartItemCount = computed(() => this.cartService.getCartItemCount()());
  isLoggedIn = computed(() => this.authService.isLoggedIn()());
  isAdmin = computed(() => this.authService.isAdmin());

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    effect(() => {
      console.log('Cart item count changed', this.cartItemCount());
    });

    effect(() => {
      console.log('User login status:', this.isLoggedIn());
    });
  }

  toggleUserMenu() {
    this.showUserMenu.update(value => !value);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  onSearch(query: string) {
    console.log('Search query: ', query);
    // You can emit this event to filter products in the ProductListComponent
  }
}

