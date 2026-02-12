yieldUnescaped groovy.template("layout.gtpl", [
    page: 'home',
    title: 'Главная - Система учета заработной платы',
    contents: '''
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-content">
                    <h3>Всего работников</h3>
                    <p class="stat-number">${totalWorkers}</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">💰</div>
                <div class="stat-content">
                    <h3>Общая зарплата</h3>
                    <p class="stat-number">${String.format("%.2f", totalPayroll)} ₽</p>
                </div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div class="stat-content">
                    <h3>Период расчета</h3>
                    <p class="stat-number">${currentMonthName} ${currentYear}</p>
                </div>
            </div>
        </div>

        <div class="departments-section">
            <h2>📊 Статистика по цехам</h2>
            <div class="departments-grid">
                <% stats.each { deptName, data -> %>
                <div class="department-card">
                    <h3>${deptName}</h3>
                    <div class="dept-stats">
                        <div class="stat-item">
                            <span class="stat-label">Работников:</span>
                            <span class="stat-value">${data.workers}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">Фонд ЗП:</span>
                            <span class="stat-value">${String.format("%.2f", data.payroll)} ₽</span>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    '''
])