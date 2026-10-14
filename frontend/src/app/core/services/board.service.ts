import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Board, Column } from '../models/board.model';

@Injectable({
  providedIn: 'root'
})
export class BoardService {
  private readonly apiUrl = typeof window !== 'undefined' && (window.location.port === '8085' || !window.location.port) ? '/api/boards' : 'http://localhost:8085/api/boards';

  constructor(private http: HttpClient) {}

  getDefaultBoard(): Observable<Board> {
    return this.http.get<Board>(`${this.apiUrl}/default`);
  }

  getAllBoards(): Observable<Board[]> {
    return this.http.get<Board[]>(this.apiUrl);
  }

  getBoardById(id: number): Observable<Board> {
    return this.http.get<Board>(`${this.apiUrl}/${id}`);
  }

  getColumnsByBoard(boardId: number): Observable<Column[]> {
    return this.http.get<Column[]>(`${this.apiUrl}/${boardId}/columns`);
  }
}
