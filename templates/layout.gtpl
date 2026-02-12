<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title ?: 'Система учета заработной платы'}</title>
    <link rel="stylesheet" href="/static/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏭 Система учета заработной платы</h1>
            <nav>
                <a href="/" ${page == 'home' ? 'class="active"' : ''}>Главная</a>
                <a href="/workers" ${page == 'workers' ? 'class="active"' : ''}>Работники</a>
                <a href="/payroll" ${page == 'payroll' ? 'class="active"' : ''}>Начисления</a>
                <a href="/departments" ${page == 'departments' ? 'class="active"' : ''}>Цеха</a>
                <a href="/logs" ${page == 'logs' ? 'class="active"' : ''}>Логи</a>
            </nav>
        </header>

        ${contents}

        <footer>
            <p>Система учета заработной платы &copy; 2026</p>
            <p>Версия: 1.0 | Groovy ${GroovySystem.version}</p>
        </footer>
    </div>
    <script src="/static/js/script.js"></script>
</body>
</html>