yieldUnescaped groovy.template("layout.gtpl", [
    page: 'payroll',
    title: 'Начисления - Система учета заработной платы',
    contents: '''
        <div class="filter-section">
            <h2>💰 Начисления заработной платы</h2>
            <form method="GET" class="filter-form">
                <select name="month" onchange="this.form.submit()">
                    <% months.each { m -> %>
                    <option value="${m.num}" ${month == m.num.toString() ? 'selected' : ''}>${m.name}</option>
                    <% } %>
                </select>
                <select name="year" onchange="this.form.submit()">
                    <option value="2025" ${year == '2025' ? 'selected' : ''}>2025</option>
                    <option value="2024" ${year == '2024' ? 'selected' : ''}>2024</option>
                    <option value="2023" ${year == '2023' ? 'selected' : ''}>2023</option>
                </select>
                <button type="submit">Показать</button>
            </form>
        </div>

        <div class="payroll-table">
            <table>
                <thead>
                    <tr>
                        <th>ФИО</th>
                        <th>Цех</th>
                        <th>Период</th>
                        <th>Больничные</th>
                        <th>Командировки</th>
                        <th>Общая сумма</th>
                    </tr>
                </thead>
                <tbody>
                    <% payroll.each { p -> %>
                    <tr>
                        <td><strong>${p.фамилия} ${p.имя}</strong><br>${p.отчество ?: ''}</td>
                        <td>${p.цех}</td>
                        <td>${p.месяц}.${p.год}</td>
                        <td>${String.format("%.2f", p.зарплата_за_больничные_дни)} ₽</td>
                        <td>${String.format("%.2f", p.зарплата_за_командировочные_дни)} ₽</td>
                        <td><strong>${String.format("%.2f", p.общая_зарплата)} ₽</strong></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    '''
])