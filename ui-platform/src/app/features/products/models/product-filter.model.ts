export interface ProductFilter {
  category: string[];
  priceRange: { min: number; max: number };
  showNewProducts: boolean;
  sort: string | null;
}
