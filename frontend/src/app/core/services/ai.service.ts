import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AiCommandRequest, AiCommandResponse, McpTool } from '../models/ai.model';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  private readonly apiUrl = typeof window !== 'undefined' && (window.location.port === '8085' || !window.location.port) ? '/api/ai' : 'http://localhost:8085/api/ai';

  constructor(private http: HttpClient) {}

  executeCommand(request: AiCommandRequest): Observable<AiCommandResponse> {
    return this.http.post<AiCommandResponse>(`${this.apiUrl}/command`, request);
  }

  getMcpTools(): Observable<McpTool[]> {
    return this.http.get<McpTool[]>(`${this.apiUrl}/tools`);
  }

  getMcpManifest(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/mcp-manifest`);
  }
}
