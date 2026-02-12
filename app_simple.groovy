#!/usr/bin/env groovy

import groovy.sql.Sql
import java.nio.file.*
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class SalaryApp {
    Sql db = Sql.newInstance("jdbc:sqlite:salary.db", "org.sqlite.JDBC")
    Path logPath = Paths.get('app.log')
    
    void log(String msg) {
        def ts = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        Files.write(logPath, ["[${ts}] ${msg}\n".getBytes()], 
                     Files.exists(logPath) ? [StandardOpenOption.APPEND] : [StandardOpenOption.CREATE])
        println "[${ts}] ${msg}"
    }
    
    String getWorkersPage() {
        log("Запрос списка работников")
        def workers = db.rows("SELECT * FROM Работник LIMIT 10")
        """
        <html><head><meta charset='utf-8'><title>Работники</title></head>
        <body>
          <h1>Работники (${workers.size()})</h1>
          <table border='1'>
            <tr><th>ФИО</th><th>Дата приёма</th></tr>
            ${workers.collect { w -> 
                "<tr><td>${w.фамилия} ${w.имя}</td><td>${w.дата_приема}</td></tr>"
            }.join('')}
          </table>
          <p><a href='/'>Главная</a></p>
        </body></html>
        """
    }
    
    String getHomePage() {
        log("Запрос главной страницы")
        """
        <html><head><meta charset='utf-8'><title>Главная</title></head>
        <body>
          <h1>Система учёта ЗП</h1>
          <ul>
            <li><a href='/workers'>Работники</a></li>
            <li><a href='/logs'>Логи</a></li>
          </ul>
        </body></html>
        """
    }
    
    String getLogsPage() {
        log("Запрос логов")
        def logs = Files.exists(logPath) ? Files.readAllLines(logPath).reverse().take(50) : ['Логи пусты']
        """
        <html><head><meta charset='utf-8'><title>Логи</title></head>
        <body>
          <h1>Логи приложения</h1>
          <pre>${logs.join('\n')}</pre>
          <p><a href='/'>Главная</a></p>
        </body></html>
        """
    }
}

def app = new SalaryApp()
def server = HttpServer.create(new InetSocketAddress(8080), 0)

server.createContext("/") { exchange ->
    def resp = app.getHomePage()
    exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
    exchange.sendResponseHeaders(200, resp.length())
    exchange.responseBody.write(resp.getBytes("UTF-8"))
    exchange.responseBody.close()
}

server.createContext("/workers") { exchange ->
    def resp = app.getWorkersPage()
    exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
    exchange.sendResponseHeaders(200, resp.length())
    exchange.responseBody.write(resp.getBytes("UTF-8"))
    exchange.responseBody.close()
}

server.createContext("/logs") { exchange ->
    def resp = app.getLogsPage()
    exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
    exchange.sendResponseHeaders(200, resp.length())
    exchange.responseBody.write(resp.getBytes("UTF-8"))
    exchange.responseBody.close()
}

server.setExecutor(null)
server.start()
println "✅ Сервер запущен: http://localhost:8080"
println "Нажмите Ctrl+C для остановки"
Runtime.runtime.addShutdownHook(new Thread({ server.stop(1); println "Сервер остановлен" }))