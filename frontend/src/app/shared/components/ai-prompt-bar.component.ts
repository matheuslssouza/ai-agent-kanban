import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiService } from '../../core/services/ai.service';
import { AiCommandResponse } from '../../core/models/ai.model';

@Component({
  selector: 'app-ai-prompt-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ai-bar">
      <div class="ai-bar__badge">
        <svg width="14" height="14" viewBox="0 0 16 16" fill="currentColor">
          <path d="M7.5 1.5a.75.75 0 0 1 1 0l.94 1.88a2.25 2.25 0 0 0 1.18 1.18l1.88.94a.75.75 0 0 1 0 1l-1.88.94a2.25 2.25 0 0 0-1.18 1.18l-.94 1.88a.75.75 0 0 1-1 0l-.94-1.88a2.25 2.25 0 0 0-1.18-1.18l-1.88-.94a.75.75 0 0 1 0-1l1.88-.94a2.25 2.25 0 0 0 1.18-1.18l.94-1.88Z"/>
        </svg>
        AI Copilot / MCP
      </div>

      <form class="ai-bar__form" (submit)="onSubmit($event)">
        <input
          type="text"
          class="ai-bar__input"
          placeholder="Ex: 'Criar tarefa: Fazer deploy com prioridade alta', 'Marcar 3 como feita', 'Mover 2 para In Progress'..."
          [(ngModel)]="prompt"
          name="aiPrompt"
          [disabled]="isLoading"
        />

        <button
          type="submit"
          class="ai-bar__submit"
          [disabled]="isLoading || !prompt.trim()"
        >
          @if (isLoading) {
            <span>Processando...</span>
          } @else {
            <span>Executar</span>
          }
        </button>

        <button
          type="button"
          class="ai-bar__mcp-toggle"
          (click)="mcpToggleClick.emit()"
          title="Ver ferramentas MCP e documentação"
        >
          <svg width="13" height="13" viewBox="0 0 16 16" fill="currentColor">
            <path d="M0 2.75C0 1.784.784 1 1.75 1h12.5c.966 0 1.75.784 1.75 1.75v10.5A1.75 1.75 0 0 1 14.25 15H1.75A1.75 1.75 0 0 1 0 13.25Zm1.75-.25a.25.25 0 0 0-.25.25v10.5c0 .138.112.25.25.25h12.5a.25.25 0 0 0 .25-.25V2.75a.25.25 0 0 0-.25-.25ZM3 4.5a.75.75 0 0 1 .75-.75h8.5a.75.75 0 0 1 0 1.5h-8.5A.75.75 0 0 1 3 4.5Zm0 3.5a.75.75 0 0 1 .75-.75h8.5a.75.75 0 0 1 0 1.5h-8.5A.75.75 0 0 1 3 8Zm0 3.5a.75.75 0 0 1 .75-.75h5.5a.75.75 0 0 1 0 1.5h-5.5a.75.75 0 0 1-.75-.75Z"/>
          </svg>
          MCP Tools
        </button>
      </form>

      @if (lastFeedback) {
        <div class="ai-bar__feedback" [class.error]="!lastFeedback.success">
          {{ lastFeedback.message }}
        </div>
      }
    </div>
  `
})
export class AiPromptBarComponent {
  @Output() actionCompleted = new EventEmitter<void>();
  @Output() mcpToggleClick = new EventEmitter<void>();

  prompt = '';
  isLoading = false;
  lastFeedback: { message: string; success: boolean } | null = null;

  constructor(private aiService: AiService) {}

  onSubmit(event: Event): void {
    event.preventDefault();
    if (!this.prompt.trim() || this.isLoading) return;

    this.isLoading = true;
    this.lastFeedback = null;

    this.aiService.executeCommand({ prompt: this.prompt.trim() }).subscribe({
      next: (res: AiCommandResponse) => {
        this.isLoading = false;
        this.lastFeedback = {
          message: res.message,
          success: res.success
        };
        this.prompt = '';
        this.actionCompleted.emit();
        setTimeout(() => (this.lastFeedback = null), 6000);
      },
      error: (err) => {
        this.isLoading = false;
        this.lastFeedback = {
          message: 'Erro ao executar comando: ' + (err.error?.message || err.message),
          success: false
        };
      }
    });
  }
}
