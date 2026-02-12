<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Работники - Система учета заработной платы</title>
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>👥 Работники предприятия</h1>
            <nav>
                <a href="/">Главная</a>
                <a href="/workers" class="active">Работники</a>
                <a href="/payroll">Начисления</a>
                <a href="/departments">Цеха</a>
                <a href="/logs">Логи</a>
            </nav>
        </header>

        <button class="btn-add" onclick="toggleAddForm()">➕ Добавить работника</button>

        <div id="addWorkerForm" style="display: none;" class="add-form">
            <h3>Добавить нового работника</h3>
            <form id="newWorkerForm">
                <div class="form-row">
                    <input type="text" name="surname" placeholder="Фамилия *" required>
                    <input type="text" name="name" placeholder="Имя *" required>
                    <input type="text" name="patronymic" placeholder="Отчество">
                </div>
                <div class="form-row">
                    <input type="date" name="hireDate" placeholder="Дата приема *" required>
                    <select name="departmentId" required>
                        <option value="">Выберите цех *</option>
                        <% departments.each { dept -> %>
                        <option value="${dept.id}">${dept.название_цеха}</option>
                        <% } %>
                    </select>
                </div>
                <div class="form-row">
                    <select name="paymentSystemId" required>
                        <option value="">Система оплаты *</option>
                        <% paymentSystems.each { ps -> %>
                        <option value="${ps.id}">${ps.название_системы}</option>
                        <% } %>
                    </select>
                    <select name="categoryId" required>
                        <option value="">Категория *</option>
                        <% categories.each { cat -> %>
                        <option value="${cat.id}">${cat.название_категории}</option>
                        <% } %>
                    </select>
                </div>
                <div class="form-row">
                    <select name="rankId" required>
                        <option value="">Разряд *</option>
                        <% ranks.each { rank -> %>
                        <option value="${rank.id}">${rank.номер_разряда}</option>
                        <% } %>
                    </select>
                    <select name="workModeId" required>
                        <option value="">Режим работы *</option>
                        <% workModes.each { wm -> %>
                        <option value="${wm.id}">${wm.название_режима}</option>
                        <% } %>
                    </select>
                </div>
                <div class="form-row">
                    <select name="salaryId">
                        <option value="">Оклад (если применимо)</option>
                        <% salaries.each { sal -> %>
                        <option value="${sal.id}">${String.format("%.2f", sal.оклад_в_месяц)} ₽</option>
                        <% } %>
                    </select>
                    <select name="hourlyRateId">
                        <option value="">Почасовая ставка (если применимо)</option>
                        <% hourlyRates.each { hr -> %>
                        <option value="${hr.id}">${String.format("%.2f", hr.ставка_в_час)} ₽/час</option>
                        <% } %>
                    </select>
                </div>
                <button type="submit" class="btn-submit">Добавить работника</button>
                <button type="button" onclick="toggleAddForm()" class="btn-cancel">Отмена</button>
            </form>
        </div>

        <div class="workers-table">
            <table>
                <thead>
                    <tr>
                        <th>ФИО</th>
                        <th>Дата приема</th>
                        <th>Цех</th>
                        <th>Система оплаты</th>
                        <th>Категория</th>
                        <th>Разряд</th>
                        <th>Режим работы</th>
                        <th>Оклад/Ставка</th>
                    </tr>
                </thead>
                <tbody>
                    <% workers.each { worker -> %>
                    <tr>
                        <td><strong>${worker.фамилия} ${worker.имя}</strong><br>${worker.отчество ?: ''}</td>
                        <td>${worker.дата_приема}</td>
                        <td>${worker.цех}</td>
                        <td>${worker.система_оплаты}</td>
                        <td>${worker.категория}</td>
                        <td>${worker.разряд}</td>
                        <td>${worker.режим_работы}</td>
                        <td>
                            <% if (worker.оклад) { %>
                            Оклад: ${String.format("%.2f", worker.оклад)} ₽
                            <% } else if (worker.почасовая_ставка) { %>
                            Ставка: ${String.format("%.2f", worker.почасовая_ставка)} ₽/час
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <footer>
            <p>Система учета заработной платы &copy; 2026</p>
        </footer>
    </div>

    <script src="/static/js/script.js"></script>
</body>
</html>