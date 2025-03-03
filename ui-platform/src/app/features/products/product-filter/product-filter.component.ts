import { Component, EventEmitter, Output, signal } from '@angular/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { NgForOf } from '@angular/common';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { ProductFilter } from '../models/product-filter.model';

@Component({
  selector: 'app-product-filter',
  imports: [
    MatFormField,
    MatSelect,
    MatOption,
    NgForOf,
    MatCheckbox,
    FormsModule,
    MatInput,
    MatLabel,
    MatButton
  ],
  templateUrl: './product-filter.component.html',
  styleUrl: './product-filter.component.scss'
})
export class ProductFilterComponent {
  categories = signal<string[]>(["Laptops", "Mobile Phones", "Headphones", "Accessories"]);
  priceRange = signal<{ min: number, max: number }>({ min: 0, max: 2000 });
  showNewProducts = signal<boolean>(false);
  sortOptions = signal<{label: string; value: string}[]>([
    { label: "Newest First", value: "createdAt-desc" },
    { label: "Price: Low to High", value: "price-asc" },
    { label: "Price: High to Low", value: "price-desc" }
  ]);

  // Selected categories and sort order
  selectedCategories = signal<string[]>([]);
  selectedSort = signal<string | null>("createdAt-desc");

  @Output() filtersChanged = new EventEmitter<ProductFilter>();

  // Handle category selection (checkbox toggle)
  toggleCategory(category: string) {
    const currentCategories = this.selectedCategories();
    const updatedCategories = currentCategories.includes(category)
      ? currentCategories.filter(c => c !== category)
      : [... currentCategories, category];
    this.selectedCategories.set(updatedCategories);
    this.emitFilterChange();
  }

  // Handle changes in minimum price input
  onMinPriceChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    let newMin = parseFloat(inputElement.value);
    newMin = isNaN(newMin) || newMin < 0 ? 0 : newMin;
    this.priceRange.set({ min: newMin, max: this.priceRange().max});
    this.emitFilterChange();
  }

  // Handle changes in maximum price input
  onMaxPriceChange(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    let newMax = parseFloat(inputElement.value);
    newMax = isNaN(newMax) ? this.priceRange().max : newMax;
    this.priceRange.set({ min: this.priceRange().min, max: newMax });
    this.emitFilterChange();
  }

  // Toggle "New Products" filter
  toggleNewProducts() {
    this.showNewProducts.update(value => !value);
    this.emitFilterChange();
  }

  // Update sorting preferences
  updateSort(sortValue: string) {
    this.selectedSort.set(sortValue);
    this.emitFilterChange();
  }

  applyFilters() {
    this.emitFilterChange();
  }

  resetFilters() {
    this.selectedCategories.set([]);
    this.priceRange.set({ min: 0, max: 2000 });
    this.showNewProducts.set(false);
    this.selectedSort.set("createdAt-desc");
    this.emitFilterChange();
  }

  // Emit filter change
  private emitFilterChange() {
    const filterState = {
      category: this.selectedCategories(),
      priceRange: this.priceRange(),
      showNewProducts: this.showNewProducts(),
      sort: this.selectedSort()
    }
    console.log("THE FILTER STATE: ", filterState);
    this.filtersChanged.emit({
      category: this.selectedCategories(),
      priceRange: this.priceRange(),
      showNewProducts: this.showNewProducts(),
      sort: this.selectedSort()
    });
  }
}
