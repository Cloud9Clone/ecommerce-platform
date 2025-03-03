import { Component, computed, inject, OnInit, Signal, signal } from '@angular/core';
import { ProductFilterComponent } from '../../product-filter/product-filter.component';
import { ProductListComponent } from '../../product-list/product-list.component';
import { ActivatedRoute } from '@angular/router';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { ProductFilter } from '../../models/product-filter.model';

@Component({
  selector: 'app-products-page',
  imports: [
    ProductFilterComponent,
    ProductListComponent
  ],
  templateUrl: './products-page.component.html',
  styleUrl: './products-page.component.scss'
})
export class ProductsPageComponent implements OnInit {
  activeFilters = signal<ProductFilter>({
    category: [],
    priceRange: { min: 0, max: 2000 },
    showNewProducts: false,
    sort: null
  });
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);

  totalProducts: Signal<Product[]> = this.productService.products;

  searchQuery = signal<string | null>(null);
  selectedCategory = signal<string | null>(null);

  totalProductCount = computed(() => this.totalProducts().length);
  filteredProductCount = signal(this.totalProducts().length);

  constructor() {
    this.route.queryParams.subscribe(params => {
        if (params['search']) {
          this.searchQuery.set(params['search']);
        }
    });
  }

  ngOnInit() {
    this.updateFilteredCount(this.totalProducts().length);
  }

  onFilterChange(filters: ProductFilter) {
    this.activeFilters.set(filters);
  }

  updateFilteredCount(count: number) {
    this.filteredProductCount.set(count);
  }

  // Dynamically set the page title
  pageTitle = computed(() => {
      if (this.searchQuery()) {
        return `Search Results for "${this.searchQuery()}"`;
      } else if (this.selectedCategory()) {
        return `Products in ${this.selectedCategory()}`;
      }
      return "All Products";
  });

  // Dynamically set the product count message
  productCountMessage = computed(() => {
    if (this.filteredProductCount() === this.totalProductCount()) {
      return `(Total ${this.totalProductCount()} products)`;
    }
    return `${this.filteredProductCount()} products filtered`;
  });
}
