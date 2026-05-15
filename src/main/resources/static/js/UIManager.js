export default class UIManager {
    constructor() {
        this.days = ['Domingo','Segunda-feira','Terça-feira','Quarta-feira','Quinta-feira','Sexta-feira','Sábado'];
        this.months = ['janeiro','fevereiro','março','abril','maio','junho','julho','agosto','setembro','outubro','novembro','dezembro'];
    }

    initDate() {
        const now = new Date();
        const dayName = this.days[now.getDay()];
        const d = now.getDate();
        const m = this.months[now.getMonth()];
        const y = now.getFullYear();
        document.getElementById('day-label').textContent = dayName;
        document.getElementById('day-title').textContent = `${d} de ${m} de ${y}`;
        document.getElementById('header-date').textContent = `${dayName}, ${d} de ${m}`;
        document.getElementById('f-date').value = now.toISOString().split('T')[0];
    }

    showToast(msg, type = '') {
        const t = document.getElementById('toast');
        t.textContent = msg;
        t.className = 'toast show ' + type;
        setTimeout(() => { t.className = 'toast'; }, 3000);
    }

    setLoading(isLoading) {
        const container = document.getElementById('tasks-container');
        if (isLoading) {
            container.innerHTML = '<div class="loading"><div class="spinner"></div> Carregando tarefas...</div>';
        }
    }

    showError(message) {
        const container = document.getElementById('tasks-container');
        container.innerHTML = `<div class="empty-state"><div class="icon">⚠️</div><p>Não foi possível conectar à API.<br><small style="color:var(--text-light)">${message}</small></p></div>`;
    }

    renderTasks(list) {
        const container = document.getElementById('tasks-container');
        
        if (!list.length) {
            container.innerHTML = `<div class="empty-state"><div class="icon">📋</div><p>Nenhuma tarefa encontrada.</p></div>`;
            return;
        }

        container.innerHTML = `<div class="tasks-list">${list.map(task => `
            <div class="task-card ${task.status === 'DONE' ? 'done' : ''}" id="card-${task.idTask}">
                <div class="check-wrap">
                    <button class="check-btn ${task.status === 'DONE' ? 'checked' : ''}" onclick="app.toggleDone(${task.idTask})" title="Marcar como concluída">
                        <svg viewBox="0 0 12 12"><polyline points="2 6 5 9 10 3"/></svg>
                    </button>
                </div>
                <div class="task-body">
                    <div class="task-name">${this.escHtml(task.name)}</div>
                    ${task.description ? `<div class="task-desc">${this.escHtml(task.description)}</div>` : ''}
                    <div class="task-meta">
                        ${task.dataTask ? `<span class="task-date">
                            <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                            ${this.formatDate(task.dataTask)}
                        </span>` : ''}
                        <span class="status-pill status-${task.status}">${this.statusLabel(task.status)}</span>
                    </div>
                </div>
                <div class="task-actions">
                    <button class="icon-btn" onclick="app.editTask(${task.idTask})" title="Editar">
                        <svg viewBox="0 0 24 24"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                    <button class="icon-btn del" onclick="app.deleteTask(${task.idTask})" title="Excluir">
                        <svg viewBox="0 0 24 24"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 01-2 2H8a2 2 0 01-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4h6v2"/></svg>
                    </button>
                </div>
            </div>
        `).join('')}</div>`;
    }

    updateCount(tasks) {
        const done = tasks.filter(t => t.status === 'DONE').length;
        document.getElementById('task-count').textContent =
            tasks.length === 0
                ? 'Nenhuma tarefa ainda'
                : `${done} de ${tasks.length} tarefa${tasks.length !== 1 ? 's' : ''} concluída${done !== 1 ? 's' : ''}`;
    }

    clearForm() {
        document.getElementById('f-name').value = '';
        document.getElementById('f-desc').value = '';
        document.getElementById('f-status').value = 'PENDING';
        document.getElementById('f-date').value = new Date().toISOString().split('T')[0];
        document.getElementById('save-btn').innerHTML = `
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>
            Salvar`;
    }

    populateForm(task) {
        document.getElementById('f-name').value = task.name || '';
        document.getElementById('f-desc').value = task.description || '';
        document.getElementById('f-status').value = task.status || 'PENDING';
        document.getElementById('f-date').value = task.dataTask || '';
        document.getElementById('save-btn').textContent = 'Atualizar';
        window.scrollTo({ top: 0, behavior: 'smooth' });
        document.getElementById('f-name').focus();
    }

    escHtml(str) {
        return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }

    formatDate(dateStr) {
        if (!dateStr) return '';
        const [y, m, d] = dateStr.split('-');
        return `${d}/${m}/${y}`;
    }

    renderDashboard(tasks) {
        const total = tasks.length;
        const done = tasks.filter(t => t.status === 'DONE').length;
        const pending = tasks.filter(t => t.status === 'PENDING').length;
        const inProgress = tasks.filter(t => t.status === 'IN_PROGRESS').length;
        const percent = total > 0 ? Math.round((done / total) * 100) : 0;

        const dashboard = document.getElementById('dashboard-container');
        if (!dashboard) return;

        dashboard.innerHTML = `
            <div class="dashboard-grid">
                <div class="dash-card">
                    <div class="dash-val">${total}</div>
                    <div class="dash-label">Total</div>
                </div>
                <div class="dash-card">
                    <div class="dash-val" style="color: #c9920a">${pending}</div>
                    <div class="dash-label">Pendentes</div>
                </div>
                <div class="dash-card">
                    <div class="dash-val" style="color: #5b9bd5">${inProgress}</div>
                    <div class="dash-label">Fazendo</div>
                </div>
                <div class="dash-card">
                    <div class="dash-val" style="color: var(--accent)">${done}</div>
                    <div class="dash-label">Concluídas</div>
                </div>
            </div>
            <div class="progress-wrap">
                <div class="progress-bar" style="width: ${percent}%"></div>
                <span class="progress-text">${percent}% Completo</span>
            </div>
        `;
    }

    renderAdminUsers(users) {
        const container = document.getElementById('admin-users-container');
        if (!users.length) {
            container.innerHTML = '<p style="text-align:center; padding:2rem; color:var(--text-light)">Nenhum usuário cadastrado.</p>';
            return;
        }

        container.innerHTML = `
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th>E-mail</th>
                        <th>Cargo</th>
                    </tr>
                </thead>
                <tbody>
                    ${users.map(u => `
                        <tr>
                            <td>${u.id}</td>
                            <td>${this.escHtml(u.name || '')}</td>
                            <td>${this.escHtml(u.email)}</td>
                            <td><span class="status-pill status-DONE">${u.role || 'USER'}</span></td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    statusLabel(s) {
        const map = { PENDING: 'Pendente', IN_PROGRESS: 'Em andamento', DONE: 'Concluída' };
        return map[s] || s;
    }

    setSaveButtonState(disabled) {
        document.getElementById('save-btn').disabled = disabled;
    }
}
