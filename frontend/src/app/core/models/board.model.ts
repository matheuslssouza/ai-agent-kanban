import { Task, TaskStatus } from './task.model';

export interface Column {
  id: number;
  name: string;
  statusKey: TaskStatus;
  position: number;
  color: string;
  boardId: number;
  taskCount: number;
  tasks: Task[];
}

export interface Board {
  id: number;
  name: string;
  description: string;
  totalTasks: number;
  columns: Column[];
  createdAt: string;
  updatedAt: string;
}
