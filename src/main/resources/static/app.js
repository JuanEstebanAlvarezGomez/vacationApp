// Variables globales
let currentUser = null;
let token = null;

// Elementos del DOM
const roleSelect = document.getElementById('role-select');
const loginBtn = document.getElementById('login-btn');
const contentDiv = document.getElementById('content');
const usernameDisplay = document.getElementById('username-display');
const rolePanel = document.getElementById('role-panel');
const messageDiv = document.getElementById('message');

// Base URL del backend
const API_BASE = 'http://localhost:8080/api';

// --- Utilidades ---

function showMessage(text, type = 'success') {
    messageDiv.textContent = text;
    messageDiv.className = type;
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

function clearMessage() {
    messageDiv.style.display = 'none';
    messageDiv.className = '';
}

function getAuthHeader() {
    if (!currentUser) return {};
    const credentials = btoa(`${currentUser}:pass`);
    return {
        'Authorization': `Basic ${credentials}`,
        'Content-Type': 'application/json'
    };
}

async function apiRequest(endpoint, method = 'GET', body = null) {
    const url = `${API_BASE}${endpoint}`;
    const options = {
        method,
        headers: getAuthHeader()
    };
    if (body) {
        options.body = JSON.stringify(body);
    }
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Error ${response.status}: ${errorText}`);
        }
        if (response.status === 204) {
            return null;
        }
        return await response.json();
    } catch (error) {
        showMessage(`Error: ${error.message}`, 'error');
        throw error;
    }
}

// --- Funciones de renderizado según rol ---

function renderEmployeePanel() {
    rolePanel.innerHTML = `
        <div class="section">
            <h3>Solicitar Vacaciones</h3>
            <form id="request-form">
                <div>
                    <label>Fecha inicio:</label>
                    <input type="date" id="start-date" required>
                </div>
                <div style="margin-top: 8px;">
                    <label>Fecha fin:</label>
                    <input type="date" id="end-date" required>
                </div>
                <div style="margin-top: 8px;">
                    <label>Motivo:</label>
                    <input type="text" id="reason" placeholder="Ej: Vacaciones familiares" required>
                </div>
                <button type="submit" style="margin-top: 10px;">Crear solicitud</button>
            </form>
        </div>
        <div class="section">
            <h3>Mis solicitudes</h3>
            <button id="refresh-employee" class="info">Actualizar listado</button>
            <div id="employee-requests"></div>
        </div>
    `;

    // Evento para crear solicitud
    document.getElementById('request-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        clearMessage();
        const startDate = document.getElementById('start-date').value;
        const endDate = document.getElementById('end-date').value;
        const reason = document.getElementById('reason').value.trim();

        if (!startDate || !endDate || !reason) {
            showMessage('Todos los campos son obligatorios.', 'error');
            return;
        }

        try {
            const body = { startDate, endDate, reason };
            await apiRequest('/employee/requests', 'POST', body);
            showMessage('Solicitud creada con éxito.');
            document.getElementById('request-form').reset();
            loadEmployeeRequests();
        } catch (error) {
        }
    });

    // Evento para actualizar listado
    document.getElementById('refresh-employee').addEventListener('click', loadEmployeeRequests);

    // Cargar solicitudes al inicio
    loadEmployeeRequests();
}

async function loadEmployeeRequests() {
    const container = document.getElementById('employee-requests');
    container.innerHTML = '<p class="loading">Cargando...</p>';
    try {
        const data = await apiRequest('/employee/requests');
        if (data.length === 0) {
            container.innerHTML = '<p>No tienes solicitudes.</p>';
            return;
        }
        let html = '<table><thead><tr><th>ID</th><th>Desde</th><th>Hasta</th><th>Motivo</th><th>Estado</th><th>Comentario Jefe</th><th>Comentario RRHH</th></tr></thead><tbody>';
        data.forEach(req => {
            const statusText = translateStatus(req.status);
            html += `<tr>
                <td>${req.id}</td>
                <td>${req.startDate}</td>
                <td>${req.endDate}</td>
                <td>${req.reason}</td>
                <td>${statusText}</td>
                <td>${req.bossComment || '-'}</td>
                <td>${req.hrComment || '-'}</td>
            </tr>`;
        });
        html += '</tbody></table>';
        container.innerHTML = html;
    } catch (error) {
        container.innerHTML = '<p>Error al cargar solicitudes.</p>';
    }
}

function renderBossPanel() {
    rolePanel.innerHTML = `
        <div class="section">
            <h3>Solicitudes pendientes de aprobación</h3>
            <button id="refresh-boss" class="info">Actualizar</button>
            <div id="boss-pending"></div>
        </div>
    `;

    document.getElementById('refresh-boss').addEventListener('click', loadBossPending);
    loadBossPending();
}

async function loadBossPending() {
    const container = document.getElementById('boss-pending');
    container.innerHTML = '<p class="loading">Cargando...</p>';
    try {
        const data = await apiRequest('/boss/pending');
        if (data.length === 0) {
            container.innerHTML = '<p>No hay solicitudes pendientes.</p>';
            return;
        }
        let html = '<table><thead><tr><th>ID</th><th>Empleado</th><th>Desde</th><th>Hasta</th><th>Motivo</th><th>Acciones</th></tr></thead><tbody>';
        data.forEach(req => {
            html += `<tr>
                <td>${req.id}</td>
                <td>${req.employee.fullName} (${req.employee.username})</td>
                <td>${req.startDate}</td>
                <td>${req.endDate}</td>
                <td>${req.reason}</td>
                <td>
                    <button class="approve-btn" data-id="${req.id}">Aprobar</button>
                    <button class="reject-btn danger" data-id="${req.id}">Rechazar</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        container.innerHTML = html;

        // Eventos para aprobar/rechazar
        document.querySelectorAll('.approve-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                const comment = prompt('Comentario de aprobación:');
                if (comment === null) return;
                try {
                    await apiRequest(`/boss/requests/${id}/approve`, 'PUT', { comment });
                    showMessage('Solicitud aprobada.');
                    loadBossPending();
                } catch (error) {}
            });
        });

        document.querySelectorAll('.reject-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                const comment = prompt('Comentario de rechazo:');
                if (comment === null) return;
                try {
                    await apiRequest(`/boss/requests/${id}/reject`, 'PUT', { comment });
                    showMessage('Solicitud rechazada.');
                    loadBossPending();
                } catch (error) {}
            });
        });

    } catch (error) {
        container.innerHTML = '<p>Error al cargar pendientes.</p>';
    }
}

function renderHRPanel() {
    rolePanel.innerHTML = `
        <div class="section">
            <h3>Solicitudes pendientes de RRHH</h3>
            <button id="refresh-hr" class="info">Actualizar</button>
            <div id="hr-pending"></div>
        </div>
        <div class="section">
            <h3>Consultar saldo de un empleado</h3>
            <div>
                <label>ID del empleado:</label>
                <input type="number" id="employee-id-balance" placeholder="Ej: 1">
                <button id="check-balance">Consultar</button>
            </div>
            <div id="balance-result"></div>
        </div>
    `;

    document.getElementById('refresh-hr').addEventListener('click', loadHRPending);
    document.getElementById('check-balance').addEventListener('click', checkBalance);
    loadHRPending();
}

async function loadHRPending() {
    const container = document.getElementById('hr-pending');
    container.innerHTML = '<p class="loading">Cargando...</p>';
    try {
        const data = await apiRequest('/hr/pending');
        if (data.length === 0) {
            container.innerHTML = '<p>No hay solicitudes pendientes de RRHH.</p>';
            return;
        }
        let html = '<table><thead><tr><th>ID</th><th>Empleado</th><th>Desde</th><th>Hasta</th><th>Motivo</th><th>Comentario Jefe</th><th>Acciones</th></tr></thead><tbody>';
        data.forEach(req => {
            html += `<tr>
                <td>${req.id}</td>
                <td>${req.employee.fullName} (${req.employee.username})</td>
                <td>${req.startDate}</td>
                <td>${req.endDate}</td>
                <td>${req.reason}</td>
                <td>${req.bossComment || '-'}</td>
                <td>
                    <button class="confirm-btn" data-id="${req.id}">Confirmar</button>
                    <button class="reject-hr-btn danger" data-id="${req.id}">Rechazar</button>
                </td>
            </tr>`;
        });
        html += '</tbody></table>';
        container.innerHTML = html;

        document.querySelectorAll('.confirm-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                const comment = prompt('Comentario de confirmación:');
                if (comment === null) return;
                try {
                    await apiRequest(`/hr/requests/${id}/confirm`, 'PUT', { comment });
                    showMessage('Solicitud confirmada (días descontados).');
                    loadHRPending();
                } catch (error) {}
            });
        });

        document.querySelectorAll('.reject-hr-btn').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                const comment = prompt('Comentario de rechazo:');
                if (comment === null) return;
                try {
                    await apiRequest(`/hr/requests/${id}/reject`, 'PUT', { comment });
                    showMessage('Solicitud rechazada por RRHH.');
                    loadHRPending();
                } catch (error) {}
            });
        });

    } catch (error) {
        container.innerHTML = '<p>Error al cargar pendientes.</p>';
    }
}

async function checkBalance() {
    const idInput = document.getElementById('employee-id-balance');
    const id = idInput.value.trim();
    if (!id) {
        showMessage('Ingresa un ID de empleado.', 'error');
        return;
    }
    const resultDiv = document.getElementById('balance-result');
    resultDiv.innerHTML = '<p class="loading">Consultando...</p>';
    try {
        const data = await apiRequest(`/hr/balances/${id}`);
        resultDiv.innerHTML = `
            <p><strong>Empleado:</strong> ${data.user.fullName} (${data.user.username})</p>
            <p><strong>Año:</strong> ${data.year}</p>
            <p><strong>Días totales:</strong> ${data.totalDays}</p>
            <p><strong>Días usados:</strong> ${data.usedDays}</p>
            <p><strong>Días restantes:</strong> ${data.remainingDays}</p>
        `;
    } catch (error) {
        resultDiv.innerHTML = '<p>Error al consultar saldo.</p>';
    }
}

// --- Función de traducción de estados ---

function translateStatus(status) {
    const map = {
        'PENDING_BOSS': 'Pendiente de jefe',
        'APPROVED_BY_BOSS': 'Aprobado por jefe',
        'REJECTED_BY_BOSS': 'Rechazado por jefe',
        'PENDING_HR': 'Pendiente de RRHH',
        'CONFIRMED_BY_HR': 'Confirmado por RRHH',
        'REJECTED_BY_HR': 'Rechazado por RRHH'
    };
    return map[status] || status;
}

// --- Gestión de login ---

loginBtn.addEventListener('click', () => {
    const selectedUser = roleSelect.value;
    currentUser = selectedUser;
    usernameDisplay.textContent = selectedUser;
    contentDiv.style.display = 'block';
    clearMessage();

    // Renderizar panel según rol
    if (selectedUser === 'employee1') {
        renderEmployeePanel();
    } else if (selectedUser === 'boss1') {
        renderBossPanel();
    } else if (selectedUser === 'hr1') {
        renderHRPanel();
    }
});

// --- Inicio: ocultar contenido hasta login ---
contentDiv.style.display = 'none';