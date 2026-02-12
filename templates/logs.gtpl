yieldUnescaped groovy.template("layout.gtpl", [
    page: 'logs',
    title: 'Логи - Система учета заработной платы',
    contents: '''
        <div class="logs-section">
            <h2>📝 Логи приложения</h2>
            <div class="logs-container">
                <% logs.each { log -> %>
                <div class="log-entry">
                    <pre>${log}</pre>
                </div>
                <% } %>
            </div>
        </div>
    '''
])