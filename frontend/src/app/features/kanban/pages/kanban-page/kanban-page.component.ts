import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Board, Column } from '../../../../core/models/board.model';
import { Task, TaskPriority, TaskStatus } from '../../../../core/models/task.model';
import { BoardService } from '../../../../core/services/board.service';
import { TaskService } from '../../../../core/services/task.service';

// Shared Reusable Components (inline templates)
import { ColumnHeaderComponent } from '../../../../shared/components/column-header.component';
import { CardItemComponent } from '../../../../shared/components/card-item.component';
import { QuickAddComponent } from '../../../../shared/components/quick-add.component';
import { FilterBarComponent } from '../../../../shared/components/filter-bar.component';
import { AiPromptBarComponent } from '../../../../shared/components/ai-prompt-bar.component';
import { McpInspectorComponent } from '../../../../shared/components/mcp-inspector.component';
import { TaskDetailModalComponent } from '../../components/task-detail-modal/task-detail-modal.component';

@Component({
  selector: 'app-kanban-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ColumnHeaderComponent,
    CardItemComponent,
    QuickAddComponent,
    FilterBarComponent,
    AiPromptBarComponent,
    McpInspectorComponent,
    TaskDetailModalComponent
  ],
  templateUrl: './kanban-page.component.html'
})
export class KanbanPageComponent implements OnInit {
  board: Board | null = null;
  columns: Column[] = [];
  currentView: 'board' | 'table' = 'board';

  searchQuery = '';
  priorityFilter = '';
  dragOverColumnId: number | null = null;

  // Modal State
  isModalOpen = false;
  isNewTask = false;
  selectedTask: Task | null = null;
  modalDefaultStatus: TaskStatus = 'TODO';
  modalDefaultColumnId?: number;

  // MCP Drawer State
  isMcpInspectorOpen = false;

  constructor(
    private boardService: BoardService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.refreshBoard();
  }

  refreshBoard(): void {
    this.boardService.getDefaultBoard().subscribe({
      next: (b) => {
        this.board = b;
        this.columns = b.columns || [];
      },
      error: (err) => {
        console.error('Error fetching board from backend:', err);
      }
    });
  }

  get totalCards(): number {
    return this.columns.reduce((sum, col) => sum + (col.tasks ? col.tasks.length : 0), 0);
  }

  get allTasksFlattened(): Task[] {
    const list: Task[] = [];
    for (const col of this.filteredColumns) {
      if (col.tasks) {
        list.push(...col.tasks);
      }
    }
    return list;
  }

  get filteredColumns(): Column[] {
    return this.columns.map((col) => {
      let tasks = col.tasks || [];
      if (this.searchQuery.trim()) {
        const q = this.searchQuery.toLowerCase().trim();
        tasks = tasks.filter(
          (t) =>
            t.title.toLowerCase().includes(q) ||
            (t.description && t.description.toLowerCase().includes(q)) ||
            (t.labels && t.labels.some((lbl) => lbl.toLowerCase().includes(q)))
        );
      }
      if (this.priorityFilter) {
        tasks = tasks.filter((t) => t.priority === this.priorityFilter);
      }
      return {
        ...col,
        tasks
      };
    });
  }

  onSearchFilter(query: string): void {
    this.searchQuery = query;
  }

  onPriorityFilter(priority: string): void {
    this.priorityFilter = priority;
  }

  // Drag and Drop Handling (Native HTML5 Drag & Drop)
  onDragOver(event: DragEvent, columnId: number): void {
    event.preventDefault();
    this.dragOverColumnId = columnId;
  }

  onDragLeave(event: DragEvent, columnId: number): void {
    if (this.dragOverColumnId === columnId) {
      this.dragOverColumnId = null;
    }
  }

  onDrop(event: DragEvent, targetColumn: Column): void {
    event.preventDefault();
    this.dragOverColumnId = null;

    if (!event.dataTransfer) return;
    const rawData = event.dataTransfer.getData('text/plain');
    if (!rawData) return;

    try {
      const data = JSON.parse(rawData);
      const taskId: number = data.taskId;

      this.taskService
        .moveTask(taskId, {
          targetColumnId: targetColumn.id,
          targetStatus: targetColumn.statusKey,
          targetPosition: targetColumn.tasks.length
        })
        .subscribe({
          next: () => this.refreshBoard(),
          error: (err) => {
            console.error('Error moving task:', err);
            this.refreshBoard();
          }
        });
    } catch (e) {
      console.error('Invalid drag data', e);
    }
  }

  // Task Actions
  onMarkDone(taskId: number): void {
    this.taskService.markAsDone(taskId).subscribe({
      next: () => this.refreshBoard(),
      error: (err) => console.error('Error marking task as done:', err)
    });
  }

  onDeleteTask(taskId: number): void {
    if (confirm(`Tem certeza que deseja excluir o item #${taskId}?`)) {
      this.taskService.deleteTask(taskId).subscribe({
        next: () => this.refreshBoard(),
        error: (err) => console.error('Error deleting task:', err)
      });
    }
  }

  onQuickAddCreated(event: { title: string; columnId: number; status: any }): void {
    this.taskService
      .createTask({
        title: event.title,
        columnId: event.columnId,
        status: event.status,
        priority: 'MEDIUM',
        labels: ['feature'],
        assignee: 'AI Assistant'
      })
      .subscribe({
        next: () => this.refreshBoard(),
        error: (err) => console.error('Error quick adding task:', err)
      });
  }

  // Modal Dialog
  openNewTaskModal(defaultStatus: TaskStatus = 'TODO', defaultColumnId?: number): void {
    this.isNewTask = true;
    this.modalDefaultStatus = defaultStatus;
    this.modalDefaultColumnId = defaultColumnId;
    this.selectedTask = null;
    this.isModalOpen = true;
  }

  openEditTaskModal(task: Task): void {
    this.isNewTask = false;
    this.selectedTask = task;
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedTask = null;
  }

  onModalSave(formData: any): void {
    if (this.isNewTask) {
      this.taskService.createTask(formData).subscribe({
        next: () => {
          this.closeModal();
          this.refreshBoard();
        },
        error: (err) => console.error('Error creating task:', err)
      });
    } else if (this.selectedTask) {
      this.taskService.updateTask(this.selectedTask.id, formData).subscribe({
        next: () => {
          this.closeModal();
          this.refreshBoard();
        },
        error: (err) => console.error('Error updating task:', err)
      });
    }
  }

  toggleMcpInspector(): void {
    this.isMcpInspectorOpen = !this.isMcpInspectorOpen;
  }
}
