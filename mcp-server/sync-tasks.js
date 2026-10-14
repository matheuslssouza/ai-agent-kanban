#!/usr/bin/env node

/**
 * Script utilitário para sincronizar tasks locais (.agents/tasks) com a API do Kanban
 * Importa o conteúdo COMPLETO do Markdown de cada task para o campo 'description' (Observações)
 * Uso: node sync-tasks.js [caminho_da_pasta_tasks] [api_url]
 */

const fs = require('fs');
const path = require('path');

const tasksDir = process.argv[2] || 'C:/Users/tizo/Documents/olhar-territorial/.agents/tasks';
const apiUrl = process.argv[3] || process.env.KANBAN_API_URL || 'http://localhost:8085';

async function scanAndSync() {
  console.log(`[Kanban Sync] Lendo tarefas de: ${tasksDir}`);
  console.log(`[Kanban Sync] Conectando à API: ${apiUrl}`);

  if (!fs.existsSync(tasksDir)) {
    console.error(`[Erro] Pasta não encontrada: ${tasksDir}`);
    process.exit(1);
  }

  // 1. Obter estado atual do Kanban
  let currentTasks = [];
  try {
    const res = await fetch(`${apiUrl}/api/tasks`);
    if (res.ok) {
      currentTasks = await res.json();
      console.log(`[Kanban Sync] O quadro possui atualmente ${currentTasks.length} tarefas.`);
    }
  } catch (e) {
    console.error(`[Erro] Não foi possível conectar ao backend em ${apiUrl}. Verifique se está rodando.`);
    process.exit(1);
  }

  // 2. Varrer diretórios de tasks
  const subdirs = [
    { dir: 'completed', status: 'DONE' },
    { dir: 'active', status: 'IN_PROGRESS' },
    { dir: 'backlog', status: 'BACKLOG' }
  ];

  let syncedCount = 0;

  for (const { dir, status } of subdirs) {
    const fullSubdir = path.join(tasksDir, dir);
    if (!fs.existsSync(fullSubdir)) continue;

    const files = fs.readdirSync(fullSubdir).filter(f => f.startsWith('TASK-') && f.endsWith('.md'));

    for (const file of files) {
      const filePath = path.join(fullSubdir, file);
      const content = fs.readFileSync(filePath, 'utf-8');

      // 1. Extrair frontmatter
      const fmMatch = content.match(/^---\r?\n([\s\S]*?)\r?\n---/);
      let title = file.replace('.md', '');
      let taskId = file.split('-').slice(0, 2).join('-');
      let type = 'feature';
      let owner = 'AI Assistant';

      if (fmMatch) {
        const fm = fmMatch[1];
        const titleMatch = fm.match(/title:\s*(.+)/);
        if (titleMatch) title = titleMatch[1].trim();

        const idMatch = fm.match(/id:\s*(.+)/);
        if (idMatch) taskId = idMatch[1].trim();

        const typeMatch = fm.match(/type:\s*(.+)/);
        if (typeMatch) type = typeMatch[1].trim();

        const ownerMatch = fm.match(/owner:\s*(.+)/);
        if (ownerMatch) owner = ownerMatch[1].trim();
      }

      // 2. Extrair a DESCRIÇÃO COMPLETA da TASK (Todo o corpo do Markdown após o frontmatter)
      let fullDescription = content;
      const fmEndIndex = content.indexOf('---', 3);
      if (fmEndIndex !== -1) {
        fullDescription = content.substring(fmEndIndex + 3).trim();
      }

      // Determinar prioridade e status refinado
      let priority = 'MEDIUM';
      let finalStatus = status;

      if (dir === 'completed') {
        finalStatus = 'DONE';
        priority = 'HIGH';
      } else if (dir === 'active') {
        if (file.includes('008') || file.includes('009')) {
          finalStatus = 'IN_REVIEW';
          priority = file.includes('008') ? 'HIGH' : 'MEDIUM';
        } else {
          finalStatus = 'IN_PROGRESS';
          priority = 'URGENT';
        }
      }

      const fullTitle = `[${taskId}] ${title}`;

      // Verificar se já existe no Kanban
      const existing = currentTasks.find(t => t.title.includes(taskId) || t.title === fullTitle);

      if (existing) {
        // Atualizar com a descrição COMPLETA e dados atuais
        const updateRes = await fetch(`${apiUrl}/api/tasks/${existing.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json; charset=utf-8' },
          body: JSON.stringify({
            title: fullTitle,
            description: fullDescription,
            status: finalStatus,
            priority,
            assignee: owner,
            labels: [type, 'synced', dir]
          })
        });

        if (updateRes.ok) {
          console.log(`[Atualizada] #${existing.id} ${fullTitle} -> Descrição completa (${fullDescription.length} caracteres)`);
        } else {
          console.error(`[Falha] Erro ao atualizar #${existing.id}: ${updateRes.status}`);
        }
      } else {
        // Criar nova com descrição completa
        const createRes = await fetch(`${apiUrl}/api/tasks`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            title: fullTitle,
            description: fullDescription,
            status: finalStatus,
            priority,
            assignee: owner,
            labels: [type, 'synced', dir]
          })
        });
        const created = await createRes.json();
        console.log(`[Criada] #${created.id} ${fullTitle} [${finalStatus}] -> Descrição completa (${fullDescription.length} caracteres)`);
      }

      syncedCount++;
    }
  }

  console.log(`[Kanban Sync] Sincronização de descrições completas concluída! Total processado: ${syncedCount}`);
}

scanAndSync();
