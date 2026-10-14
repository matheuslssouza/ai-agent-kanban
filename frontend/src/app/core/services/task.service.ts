import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskCreatePayload, TaskMovePayload, TaskStatus, TaskUpdatePayload } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly apiUrl = typeof window !== 'undefined' && (window.location.port === '8085' || !window.location.port) ? '/api/tasks' : 'http://localhost:8085/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(query?: string, status?: TaskStatus, columnId?: number): Observable<Task[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (status) params = params.set('status', status);
    if (columnId) params = params.set('columnId', columnId.toString());
    return this.http.get<Task[]>(this.apiUrl, { params });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(payload: TaskCreatePayload): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, payload);
  }

  updateTask(id: number, payload: TaskUpdatePayload): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, payload);
  }

  moveTask(id: number, payload: TaskMovePayload): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/move`, payload);
  }

  markAsDone(id: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/done`, {});
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
