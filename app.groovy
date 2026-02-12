#!/usr/bin/env groovy

// Импорты
import groovy.sql.Sql
import java.nio.file.*
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

// Проверка драйвера SQLite
try {
    Class.forName("org.sqlite.JDBC")
} catch (e) {
    println "❌ Ошибка: драйвер SQLite не найден!"
    println "Выполните: mvn dependency:copy-dependencies -DoutputDirectory=lib"
    System.exit(1)
}

class SalaryApp {
    Sql db
    Path logPath = Paths.get('app.log')
    
    SalaryApp() {
        db = Sql.newInstance("jdbc:sqlite:salary.db", "org.sqlite.JDBC")
        if (!Files.exists(logPath)) {
            Files.createFile(logPath)
        }
        log("Приложение запущено")
        println "✅ База данных подключена"
    }
    
    // Упрощённая запись лога
    void log(String msg) {
        def ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        def logEntry = "[${ts}] ${msg}\n".getBytes(StandardCharsets.UTF_8)
        
        if (Files.exists(logPath)) {
            Files.write(logPath, logEntry, StandardOpenOption.APPEND)
        } else {
            Files.write(logPath, logEntry, StandardOpenOption.CREATE)
        }
    }
    
    String render(String title, String content) {
        """
        <!DOCTYPE html>
        <html lang="ru">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${title} | Система ЗП</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; 
                       background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #333; padding: 20px; }
                .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 20px; 
                             box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; }
                header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; 
                         padding: 30px; text-align: center; }
                nav { display: flex; justify-content: center; gap: 25px; padding: 20px; background: #f8f9fa; }
                nav a { text-decoration: none; color: #667eea; font-weight: 500; padding: 8px 16px; 
                        border-radius: 25px; transition: all 0.3s; }
                nav a:hover, nav a.active { background: #667eea; color: white; }
                main { padding: 40px; }
                h1 { text-align: center; color: #667eea; margin-bottom: 30px; font-size: 2.2em; }
                table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 5px 15px rgba(0,0,0,0.08); 
                        border-radius: 10px; overflow: hidden; margin-top: 20px; }
                th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; 
                     padding: 16px; text-align: left; font-weight: 500; }
                td { padding: 14px 16px; border-bottom: 1px solid #eee; }
                tr:hover { background: #f8f9ff; }
                .stat-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
                             text-align: center; margin: 20px 0; }
                .stat-number { font-size: 2.5em; font-weight: bold; color: #667eea; margin: 10px 0; }
                footer { text-align: center; padding: 25px; background: #f8f9fa; color: #666; font-size: 0.95em; }
                @media (max-width: 768px) { 
                    nav { flex-direction: column; gap: 10px; } 
                    main { padding: 25px; }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <header>
                    <h1>🏭 Система учёта заработной платы</h1>
                </header>
                <nav>
                    <a href="/" ${title == 'Главная' ? 'class="active"' : ''}>Главная</a>
                    <a href="/workers" ${title == 'Работники' ? 'class="active"' : ''}>Работники</a>
                    <a href="/payroll" ${title == 'Начисления' ? 'class="active"' : ''}>Начисления</a>
                    <a href="/logs" ${title == 'Логи' ? 'class="active"' : ''}>Логи</a>
                </nav>
                <main>
                    ${content}
                </main>
                <footer>
                    <p>Система учёта ЗП &copy; 2026 | Groovy ${GroovySystem.version} | SQLite</p>
                </footer>
            </div>
        </body>
        </html>
        """
    }
    
    String homePage() {
        log("Главная страница")
        def totalWorkers = db.firstRow("SELECT COUNT(*) as cnt FROM Работник").cnt
        def totalPayroll = db.firstRow("""
            SELECT SUM(общая_зарплата) as sum 
            FROM Начисление_заработной_платы 
            WHERE год = 2025 AND месяц = 10
        """).sum ?: 0
        
        render("Главная", """
            <div class="stat-card">
                <h2>👥 Всего работников</h2>
                <div class="stat-number">${totalWorkers}</div>
            </div>
            <div class="stat-card">
                <h2>💰 Фонд зарплаты (окт 2025)</h2>
                <div class="stat-number">${String.format("%.0f", totalPayroll)} ₽</div>
            </div>
            <div style="text-align: center; margin-top: 30px; padding: 25px; background: #f8f9fa; border-radius: 15px;">
                <h3>💡 Быстрый доступ</h3>
                <div style="display: flex; justify-content: center; gap: 20px; flex-wrap: wrap; margin-top: 20px;">
                    <a href="/workers" style="display: block; padding: 15px 30px; background: #667eea; color: white; 
                       text-decoration: none; border-radius: 25px; font-weight: 500; transition: all 0.3s;">
                        👥 Просмотр работников
                    </a>
                    <a href="/payroll" style="display: block; padding: 15px 30px; background: #4CAF50; color: white; 
                       text-decoration: none; border-radius: 25px; font-weight: 500; transition: all 0.3s;">
                        💰 Просмотр начислений
                    </a>
                </div>
            </div>
        """)
    }
    
    String workersPage() {
        log("Страница работников")
        def workers = db.rows("""
            SELECT r.id, r.фамилия, r.имя, r.отчество, r.дата_приема,
                   ц.название_цеха as цех,
                   с.название_системы as система_оплаты
            FROM Работник r
            LEFT JOIN Цех ц ON r.цех_id = ц.id
            LEFT JOIN Система_оплаты с ON r.система_оплаты_id = с.id
            ORDER BY r.фамилия, r.имя
        """)
        
        def tableRows = workers.collect { w ->
            """<tr>
                <td><strong>${w.фамилия} ${w.имя}</strong>${w.отчество ? " ${w.отчество}" : ''}</td>
                <td>${w.дата_приема}</td>
                <td>${w.цех ?: '—'}</td>
                <td>${w.система_оплаты}</td>
            </tr>"""
        }.join('')
        
        render("Работники", """
            <h1>👥 Список работников (${workers.size()})</h1>
            <table>
                <thead>
                    <tr>
                        <th>ФИО</th>
                        <th>Дата приёма</th>
                        <th>Цех</th>
                        <th>Система оплаты</th>
                    </tr>
                </thead>
                <tbody>
                    ${tableRows}
                </tbody>
            </table>
        """)
    }
    
    String payrollPage() {
        log("Страница начислений")
        def payroll = db.rows("""
            SELECT н.*, р.фамилия, р.имя, р.отчество, ц.название_цеха as цех
            FROM Начисление_заработной_платы н
            JOIN Учет_рабочего_времени урв ON н.учет_рабочего_времени_id = урв.id
            JOIN Работник р ON урв.работник_id = р.id
            JOIN Цех ц ON р.цех_id = ц.id
            WHERE н.год = 2025 AND н.месяц = 10
            ORDER BY р.фамилия
        """)
        
        def tableRows = payroll.collect { p ->
            """<tr>
                <td><strong>${p.фамилия} ${p.имя}</strong></td>
                <td>${p.цех}</td>
                <td>${String.format("%.2f", p.зарплата_за_больничные_дни)} ₽</td>
                <td>${String.format("%.2f", p.зарплата_за_командировочные_дни)} ₽</td>
                <td><strong>${String.format("%.2f", p.общая_зарплата)} ₽</strong></td>
            </tr>"""
        }.join('')
        
        render("Начисления", """
            <h1>💰 Начисления за октябрь 2025 (${payroll.size()})</h1>
            <table>
                <thead>
                    <tr>
                        <th>Работник</th>
                        <th>Цех</th>
                        <th>Больничные</th>
                        <th>Командировки</th>
                        <th>Общая сумма</th>
                    </tr>
                </thead>
                <tbody>
                    ${tableRows}
                </tbody>
            </table>
        """)
    }
    
    String logsPage() {
        log("Страница логов")
        def logs = []
        if (Files.exists(logPath)) {
            logs = Files.readAllLines(logPath, StandardCharsets.UTF_8).reverse().take(100)
        } else {
            logs = ['Логи пока пусты']
        }
        
        render("Логи", """
            <h1>📝 Логи приложения</h1>
            <div style="background: #222; color: #0f0; padding: 20px; border-radius: 10px; 
                        font-family: monospace; max-height: 600px; overflow-y: auto; margin-top: 20px;">
                ${logs.collect { line -> "<div>${line.encodeAsHTML()}</div>" }.join('')}
            </div>
        """)
    }
}

// Добавляем метод расширения для экранирования HTML
String.metaClass.encodeAsHTML = {
    delegate.toString()
        .replace('&', '&amp;')
        .replace('<', '&lt;')
        .replace('>', '&gt;')
        .replace('"', '&quot;')
        .replace("'", '&#39;')
}

// Запуск сервера
def app
try {
    app = new SalaryApp()
} catch (e) {
    println "❌ Ошибка подключения к БД: ${e.message}"
    System.exit(1)
}

def server = HttpServer.create(new InetSocketAddress(8080), 0)

['/' : { app.homePage() },
 '/workers' : { app.workersPage() },
 '/payroll' : { app.payrollPage() },
 '/logs' : { app.logsPage() }].each { path, handler ->
    server.createContext(path) { exchange ->
        exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
        def resp = handler()
        exchange.sendResponseHeaders(200, resp.length())
        exchange.responseBody.write(resp.getBytes("UTF-8"))
        exchange.responseBody.close()
    }
}

server.setExecutor(null)
server.start()

println """
╔══════════════════════════════════════════════════════════════╗
║  ✅ Сервер запущен: http://localhost:8080                   ║
║  💡 Для остановки нажмите Ctrl+C                            ║
╚══════════════════════════════════════════════════════════════╝
"""
Runtime.runtime.addShutdownHook(new Thread({ 
    server.stop(1) 
    println "\n🛑 Сервер остановлен"
}))