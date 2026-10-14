import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task, TaskPriority, TaskStatus } from '../../../../core/models/task.model';

@Component({
  selector: 'app-task-detail-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-detail-modal.component.html'
})
export class TaskDetailModalComponent implements OnInit {
  @Input() task!: Task;
  @Input() isNew = false;
  @Input() defaultStatus: TaskStatus = 'TODO';
  @Input() defaultColumnId?: number;

  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();
  @Output() delete = new EventEmitter<number>();

  activeTab: 'preview' | 'edit' = 'preview';

  formData: {
    title: string;
    description: string;
    status: TaskStatus;
    priority: TaskPriority;
    assignee: string;
    dueDate?: string;
    columnId?: number;
  } = {
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
    assignee: 'AI Assistant',
    dueDate: ''
  };

  labelsString = '';

  ngOnInit(): void {
    if (this.task && !this.isNew) {
      this.formData = {
        title: this.task.title,
        description: this.task.description || '',
        status: this.task.status,
        priority: this.task.priority,
        assignee: this.task.assignee || 'AI Assistant',
        dueDate: this.task.dueDate || '',
        columnId: this.task.columnId
      };
      this.labelsString = (this.task.labels || []).join(', ');
      this.activeTab = 'preview';
    } else {
      this.formData.status = this.defaultStatus;
      this.formData.columnId = this.defaultColumnId;
      this.labelsString = 'feature, ai-agent';
      this.activeTab = 'edit';
    }
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (!this.formData.title.trim()) return;

    const labels = this.labelsString
      .split(',')
      .map((l) => l.trim())
      .filter((l) => l.length > 0);

    const payload = {
      ...this.formData,
      labels
    };

    this.save.emit(payload);
  }
}
