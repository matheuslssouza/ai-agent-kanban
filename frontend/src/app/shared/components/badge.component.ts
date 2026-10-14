import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskPriority } from '../../core/models/task.model';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (type === 'priority') {
      <span class="gh-badge gh-badge--priority" [ngClass]="getPriorityClass()">
        {{ priority || 'MEDIUM' }}
      </span>
    } @else if (type === 'count') {
      <span class="gh-badge gh-badge--count">
        {{ count }}
      </span>
    } @else {
      <span class="gh-badge gh-badge--label" [ngClass]="getLabelClass()">
        {{ label }}
      </span>
    }
  `
})
export class BadgeComponent {
  @Input() type: 'label' | 'priority' | 'count' = 'label';
  @Input() label?: string;
  @Input() priority?: TaskPriority;
  @Input() count?: number;

  getPriorityClass(): string {
    const p = (this.priority || 'medium').toLowerCase();
    return `priority-${p}`;
  }

  getLabelClass(): string {
    if (!this.label) return '';
    const clean = this.label.toLowerCase().trim().replace(/[^a-z0-9]/g, '-');
    return `label-${clean}`;
  }
}
