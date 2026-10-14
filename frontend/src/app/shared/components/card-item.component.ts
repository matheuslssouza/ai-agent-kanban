import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../../core/models/task.model';
import { BadgeComponent } from './badge.component';

@Component({
  selector: 'app-card-item',
  standalone: true,
  imports: [CommonModule, BadgeComponent],
  template: `
    <div
      class="gh-card"
      [class.dragging]="isDragging"
      draggable="true"
      (dragstart)="onDragStart($event)"
      (dragend)="onDragEnd($event)"
      (click)="cardClick.emit(task)"
    >
      <div class="gh-card__top">
        <span class="gh-card__id">#{{ task.id }}</span>
        <div class="gh-card__priority">
          <app-badge type="priority" [priority]="task.priority"></app-badge>
        </div>
      </div>

      <div class="gh-card__title">{{ task.title }}</div>

      @if (task.description) {
        <div class="gh-card__description">{{ task.description }}</div>
      }

      @if (task.labels && task.labels.length > 0) {
        <div class="gh-card__labels">
          @for (lbl of task.labels; track lbl) {
            <app-badge type="label" [label]="lbl"></app-badge>
          }
        </div>
      }

      <div class="gh-card__bottom">
        <div class="gh-card__meta">
          <div class="gh-avatar" [class.gh-avatar--ai]="isAiAssignee()">
            {{ getInitials(task.assignee) }}
          </div>
          @if (task.dueDate) {
            <span class="gh-card__due-date">
              <svg width="12" height="12" viewBox="0 0 16 16" fill="currentColor">
                <path d="M4.75 0a.75.75 0 0 1 .75.75V2h5V.75a.75.75 0 0 1 1.5 0V2h1.25c.966 0 1.75.784 1.75 1.75v10.5A1.75 1.75 0 0 1 13.25 16H2.75A1.75 1.75 0 0 1 1 14.25V3.75C1 2.784 1.784 2 2.75 2H4V.75A.75.75 0 0 1 4.75 0ZM2.5 7.5v6.75c0 .138.112.25.25.25h10.5a.25.25 0 0 0 .25-.25V7.5Zm10.75-4H2.75a.25.25 0 0 0-.25.25V6h11V3.75a.25.25 0 0 0-.25-.25Z"/>
              </svg>
              {{ task.dueDate }}
            </span>
          }
        </div>

        <div class="gh-card__actions" (click)="$event.stopPropagation()">
          @if (task.status !== 'DONE') {
            <button
              type="button"
              class="gh-card__action-btn gh-card__action-btn--done"
              title="Mark as Done"
              (click)="doneClick.emit(task.id)"
            >
              <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
                <path d="M13.78 4.22a.75.75 0 0 1 0 1.06l-7.25 7.25a.75.75 0 0 1-1.06 0L2.22 9.28a.751.751 0 0 1 .018-1.042.751.751 0 0 1 1.042-.018L6 10.94l6.72-6.72a.75.75 0 0 1 1.06 0Z"/>
              </svg>
            </button>
          }
          <button
            type="button"
            class="gh-card__action-btn gh-card__action-btn--delete"
            title="Delete task"
            (click)="deleteClick.emit(task.id)"
          >
            <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
              <path d="M11 1.75V3h2.25a.75.75 0 0 1 0 1.5H2.75a.75.75 0 0 1 0-1.5H5V1.75C5 .784 5.784 0 6.75 0h2.5C10.216 0 11 .784 11 1.75ZM4.496 6.675l.66 6.6a.25.25 0 0 0 .249.225h5.19a.25.25 0 0 0 .249-.225l.66-6.6a.75.75 0 0 1 1.492.15l-.66 6.6A1.75 1.75 0 0 1 10.595 15H5.405a1.75 1.75 0 0 1-1.741-1.575l-.66-6.6a.75.75 0 1 1 1.492-.15ZM6.5 1.75V3h3V1.75a.25.25 0 0 0-.25-.25h-2.5a.25.25 0 0 0-.25.25Z"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  `
})
export class CardItemComponent {
  @Input({ required: true }) task!: Task;
  @Output() cardClick = new EventEmitter<Task>();
  @Output() doneClick = new EventEmitter<number>();
  @Output() deleteClick = new EventEmitter<number>();
  @Output() dragStarted = new EventEmitter<DragEvent>();
  @Output() dragEnded = new EventEmitter<DragEvent>();

  isDragging = false;

  onDragStart(event: DragEvent): void {
    this.isDragging = true;
    if (event.dataTransfer) {
      event.dataTransfer.setData('text/plain', JSON.stringify({
        taskId: this.task.id,
        sourceColumnId: this.task.columnId,
        sourceStatus: this.task.status
      }));
      event.dataTransfer.effectAllowed = 'move';
    }
    this.dragStarted.emit(event);
  }

  onDragEnd(event: DragEvent): void {
    this.isDragging = false;
    this.dragEnded.emit(event);
  }

  getInitials(name?: string): string {
    if (!name) return 'AI';
    if (name.toLowerCase().includes('ai')) return '🤖';
    const parts = name.trim().split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  }

  isAiAssignee(): boolean {
    return !!this.task.assignee && this.task.assignee.toLowerCase().includes('ai');
  }
}
