yieldUnescaped groovy.template("layout.gtpl", [
    page: 'departments',
    title: 'Цеха - Система учета заработной платы',
    contents: '''
        <div class="departments-section">
            <h2>🏭 Цеха предприятия</h2>
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
                        <div class="stat-item">
                            <span class="stat-label">Средняя ЗП:</span>
                            <span class="stat-value">${data.workers > 0 ? String.format("%.2f", data.payroll / data.workers) : '0.00'} ₽</span>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    '''
])