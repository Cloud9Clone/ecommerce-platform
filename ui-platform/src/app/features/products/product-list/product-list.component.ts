import {
  AfterViewInit,
  Component,
  computed, EventEmitter,
  inject,
  Input, OnChanges,
  OnInit,
  Output,
  signal,
  Signal,
  ViewChild
} from '@angular/core';
import { Product } from '../models/product.model';
import { ProductService } from '../services/product.service';
import { MatGridList, MatGridTile } from '@angular/material/grid-list';
import { MatCard, MatCardActions, MatCardContent, MatCardImage } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { RouterLink } from '@angular/router';
import { NgForOf, NgOptimizedImage } from '@angular/common';
import { ProductFilter } from '../models/product-filter.model';

@Component({
  selector: 'app-product-list',
  imports: [
    MatGridList,
    MatGridTile,
    MatCard,
    MatCardImage,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatPaginatorModule,
    RouterLink,
    NgForOf,
    NgOptimizedImage
  ],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit, AfterViewInit, OnChanges {
  private productService = inject(ProductService);
  products: Signal<Product[]> = this.productService.products;
  private _filters = signal<ProductFilter>({
    category: [],
    priceRange: { min: 0, max: 2000 },
    showNewProducts: false,
    sort: "createdAt-desc"
  });

  // Filter values received from ProductFilterComponent through ProductPageComponent
  @Input() set filters(value: ProductFilter) {
    console.log("Received filters in product-list: ", value);
    this._filters.set(value);
  }

  get filters() {
    return this._filters();
  }

  @Output() updateCount = new EventEmitter<number>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  productsPerPage = 16;
  currentPage = 0;

  filteredProducts = computed(() => {
    return this.products()
      .filter(product => this.passesFilter(product))
      .sort((a, b) => this.applySorting(a, b));
  });

  paginatedProducts = computed(() => {
    const start = this.currentPage * this.productsPerPage;
    return this.filteredProducts().slice(start, start + this.productsPerPage);
  });

  async ngOnInit() {
    await this.productService.loadProducts();

    setTimeout(() => {
      this.updateCount.emit(this.filteredProducts().length)
    }, 100);
  }

  ngAfterViewInit() {
    if (this.paginator) {
      this.paginator.page.subscribe((event: PageEvent) => {
        this.currentPage = event.pageIndex;
        this.productsPerPage = event.pageSize;
      });
    }
  }

  ngOnChanges(): void {
    this.updateCount.emit(this.filteredProducts().length);
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.productsPerPage = event.pageSize;
  }

  private passesFilter(product: Product): boolean {
    if (this.filters.category.length > 0 && !this.filters.category.includes(product.category)) return false;

    if (product.price < this.filters.priceRange.min || product.price > this.filters.priceRange.max) return false;

    // Filter by New Products (Added in last 30 days)
    const productDate = new Date(product.createdAt);
    const today = new Date();
    const daysDifference = (today.getTime() - productDate.getTime()) / (1000 * 3000 * 24);

    return !(this.filters.showNewProducts && daysDifference > 30);
  }

  private applySorting(a: Product, b: Product): number {
    if (!this.filters.sort) return 0;

    switch (this.filters.sort) {
      case 'createdAt-desc': return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      case 'price-asc': return a.price - b.price;
      case 'price-desc': return b.price - a.price;
      default: return 0;
    }
  }

  addToCart(product: Product) {
    console.log("The product is: ", product);
  }
}
