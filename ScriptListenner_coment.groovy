//Scrtip listenner en cada comentario de HST

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

def issueKey = 'ITSAMPLE-12'// issue.key
def update

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

if (result.status == 200) {
    def fechaHoy = LocalDateTime.now()
    def fechaHoyFormateada = fechaHoy.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    logger.info('Fecha Hoy -> ' + fechaHoyFormateada)

    update = put('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .body([
            fields: [
                customfield_10068: fechaHoyFormateada // Data comment
            ]
        ])
        .asString()
} else {
    return "Error al encontrar el problema: Estado: ${result.status} ${result.body}"
}

