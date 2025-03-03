import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = signal<boolean>(false);
  private userRole = signal<string | null>(null);

  constructor(private router: Router) {
    const storedUser = localStorage.getItem('userRole');
    if (storedUser) {
      this.userRole.set(storedUser);
      this.loggedIn.set(true);
    }
  }

  login(email: string, password: string): boolean {
    // Fake login logic
    if (email === 'admin@corp.com' && password === 'admin') {
      this.userRole.set('ADMIN');
      this.loggedIn.set(true);
      localStorage.setItem('userRole', 'ADMIN');
      return true;
    } else if (email === 'customer@corp.com' && password === 'customer') {
      this.userRole.set('CUSTOMER');
      this.loggedIn.set(true);
      localStorage.setItem('userRole', 'CUSTOMER');
      return true;
    }
    return false;
  }

  // Logout method
  logout(): void {
    this.loggedIn.set(false);
    this.userRole.set(null);
    localStorage.removeItem('userRole');
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn() {
    return this.loggedIn;
  }

  getUserRole() {
    return this.userRole;
  }

  isAdmin() {
    return this.userRole() === 'ADMIN';
  }
}
