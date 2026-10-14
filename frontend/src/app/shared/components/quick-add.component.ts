import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Column } from '../../core/models/board.model';

@Component({
  selector: 'app-quick-add',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (!isOpen) {
      <button
        type="button"
        class="gh-button gh-button--ghost"
        style="width: 100%; justify-content: flex-start; color: #848d97; background: transparent; border: 1px dashed #30363d; padding: 6px 10px;"
        (click)="openInput()"
      >
        <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
          <path d="M7.75 2a.75.75 0 0 1 .75.75V7h4.25a.75.75 0 0 1 0 1.5H8.5v4.25a.75.75 0 0 1-1.5 0V8.5H2.75a.75.75 0 0 1 0-1.5H7V2.75A.75.75 0 0 1 7.75 2Z"/>
        </svg>
        Add item
      </button>
    } @else {
      <div style="display: flex; flex-direction: column; gap: 8px; background: #21262d; padding: 8px; border-radius: 6px; border: 1px solid #30363d;">
        <input
          #taskInput
          type="text"
          class="gh-input"
          placeholder="Title (Press Enter to save)..."
          [(ngModel)]="title"
          (keydown.enter)="save()"
          (keydown.escape)="cancel()"
          style="width: 100%; font-size: 13px;"
        />
        <div style="display: flex; justify-content: flex-end; gap: 6px;">
          <button
            type="button"
            class="gh-button"
            style="padding: 3px 8px; font-size: 12px; background: transparent; border-color: transparent; color: #848d97;"
            (click)="cancel()"
          >
            Cancel
          </button>
          <button
            type="button"
            class="gh-button"
            style="padding: 3px 10px; font-size: 12px; background: #238636; color: white; border: none; border-radius: 4px;"
            [disabled]="!title.trim()"
            (click)="save()"
          >
            Add
          </button>
        </div>
      </div>
    }
  `
})
export class QuickAddComponent {
  @Input({ required: true }) column!: Column;
  @Output() taskCreated = new EventEmitter<{ title: string; columnId: number; status: any }>();

  isOpen = false;
  title = '';

  openInput(): void {
    this.isOpen = true;
    this.title = '';
  }

  cancel(): void {
    this.isOpen = false;
    this.title = '';
  }

  save(): void {
    if (!this.title.trim()) return;
    this.taskCreated.emit({
      title: this.title.trim(),
      columnId: this.column.id,
      status: this.column.statusKey
    });
    this.title = '';
    this.isOpen = false;
  }
}
