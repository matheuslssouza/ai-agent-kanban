import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-filter-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="gh-subheader">
      <div class="gh-subheader__search">
        <svg width="14" height="14" viewBox="0 0 16 16">
          <path d="M10.68 11.74a6 6 0 0 1-7.922-8.982 6 6 0 0 1 8.982 7.922l3.04 3.04a.749.749 0 0 1-.326 1.275.749.749 0 0 1-.734-.215ZM11.5 7a4.5 4.5 0 1 0-9 0 4.5 4.5 0 0 0 9 0Z"/>
        </svg>
        <input
          type="text"
          class="gh-input"
          placeholder="Filter cards by title or description..."
          [(ngModel)]="searchQuery"
          (input)="onSearchChange()"
        />
      </div>

      <div class="gh-subheader__filters">
        <select
          class="gh-select"
          [(ngModel)]="selectedPriority"
          (change)="onPriorityChange()"
        >
          <option value="">All Priorities</option>
          <option value="URGENT">Urgent</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>

        <button
          type="button"
          class="gh-button"
          style="background: #238636; border-color: rgba(240, 246, 252, 0.1); color: #ffffff;"
          (click)="newItemClick.emit()"
        >
          <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
            <path d="M7.75 2a.75.75 0 0 1 .75.75V7h4.25a.75.75 0 0 1 0 1.5H8.5v4.25a.75.75 0 0 1-1.5 0V8.5H2.75a.75.75 0 0 1 0-1.5H7V2.75A.75.75 0 0 1 7.75 2Z"/>
          </svg>
          New Item
        </button>
      </div>
    </div>
  `
})
export class FilterBarComponent {
  @Output() search = new EventEmitter<string>();
  @Output() filterPriority = new EventEmitter<string>();
  @Output() newItemClick = new EventEmitter<void>();

  searchQuery = '';
  selectedPriority = '';

  onSearchChange(): void {
    this.search.emit(this.searchQuery);
  }

  onPriorityChange(): void {
    this.filterPriority.emit(this.selectedPriority);
  }
}
