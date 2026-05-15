import TaskService from './TaskService.js';
import UIManager from './UIManager.js';

class App {
    constructor() {
        this.service = new TaskService();
        this.ui = new UIManager();
        
        this.tasks = [];
        this.editingId = null;
        this.currentFilter = 'ALL';
        this.isLoginMode = true; // true = login, false = register
        
        // Status mapping (Frontend -> Backend)
        this.statusMap = {
            'PENDING': 'to_do',
            'IN_PROGRESS': 'doing',
            'DONE': 'done'
        };
        // Status mapping (Backend -> Frontend)
        this.reverseStatusMap = {
            'to_do': 'PENDING',
            'doing': 'IN_PROGRESS',
            'done': 'DONE'
        };
    }

    init() {
        this.ui.initDate();
        if (this.service.getToken()) {
            this.showTasksSection();
            this.loadTasks();
        } else {
            this.showAuthSection();
        }
    }

    getBaseUrlInput() {
        return document.getElementById('base-url').value;
    }

    connect() {
        this.service.setBaseUrl(this.getBaseUrlInput());
        if (this.service.getToken()) {
            this.loadTasks();
        }
    }

    showAuthSection() {
        document.getElementById('auth-section').style.display = 'block';
        document.getElementById('tasks-section').style.display = 'none';
    }

    showTasksSection() {
        document.getElementById('auth-section').style.display = 'none';
        document.getElementById('tasks-section').style.display = 'block';
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
        if (!email || !password) {
            this.ui.showToast('Informe e-mail e senha.', 'error');
            return;
        }

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
            this.showTasksSection();
            this.loadTasks();
            document.getElementById('auth-password').value = '';
        } catch (e) {
            this.ui.showToast(e.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    logout() {
        this.service.setToken(null);
        this.tasks = [];
        this.ui.renderTasks([]);
        this.showAuthSection();
        this.ui.showToast('Sessão encerrada.', 'success');
    }

    async loadTasks() {
        this.ui.setLoading(true);
        try {
            const rawTasks = await this.service.getTasks();
            // Backend returns { id, name, description, situation, dataTask }
            // Frontend UI expects { idTask, name, description, status, dataTask }
            this.tasks = rawTasks.map(t => ({
                ...t,
                idTask: t.id,
                status: this.reverseStatusMap[t.situation] || 'PENDING'
            }));
            this.renderFilteredTasks();
            this.ui.updateCount(this.tasks);
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
        if(btnElement) btnElement.classList.add('active');

        this.renderFilteredTasks();
    }

    renderFilteredTasks() {
        let list = this.tasks;
        if (this.currentFilter !== 'ALL') {
            list = this.tasks.filter(t => t.status === this.currentFilter);
        }
        this.ui.renderTasks(list);
    }

    async saveTask() {
        const name = document.getElementById('f-name').value.trim();
        if (!name) { 
            this.ui.showToast('Informe o nome da tarefa.', 'error'); 
            return; 
        }

        const uiStatus = document.getElementById('f-status').value;
        const body = {
            name,
            description: document.getElementById('f-desc').value.trim() || '',
            status: this.statusMap[uiStatus], // to_do, doing, done
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
            if (e.message === 'UNAUTHORIZED') {
                this.logout();
                this.ui.showToast('Sessão expirada. Faça login novamente.', 'error');
            } else {
                this.ui.showToast('Erro ao salvar: ' + e.message, 'error');
            }
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
            if (e.message === 'UNAUTHORIZED') {
                this.logout();
                this.ui.showToast('Sessão expirada. Faça login novamente.', 'error');
            } else {
                this.ui.showToast('Erro ao excluir: ' + e.message, 'error');
            }
        }
    }

    async toggleDone(id) {
        const task = this.tasks.find(t => t.idTask === id);
        if (!task) return;
        
        const newStatus = task.status === 'DONE' ? 'PENDING' : 'DONE';
        const body = {
            name: task.name,
            description: task.description || '',
            status: this.statusMap[newStatus],
            dataTask: task.dataTask || null
        };
        
        try {
            await this.service.updateTask(id, body);
            this.ui.showToast(newStatus === 'DONE' ? 'Tarefa concluída! ✓' : 'Tarefa reaberta.', 'success');
            await this.loadTasks();
        } catch (e) {
            if (e.message === 'UNAUTHORIZED') {
                this.logout();
                this.ui.showToast('Sessão expirada. Faça login novamente.', 'error');
            } else {
                this.ui.showToast('Erro: ' + e.message, 'error');
            }
        }
    }

    clearForm() {
        this.editingId = null;
        this.ui.clearForm();
    }
}

const app = new App();
window.app = app;
app.init();
