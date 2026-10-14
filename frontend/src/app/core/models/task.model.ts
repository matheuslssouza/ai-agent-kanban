export type TaskStatus = 'BACKLOG' | 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'DONE';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  position: number;
  labels: string[];
  assignee: string;
  dueDate?: string;
  columnId?: number;
  columnName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TaskCreatePayload {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  columnId?: number;
  labels?: string[];
  assignee?: string;
  dueDate?: string;
}

export interface TaskUpdatePayload {
  title?: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  columnId?: number;
  position?: number;
  labels?: string[];
  assignee?: string;
  dueDate?: string;
}

export interface TaskMovePayload {
  targetColumnId?: number;
  targetStatus?: TaskStatus;
  targetPosition: number;
}
