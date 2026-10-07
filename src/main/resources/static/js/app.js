const API = "/api";

let selectedCompanyId = null;
let currentTheme = localStorage.getItem('theme') || 'light';

// Initialize theme
document.documentElement.setAttribute('data-theme', currentTheme);
updateThemeIcon();

// API Request Helper
async function request(url, options = {}) {
    const response = await fetch(API + url, {
        headers: {
            "Content-Type": "application/json"
        },
        ...options
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Ошибка сервера: ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

// Toast Notifications
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠'
    };

    toast.innerHTML = `
        <span class="toast-icon">${icons[type]}</span>
        <span class="toast-message">${message}</span>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function showError(error) {
    console.error(error);
    showToast(error.message, 'error');
}

// Modal Functions
function showModal(title, body, buttons = []) {
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalBody').innerHTML = body;

    const footer = document.getElementById('modalFooter');
    footer.innerHTML = '';

    buttons.forEach(btn => {
        const button = document.createElement('button');
        button.className = btn.class || 'btn-secondary';
        button.textContent = btn.text;
        button.onclick = () => {
            if (btn.action) btn.action();
            closeModal();
        };
        footer.appendChild(button);
    });

    document.getElementById('modalOverlay').classList.add('active');
}

function closeModal() {
    document.getElementById('modalOverlay').classList.remove('active');
}

// Navigation
function navigateTo(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    document.getElementById(`${section}-section`).classList.add('active');
    document.querySelector(`[data-section="${section}"]`).classList.add('active');

    const titles = {
        'dashboard': 'Дашборд',
        'companies': 'Компании',
        'add-company': 'Добавить компанию',
        'holdings': 'Холдинги'
    };

    document.getElementById('pageTitle').textContent = titles[section];

    if (window.innerWidth <= 768) {
        document.querySelector('.sidebar').classList.remove('active');
    }
}

// Theme Toggle
document.getElementById('themeToggle').addEventListener('click', () => {
    currentTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    localStorage.setItem('theme', currentTheme);
    updateThemeIcon();
});

function updateThemeIcon() {
    const icon = document.querySelector('.theme-icon');
    icon.textContent = currentTheme === 'light' ? '🌙' : '☀️';
}

// Menu Toggle
document.getElementById('menuToggle').addEventListener('click', () => {
    document.querySelector('.sidebar').classList.toggle('active');
});

// Nav Items
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo(item.dataset.section);
    });
});

// Company Type Toggle
document.getElementById('companyType').addEventListener('change', function() {
    const isCoal = this.value === 'CoalCompany';
    document.getElementById('coalFields').classList.toggle('hidden', !isCoal);
    document.getElementById('oilFields').classList.toggle('hidden', isCoal);
});

// Load Companies
async function loadCompanies() {
    try {
        const companies = await request('/companies');
        const table = document.getElementById('companiesTable');
        table.innerHTML = '';

        // Update stats
        document.getElementById('totalCompanies').textContent = companies.length;
        document.getElementById('coalCompanies').textContent = companies.filter(c => c.type === 'CoalCompany').length;
        document.getElementById('oilCompanies').textContent = companies.filter(c => c.type === 'OilCompany').length;
        document.getElementById('companyCount').textContent = `Всего компаний: ${companies.length}`;

        // Update recent companies
        const recentList = document.getElementById('recentCompanies');
        recentList.innerHTML = '';
        companies.slice(0, 5).forEach(company => {
            const item = document.createElement('div');
            item.className = 'recent-item';
            item.innerHTML = `
                <div class="recent-item-info">
                    <h4>${escapeHtml(company.name)}</h4>
                    <p>${escapeHtml(company.type === 'CoalCompany' ? 'Угольная' : 'Нефтяная')} • Оборот: ${company.turnover}</p>
                </div>
                <span class="type-badge ${company.type === 'CoalCompany' ? 'type-coal' : 'type-oil'}">
                    ${company.type === 'CoalCompany' ? '⛏️' : '🛢️'}
                </span>
            `;
            item.onclick = () => selectCompany(company.id, company.name);
            recentList.appendChild(item);
        });

        // Populate table
        companies.forEach(company => {
            const row = document.createElement('tr');
            if (company.id === selectedCompanyId) {
                row.classList.add('selected');
            }

            row.onclick = () => selectCompany(company.id, company.name);

            row.innerHTML = `
                <td><strong>#${company.id}</strong></td>
                <td>${escapeHtml(company.name)}</td>
                <td>
                    <span class="type-badge ${company.type === 'CoalCompany' ? 'type-coal' : 'type-oil'}">
                        ${company.type === 'CoalCompany' ? '⛏️ Угольная' : '️ Нефтяная'}
                    </span>
                </td>
                <td><strong>${company.turnover.toLocaleString()}</strong></td>
                <td>${company.holdingName ? escapeHtml(company.holdingName) : '<span style="color: var(--text-muted)">—</span>'}</td>
                <td>${escapeHtml(company.details || '—')}</td>
                <td>
                    <div class="table-actions">
                        <button class="btn-danger table-btn" onclick="event.stopPropagation(); deleteCompany(${company.id})">
                            Удалить
                        </button>
                    </div>
                </td>
            `;

            table.appendChild(row);
        });

    } catch (error) {
        showError(error);
    }
}

// Select Company
function selectCompany(id, name) {
    selectedCompanyId = id;
    document.getElementById('selectedCompanyText').textContent = `ID: ${id} • ${name}`;
    document.getElementById('operationsPanel').classList.add('active');
    loadCompanies();
    showToast(`Выбрана компания: ${name}`, 'success');
}

function toggleOperationsPanel() {
    document.getElementById('operationsPanel').classList.toggle('active');
}

// Create Company
document.getElementById('companyForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const type = document.getElementById('companyType').value;

    const data = {
        name: document.getElementById('companyName').value,
        turnover: Number(document.getElementById('turnover').value),
        type,
        coalVolume: type === 'CoalCompany' ? Number(document.getElementById('coalVolume').value) : null,
        mineCount: type === 'CoalCompany' ? Number(document.getElementById('mineCount').value) : null,
        oilVolume: type === 'OilCompany' ? Number(document.getElementById('oilVolume').value) : null,
        wellCount: type === 'OilCompany' ? Number(document.getElementById('wellCount').value) : null,
        holdingName: document.getElementById('companyHolding').value
    };

    try {
        await request('/companies', {
            method: 'POST',
            body: JSON.stringify(data)
        });

        this.reset();
        showToast('Компания успешно создана', 'success');
        await loadCompanies();
        await loadHoldingOptions();
        navigateTo('companies');

    } catch (error) {
        showError(error);
    }
});

// Delete Company
async function deleteCompany(id) {
    showModal(
        'Подтверждение удаления',
        '<p>Вы уверены, что хотите удалить эту компанию? Это действие нельзя отменить.</p>',
        [
            { text: 'Отмена', class: 'btn-secondary' },
            {
                text: 'Удалить',
                class: 'btn-danger',
                action: async () => {
                    try {
                        await request(`/companies/${id}`, { method: 'DELETE' });

                        if (selectedCompanyId === id) {
                            selectedCompanyId = null;
                            document.getElementById('selectedCompanyText').textContent = 'Компания не выбрана';
                            document.getElementById('operationsPanel').classList.remove('active');
                        }

                        showToast('Компания удалена', 'success');
                        await loadCompanies();
                    } catch (error) {
                        showError(error);
                    }
                }
            }
        ]
    );
}

// Calculate
async function calculate(id = selectedCompanyId) {
    if (!id) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    try {
        await request(`/companies/${id}/calculate`, { method: 'POST' });
        showToast('Расчёт выполнен успешно', 'success');
        await loadCompanies();
    } catch (error) {
        showError(error);
    }
}

async function calculateCompany() {
    await calculate();
}

// Add Mines
async function addMines() {
    if (!selectedCompanyId) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    showModal(
        'Добавить шахты',
        '<div class="form-group"><label class="form-label">Количество шахт</label><input type="number" id="minesCount" class="form-input" min="1" value="1"></div>',
        [
            { text: 'Отмена', class: 'btn-secondary' },
            {
                text: 'Добавить',
                class: 'btn-primary',
                action: async () => {
                    const count = document.getElementById('minesCount').value;
                    if (!count) return;

                    try {
                        await request(`/coal-companies/${selectedCompanyId}/mines?count=${count}`, { method: 'POST' });
                        showToast('Шахты добавлены', 'success');
                        await loadCompanies();
                    } catch (error) {
                        showError(error);
                    }
                }
            }
        ]
    );
}

// Stop Expanding
async function stopExpanding() {
    if (!selectedCompanyId) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    try {
        await request(`/coal-companies/${selectedCompanyId}/stop-expanding`, { method: 'POST' });
        showToast('Производство остановлено', 'success');
        await loadCompanies();
    } catch (error) {
        showError(error);
    }
}

// Add Wells
async function addWells() {
    if (!selectedCompanyId) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    showModal(
        'Добавить скважины',
        '<div class="form-group"><label class="form-label">Количество скважин</label><input type="number" id="wellsCount" class="form-input" min="1" value="1"></div>',
        [
            { text: 'Отмена', class: 'btn-secondary' },
            {
                text: 'Добавить',
                class: 'btn-primary',
                action: async () => {
                    const count = document.getElementById('wellsCount').value;
                    if (!count) return;

                    try {
                        await request(`/oil-companies/${selectedCompanyId}/wells?count=${count}`, { method: 'POST' });
                        showToast('Скважины добавлены', 'success');
                        await loadCompanies();
                    } catch (error) {
                        showError(error);
                    }
                }
            }
        ]
    );
}

// Check Resources
async function checkResources() {
    if (!selectedCompanyId) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    try {
        await request(`/oil-companies/${selectedCompanyId}/check-resources`, { method: 'POST' });
        showToast('Ресурсы проверены', 'success');
        await loadCompanies();
    } catch (error) {
        showError(error);
    }
}

// Remove from Holding
async function removeFromHolding() {
    if (!selectedCompanyId) {
        showToast('Сначала выберите компанию', 'warning');
        return;
    }

    showModal(
        'Убрать из холдинга',
        '<p>Вы уверены, что хотите убрать компанию из холдинга?</p>',
        [
            { text: 'Отмена', class: 'btn-secondary' },
            {
                text: 'Убрать',
                class: 'btn-danger',
                action: async () => {
                    try {
                        await request(`/companies/${selectedCompanyId}/holding`, { method: 'DELETE' });
                        showToast('Компания убрана из холдинга', 'success');
                        await loadCompanies();
                        await loadHoldingOptions();
                    } catch (error) {
                        showError(error);
                    }
                }
            }
        ]
    );
}

// Create Holding
document.getElementById('holdingForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const name = document.getElementById('holdingName').value.trim();
    if (!name) return;

    try {
        await request('/holdings', {
            method: 'POST',
            body: JSON.stringify({ name })
        });

        this.reset();
        showToast('Холдинг создан', 'success');
        await loadHoldingOptions();
        document.getElementById('totalHoldings').textContent = parseInt(document.getElementById('totalHoldings').textContent) + 1;

    } catch (error) {
        showError(error);
    }
});

// Load Holding Options
async function loadHoldingOptions() {
    try {
        const holdings = await request('/holdings');
        const select = document.getElementById('companyHolding');
        select.innerHTML = '<option value="">Без холдинга</option>';

        holdings.forEach(holding => {
            const option = document.createElement('option');
            option.value = holding.name;
            option.textContent = holding.name;
            select.appendChild(option);
        });

        document.getElementById('totalHoldings').textContent = holdings.length;

    } catch (error) {
        showError(error);
    }
}

// Search Holding
async function searchHolding() {
    const name = document.getElementById('searchHolding').value.trim();
    if (!name) {
        showToast('Введите название холдинга', 'warning');
        return;
    }

    try {
        const holding = await request(`/holdings/${encodeURIComponent(name)}`);
        const info = document.getElementById('holdingInfo');

        info.innerHTML = `
            <div class="holding-company-card" style="background: linear-gradient(135deg, var(--primary-light), var(--primary)); color: white; border: none;">
                <div class="holding-company-info">
                    <h4 style="color: white;">${escapeHtml(holding.name)}</h4>
                    <p style="color: rgba(255,255,255,0.9);">Компаний: ${holding.companies.length}</p>
                </div>
            </div>
        `;

        holding.companies.forEach(company => {
            const card = document.createElement('div');
            card.className = 'holding-company-card';
            card.innerHTML = `
                <div class="holding-company-info">
                    <h4>${escapeHtml(company.name)}</h4>
                    <p>${escapeHtml(company.type === 'CoalCompany' ? 'Угольная' : 'Нефтяная')} • ID: ${company.id}</p>
                </div>
                <button class="btn-danger table-btn" onclick="removeCompanyFromHolding('${escapeHtml(holding.name)}', ${company.id})">
                    Отвязать
                </button>
            `;
            info.appendChild(card);
        });

        showToast('Холдинг найден', 'success');

    } catch (error) {
        showError(error);
    }
}

// Remove Company from Holding
async function removeCompanyFromHolding(holdingName, companyId) {
    try {
        await request(`/holdings/${encodeURIComponent(holdingName)}/companies/${companyId}`, { method: 'DELETE' });
        showToast('Компания отвязана от холдинга', 'success');
        await searchHolding();
        await loadCompanies();
    } catch (error) {
        showError(error);
    }
}

// Delete Holding
async function deleteHolding() {
    const name = document.getElementById('searchHolding').value.trim();
    if (!name) {
        showToast('Введите название холдинга', 'warning');
        return;
    }

    showModal(
        'Удалить холдинг',
        `<p>Вы уверены, что хотите удалить холдинг "<strong>${escapeHtml(name)}</strong>"?</p>`,
        [
            { text: 'Отмена', class: 'btn-secondary' },
            {
                text: 'Удалить',
                class: 'btn-danger',
                action: async () => {
                    try {
                        await request(`/holdings/${encodeURIComponent(name)}`, { method: 'DELETE' });
                        document.getElementById('holdingInfo').innerHTML = '';
                        document.getElementById('searchHolding').value = '';
                        showToast('Холдинг удалён', 'success');
                        await loadCompanies();
                        await loadHoldingOptions();
                    } catch (error) {
                        showError(error);
                    }
                }
            }
        ]
    );
}

// Search Companies
document.getElementById('searchCompanies')?.addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase();
    const rows = document.querySelectorAll('#companiesTable tr');

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
});

// Escape HTML
function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

// Initialize
document.addEventListener('DOMContentLoaded', async () => {
    try {
        await loadCompanies();
        await loadHoldingOptions();
    } catch (error) {
        showError(error);
    }
});

// Close modal on overlay click
document.getElementById('modalOverlay').addEventListener('click', (e) => {
    if (e.target.id === 'modalOverlay') closeModal();
});