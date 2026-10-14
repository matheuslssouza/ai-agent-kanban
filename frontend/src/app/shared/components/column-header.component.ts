import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Column } from '../../core/models/board.model';
import { BadgeComponent } from './badge.component';

@Component({
  selector: 'app-column-header',
  standalone: true,
  imports: [CommonModule, BadgeComponent],
  template: `
    <div class="kanban-column__header">
      <div class="kanban-column__header-info">
        <span
          class="kanban-column__status-dot"
          [style.color]="column.color || '#58a6ff'"
          [style.backgroundColor]="column.color || '#58a6ff'"
        ></span>
        <span class="kanban-column__title">{{ column.name }}</span>
        <app-badge type="count" [count]="column.tasks.length"></app-badge>
      </div>

      <button
        type="button"
        class="kanban-column__quick-add-btn"
        title="Add item to this column"
        (click)="addClick.emit(column)"
      >
        <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
          <path d="M7.75 2a.75.75 0 0 1 .75.75V7h4.25a.75.75 0 0 1 0 1.5H8.5v4.25a.75.75 0 0 1-1.5 0V8.5H2.75a.75.75 0 0 1 0-1.5H7V2.75A.75.75 0 0 1 7.75 2Z"/>
        </svg>
      </button>
    </div>
  `
})
export class ColumnHeaderComponent {
  @Input({ required: true }) column!: Column;
  @Output() addClick = new EventEmitter<Column>();
}
