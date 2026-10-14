import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService } from '../../core/services/ai.service';
import { McpTool } from '../../core/models/ai.model';

@Component({
  selector: 'app-mcp-inspector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="mcp-inspector">
      <div class="mcp-inspector__header">
        <div class="mcp-inspector__title">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M0 2.75C0 1.784.784 1 1.75 1h12.5c.966 0 1.75.784 1.75 1.75v10.5A1.75 1.75 0 0 1 14.25 15H1.75A1.75 1.75 0 0 1 0 13.25Zm1.75-.25a.25.25 0 0 0-.25.25v10.5c0 .138.112.25.25.25h12.5a.25.25 0 0 0 .25-.25V2.75a.25.25 0 0 0-.25-.25ZM3 4.5a.75.75 0 0 1 .75-.75h8.5a.75.75 0 0 1 0 1.5h-8.5A.75.75 0 0 1 3 4.5Zm0 3.5a.75.75 0 0 1 .75-.75h8.5a.75.75 0 0 1 0 1.5h-8.5A.75.75 0 0 1 3 8Zm0 3.5a.75.75 0 0 1 .75-.75h5.5a.75.75 0 0 1 0 1.5h-5.5a.75.75 0 0 1-.75-.75Z"/>
          </svg>
          Model Context Protocol (MCP)
        </div>
        <button type="button" class="gh-button gh-button--ghost" (click)="close.emit()">
          ✕
        </button>
      </div>

      <div class="mcp-inspector__content">
        <div style="font-size: 12px; color: #848d97;">
          Esta aplicação expõe ferramentas prontas para conexão com IA local (Claude Code, Cursor, Codex, Antigravity) via MCP ou REST:
        </div>

        <div style="display: flex; gap: 8px;">
          <a
            href="http://localhost:8080/swagger-ui.html"
            target="_blank"
            class="gh-button gh-button--primary"
            style="font-size: 11px; flex: 1; text-align: center;"
          >
            Abrir Swagger UI
          </a>
          <a
            href="http://localhost:8080/api/ai/mcp-manifest"
            target="_blank"
            class="gh-button"
            style="font-size: 11px; flex: 1; text-align: center;"
          >
            Ver Manifest JSON
          </a>
        </div>

        <h3 style="font-size: 13px; font-weight: 600; color: #e6edf3; margin-top: 8px;">
          Ferramentas Registradas ({{ tools.length }}):
        </h3>

        @for (tool of tools; track tool.name) {
          <div class="mcp-inspector__tool-card">
            <h4>{{ tool.name }}()</h4>
            <p>{{ tool.description }}</p>
            <div class="mcp-inspector__code-box">{{ tool.inputSchema | json }}</div>
          </div>
        }

        <div style="margin-top: 12px;">
          <h4 style="font-size: 13px; font-weight: 600; margin-bottom: 6px;">
            Exemplo de Configuração claude_desktop_config.json:
          </h4>
          <div class="mcp-inspector__code-box">
{{ mcpConfigSnippet }}
          </div>
        </div>
      </div>
    </div>
  `
})
export class McpInspectorComponent implements OnInit {
  @Output() close = new EventEmitter<void>();

  tools: McpTool[] = [];
  mcpConfigSnippet = `{
  "mcpServers": {
    "kanban-projects": {
      "command": "node",
      "args": ["./mcp-server/index.js"],
      "env": {
        "KANBAN_API_URL": "http://localhost:8080"
      }
    }
  }
}`;

  constructor(private aiService: AiService) {}

  ngOnInit(): void {
    this.aiService.getMcpTools().subscribe({
      next: (t) => (this.tools = t),
      error: () => {
        this.tools = [];
      }
    });
  }
}
