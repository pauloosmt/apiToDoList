import TaskService from './TaskService.js';
import UIManager from './UIManager.js';


class App {
    constructor() {
        this.service = new TaskService();
        this.ui = new UIManager();

        this.tasks = [];
        this.editingId = null;
        this.currentFilter = 'ALL';
        this.currentDateFilter = 'ALL'; // ALL | WEEK | MONTH
        this.isLoginMode = true;
        this.currentEmail = null;
        this.currentView = 'tasks'; // 'tasks' | 'admin'

        this.statusMap = { 'PENDING': 'to_do', 'IN_PROGRESS': 'doing', 'DONE': 'done' };
        this.reverseStatusMap = { 'to_do': 'PENDING', 'doing': 'IN_PROGRESS', 'done': 'DONE' };
    }

    init() {
        this.ui.initDate();
        const savedEmail = localStorage.getItem('user_email');
        if (savedEmail) this.currentEmail = savedEmail;

        if (this.service.getToken()) {
            this.showTasksSection();
            this.loadTasks();
            this.checkAdminStatus();
        } else {
            this.showAuthSection();
        }
    }

    async checkAdminStatus() {
        if (!this.service.getToken()) {
            this.removeAdminButton();
            document.getElementById('admin-container').innerHTML = '';
            return;
        }
        try {
            const html = await this.service.getAdminPanelHTML();
            document.getElementById('admin-container').innerHTML = html;
            this.injectAdminButton();
        } catch (e) {
            document.getElementById('admin-container').innerHTML = '';
            this.removeAdminButton();
        }
    }

    injectAdminButton() {
        let btn = document.getElementById('admin-btn');
        if (!btn) {
            btn = document.createElement('button');
            btn.id = 'admin-btn';
            btn.className = 'btn btn-ghost';
            btn.style.padding = '4px 10px';
            btn.style.fontSize = '11px';
            btn.textContent = 'Painel Admin';
            btn.onclick = () => this.showAdminSection();
            const headerRight = document.querySelector('.header-right');
            if (headerRight) {
                headerRight.insertBefore(btn, headerRight.firstChild);
            }
        }
    }

    removeAdminButton() {
        const btn = document.getElementById('admin-btn');
        if (btn) {
            btn.remove();
        }
    }

    showAuthSection() {
        document.getElementById('auth-section').style.display = 'block';
        document.getElementById('tasks-section').style.display = 'none';
        const adminSec = document.getElementById('admin-section');
        if (adminSec) adminSec.style.display = 'none';
    }

    showTasksSection() {
        document.getElementById('auth-section').style.display = 'none';
        document.getElementById('tasks-section').style.display = 'block';
        const adminSec = document.getElementById('admin-section');
        if (adminSec) adminSec.style.display = 'none';
        this.currentView = 'tasks';
    }

    async showAdminSection() {
        let adminSec = document.getElementById('admin-section');
        if (!adminSec) {
            try {
                const html = await this.service.getAdminPanelHTML();
                document.getElementById('admin-container').innerHTML = html;
                adminSec = document.getElementById('admin-section');
            } catch (e) {
                this.showTasksSection();
                return;
            }
        }
        if (adminSec) {
            document.getElementById('auth-section').style.display = 'none';
            document.getElementById('tasks-section').style.display = 'none';
            adminSec.style.display = 'block';
            this.currentView = 'admin';
            this.loadAdminData();
        } else {
            this.showTasksSection();
        }
    }

    toggleAuthMode() {
        this.isLoginMode = !this.isLoginMode;
        const btn = document.getElementById('btn-login');
        const toggleLink = document.getElementById('toggle-auth-mode');
        if (this.isLoginMode) {
            btn.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg> Entrar`;
            toggleLink.textContent = 'Registre-se';
            toggleLink.previousSibling.textContent = 'Não tem conta? ';
        } else {
            btn.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><line x1="20" y1="8" x2="20" y2="14"/><line x1="23" y1="11" x2="17" y2="11"/></svg> Registrar`;
            toggleLink.textContent = 'Faça login';
            toggleLink.previousSibling.textContent = 'Já tem conta? ';
        }
    }

    async authenticate() {
        const email = document.getElementById('auth-email').value.trim();
        const password = document.getElementById('auth-password').value;
        if (!email || !password) { this.ui.showToast('Informe e-mail e senha.', 'error'); return; }

        const btn = document.getElementById('btn-login');
        btn.disabled = true;
        try {
            if (this.isLoginMode) {
                await this.service.login(email, password);
                this.ui.showToast('Login realizado com sucesso!', 'success');
            } else {
                await this.service.register(email, password);
                this.ui.showToast('Conta criada com sucesso!', 'success');
            }
            this.currentEmail = email;
            localStorage.setItem('user_email', email);
            this.showTasksSection();
            this.loadTasks();
            this.checkAdminStatus();
            document.getElementById('auth-password').value = '';
        } catch (e) {
            this.ui.showToast(e.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    logout() {
        this.service.setToken(null);
        this.currentEmail = null;
        localStorage.removeItem('user_email');
        this.removeAdminButton();
        document.getElementById('admin-container').innerHTML = '';
        this.tasks = [];
        this.ui.renderTasks([]);
        this.showAuthSection();
        this.ui.showToast('Sessão encerrada.', 'success');
    }

    async loadTasks() {
        this.ui.setLoading(true);
        try {
            const rawTasks = await this.service.getTasks();
            this.tasks = rawTasks.map(t => ({
                ...t,
                idTask: t.id,
                status: this.reverseStatusMap[t.situation] || 'PENDING'
            }));
            this.renderFilteredTasks();
            this.ui.updateCount(this.tasks);
            this.ui.renderDashboard(this.tasks);
        } catch (e) {
            if (e.message === 'UNAUTHORIZED') {
                this.logout();
                this.ui.showToast('Sessão expirada. Faça login novamente.', 'error');
            } else {
                this.ui.showError(e.message);
            }
        }
    }

    setFilter(filter, btnElement) {
        this.currentFilter = filter;
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        if (btnElement) btnElement.classList.add('active');
        this.renderFilteredTasks();
    }

    setDateFilter(dateFilter, btnElement) {
        this.currentDateFilter = dateFilter;
        document.querySelectorAll('.date-filter-btn').forEach(b => b.classList.remove('active'));
        if (btnElement) btnElement.classList.add('active');
        this.renderFilteredTasks();
    }

    getDateRange(filter) {
        const now = new Date();
        const today = now.toISOString().split('T')[0];
        if (filter === 'WEEK') {
            const start = new Date(now);
            start.setDate(now.getDate() - now.getDay());
            const end = new Date(start);
            end.setDate(start.getDate() + 6);
            return { start: start.toISOString().split('T')[0], end: end.toISOString().split('T')[0] };
        }
        if (filter === 'MONTH') {
            const start = new Date(now.getFullYear(), now.getMonth(), 1);
            const end = new Date(now.getFullYear(), now.getMonth() + 1, 0);
            return { start: start.toISOString().split('T')[0], end: end.toISOString().split('T')[0] };
        }
        return null;
    }

    renderFilteredTasks() {
        let list = this.tasks;

        // Status filter
        if (this.currentFilter !== 'ALL') {
            list = list.filter(t => t.status === this.currentFilter);
        }

        // Date filter
        if (this.currentDateFilter !== 'ALL') {
            const range = this.getDateRange(this.currentDateFilter);
            if (range) {
                list = list.filter(t => {
                    if (!t.dataTask) return false;
                    return t.dataTask >= range.start && t.dataTask <= range.end;
                });
            }
        }

        // Sort by date ascending, then by idTask ascending
        list = [...list].sort((a, b) => {
            if (!a.dataTask && !b.dataTask) return a.idTask - b.idTask;
            if (!a.dataTask) return 1;
            if (!b.dataTask) return -1;
            const dateComp = a.dataTask.localeCompare(b.dataTask);
            if (dateComp !== 0) return dateComp;
            return a.idTask - b.idTask;
        });

        this.ui.renderTasks(list);
    }

    async saveTask() {
        const name = document.getElementById('f-name').value.trim();
        if (!name) { this.ui.showToast('Informe o nome da tarefa.', 'error'); return; }

        const uiStatus = document.getElementById('f-status').value;
        const body = {
            name,
            description: document.getElementById('f-desc').value.trim() || '',
            status: this.statusMap[uiStatus],
            dataTask: document.getElementById('f-date').value || null
        };

        this.ui.setSaveButtonState(true);
        try {
            if (this.editingId) {
                await this.service.updateTask(this.editingId, body);
                this.ui.showToast('Tarefa atualizada!', 'success');
            } else {
                await this.service.createTask(body);
                this.ui.showToast('Tarefa criada!', 'success');
            }
            this.clearForm();
            await this.loadTasks();
        } catch (e) {
            if (e.message === 'UNAUTHORIZED') { this.logout(); this.ui.showToast('Sessão expirada.', 'error'); }
            else this.ui.showToast('Erro ao salvar: ' + e.message, 'error');
        } finally {
            this.ui.setSaveButtonState(false);
        }
    }

    editTask(id) {
        const task = this.tasks.find(t => t.idTask === id);
        if (!task) return;
        this.editingId = id;
        this.ui.populateForm(task);
    }

    async deleteTask(id) {
        if (!confirm('Excluir esta tarefa?')) return;
        try {
            await this.service.deleteTask(id);
            this.ui.showToast('Tarefa excluída.', 'success');
            await this.loadTasks();
        } catch (e) {
            if (e.message === 'UNAUTHORIZED') { this.logout(); this.ui.showToast('Sessão expirada.', 'error'); }
            else this.ui.showToast('Erro ao excluir: ' + e.message, 'error');
        }
    }

    async toggleDone(id) {
        const task = this.tasks.find(t => t.idTask === id);
        if (!task) return;
        const newStatus = task.status === 'DONE' ? 'PENDING' : 'DONE';
        const body = { name: task.name, description: task.description || '', status: this.statusMap[newStatus], dataTask: task.dataTask || null };
        try {
            await this.service.updateTask(id, body);
            this.ui.showToast(newStatus === 'DONE' ? 'Tarefa concluída! ✓' : 'Tarefa reaberta.', 'success');
            await this.loadTasks();
        } catch (e) {
            if (e.message === 'UNAUTHORIZED') { this.logout(); this.ui.showToast('Sessão expirada.', 'error'); }
            else this.ui.showToast('Erro: ' + e.message, 'error');
        }
    }

    clearForm() {
        this.editingId = null;
        this.ui.clearForm();
    }

    async loadAdminData() {
        const container = document.getElementById('admin-users-container');
        container.innerHTML = '<div class="loading"><div class="spinner"></div> Carregando usuários...</div>';
        try {
            const users = await this.service.getAllUsers();
            this.ui.renderAdminUsers(users);
        } catch (e) {
            container.innerHTML = `<div class="empty-state"><div class="icon">⚠️</div><p>Erro ao carregar usuários: ${e.message}</p></div>`;
        }
    }
}

const app = new App();
window.app = app;
app.init();
