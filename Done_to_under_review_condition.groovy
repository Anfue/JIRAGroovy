// hacer en transicion DOne a Under Review  (Condicion)

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

def issueKey = 'ITSAMPLE-12'// issue.key

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

if (result.status == 200) {
    def fechaHoy = LocalDateTime.now()
    logger.info('Fecha Hoy -> ' + fechaHoy)

    def updatedate = result.body.fields.updated
    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    def dateTime = LocalDateTime.parse(updatedate, formatter)
    logger.info('Fecha Update -> ' + dateTime)

    // Calcular la diferencia en días entre las dos fechas
    long daysDifference = ChronoUnit.DAYS.between(dateTime, fechaHoy)

    if (daysDifference >= 14) {
        logger.info('La fecha de actualización es exactamente 2 semanas posterior a la fecha de hoy.')
        // Realiza las acciones que necesitas cuando la diferencia es de 2 semanas exactamente
    } else {
        logger.info('La fecha de actualización es menos de 1 semanas posterior a la fecha de hoy.')
        // Realiza las acciones que necesitas cuando la diferencia no es de 2 semanas exactamente
    }
} else {
    return "Error al encontrar el problema: Estado: ${result.status} ${result.body}"
}
