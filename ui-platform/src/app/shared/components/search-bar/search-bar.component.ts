import {Component, EventEmitter, Output} from '@angular/core';
import {MatFormField, MatSuffix} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-search-bar',
  imports: [
    MatFormField,
    MatInput,
    MatIcon,
    MatSuffix
  ],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.scss'
})
export class SearchBarComponent {
  @Output() search = new EventEmitter<string>();

  onSearch(event: any): void {
    this.search.emit(event.target.value);
  }
}
