export default class TaskService {
    constructor() {
        this.baseUrl = 'http://localhost:8080';
        this.token = localStorage.getItem('jwt_token') || null;
    }

    setBaseUrl(url) {
        this.baseUrl = url.replace(/\/$/, '');
    }

    getBaseUrl() {
        return this.baseUrl;
    }

    setToken(token) {
        this.token = token;
        if (token) {
            localStorage.setItem('jwt_token', token);
        } else {
            localStorage.removeItem('jwt_token');
        }
    }

    getToken() {
        return this.token;
    }

    getHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }

    async login(email, password) {
        const res = await fetch(`${this.baseUrl}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Erro ao fazer login');
        this.setToken(data.token);
        return data;
    }

    async register(email, password) {
        // Backend UserRequestDTO espera (email, password, role).
        // A controller mapeia "role" opcionalmente, mas UserRequestDTO precisa de email e password.
        const res = await fetch(`${this.baseUrl}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name: email.split('@')[0], email, password })
        });
        if (!res.ok) {
            let errorMsg = 'Erro ao registrar';
            try {
                const data = await res.json();
                errorMsg = data.message || errorMsg;
            } catch (e) {}
            throw new Error(errorMsg);
        }
        // Se registrou, tenta fazer login automático
        return await this.login(email, password);
    }

    async getTasks() {
        const res = await fetch(`${this.baseUrl}/task/all`, {
            headers: this.getHeaders()
        });
        if (res.status === 401) {
            this.setToken(null);
            throw new Error('UNAUTHORIZED');
        }
        if (!res.ok) throw new Error('Erro ' + res.status);
        return await res.json();
    }

    async createTask(taskData) {
        const res = await fetch(`${this.baseUrl}/task/create`, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify(taskData)
        });
        if (res.status === 401) throw new Error('UNAUTHORIZED');
        if (!res.ok) throw new Error('Erro ' + res.status);
        return await res.json();
    }

    async updateTask(id, taskData) {
        const res = await fetch(`${this.baseUrl}/task/update/${id}`, {
            method: 'PUT',
            headers: this.getHeaders(),
            body: JSON.stringify(taskData)
        });
        if (res.status === 401) throw new Error('UNAUTHORIZED');
        if (!res.ok) throw new Error('Erro ' + res.status);
        return await res.json();
    }

    async deleteTask(id) {
        const res = await fetch(`${this.baseUrl}/task/delete/${id}`, { 
            method: 'DELETE',
            headers: this.getHeaders()
        });
        if (res.status === 401) throw new Error('UNAUTHORIZED');
        if (!res.ok) throw new Error('Erro ' + res.status);
        return await res.text();
    }
}
