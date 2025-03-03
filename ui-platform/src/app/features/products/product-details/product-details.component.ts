import { AfterViewInit, Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatCard, MatCardActions, MatCardContent } from '@angular/material/card';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { MatButton } from '@angular/material/button';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../services/product.service';
import { Product } from '../models/product.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-details',
  imports: [
    CommonModule,
    MatCard,
    NgOptimizedImage,
    MatCardContent,
    MatButton,
    MatCardActions,
    FormsModule
  ],
  templateUrl: './product-details.component.html',
  styleUrl: './product-details.component.scss'
})
export class ProductDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);

  product = signal<Product | null>(null);
  quantity = 1; // Default quantity

  async ngOnInit() {
    const productId = this.route.snapshot.paramMap.get('id');
    if (!productId) return;

    const foundProduct = await this.productService.getProductById(productId);
    if (foundProduct) {
      this.product.set(foundProduct);
    } else {
      console.log(`Product with ID ${productId} not found`);
    }
  }

  // Ensures quantity is within allowed range
  validateQuantity() {
    if (this.quantity < 1) {
      this.quantity = 1;
    } else if (this.quantity > (this.product()?.stock ?? 1)) {
      this.quantity = this.product()?.stock ?? 1;
    }
  }

  // Check if the product is New (added in the last 30 days)
  isNewProduct = computed(() => {
    if (!this.product()) return false;
    const productDate = new Date(this.product()?.createdAt ?? '');
    const today = new Date();
    const differenceInDays = (today.getTime() - productDate.getTime()) / (1000 * 60 * 60 * 24);
    return differenceInDays <= 30;
  });

  // Compute the formatted date from createdAt
  formattedDate = computed(() => {
    const createdAt = this.product()?.createdAt;
    if (!createdAt) return 'N/A';

    const parsedDate = new Date(createdAt);
    return isNaN(parsedDate.getTime()) ? 'Invalid Date' : parsedDate.toLocaleDateString();
  });

  addToCart() {
    console.log(`Added ${this.quantity} of ${this.product()?.name} to cart.`);
  }
}
