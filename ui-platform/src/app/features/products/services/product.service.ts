import { Injectable, Signal, signal } from '@angular/core';
import { Product } from '../models/product.model';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, firstValueFrom, lastValueFrom, tap } from 'rxjs';
import { ProductFilter } from '../models/product-filter.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private mockDataUrl = 'assets/mock-data/products.json';

  // Observables for async HTTP request handling
  private productsSubject = new BehaviorSubject<Product[]>([]);
  products$ = this.productsSubject.asObservable();

  // Signal to store loaded products
  private _products = signal<Product[]>([]);
  products: Signal<Product[]> = this._products;

  // Track if products have been loaded
  private productsLoaded = false;

  // Filters stored using a Signal
  activeFilters = signal<ProductFilter>({
    category: [],
    priceRange: { min: 0, max: 2000 },
    showNewProducts: false,
    sort: 'createdAt-desc'
  });

  constructor(private http: HttpClient) {}

  // Load products from the mock API and store them as a Signal.
  async loadProducts(): Promise<void> {
    if (this.productsLoaded) return;

    const products = await lastValueFrom(this.http.get<Product[]>(this.mockDataUrl))
    this.productsSubject.next(products);
    this._products.set(products);
    this.productsLoaded = true;

    /*
    this.http.get<Product[]>(this.mockDataUrl)
      .pipe(
        tap(products => {
          console.log(`Loaded ${products.length} products`);
          this.productsSubject.next(products);
          this._products.set(products);
        }),
        catchError(error => {
          console.log('Failed to load products: ', error);
          throw error;
        })
      )
      .subscribe();
     */
  }

  // Retrieve a single product by its ID
  async getProductById(id: string | null): Promise<Product | undefined> {
    if (!id) return undefined;

    // Wait until products are loaded
    if (!this.productsLoaded) {
      await this.loadProducts();
    }

    return this.products().find(p => p.id === id);
  }

  passesFilter(product: Product): boolean {
    const filters = this.activeFilters();

    // Category filtering (multiple selections)
    if (filters.category.length > 0 && !filters.category.includes(product.category)) return false;

    // Price range filtering
    if (product.price < filters.priceRange.min || product.price > filters.priceRange.max) return false;

    // New Products filtering
    if (filters.showNewProducts) {
      const productDate = new Date(product.createdAt);
      const today = new Date();
      const daysDifference = (today.getTime() - productDate.getTime()) / (1000 * 60 * 60 * 24);

      if (daysDifference > 30) return false;
    }
    return true;
  }
}
