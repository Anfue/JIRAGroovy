// job o Listenner Send mail 1 weeks

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

def issueKey = 'ITSAMPLE-12' // issue.key

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

if (result.status == 200) {
    def fechaHoy = LocalDateTime.now()
    logger.info('Fecha Hoy -> ' + fechaHoy)
    def DataComment = result.body.fields.customfield_10068
    def Id_reportero = result.body.fields.reporter.accountId
    def name_reportero = result.body.fields.reporter.displayName
    // Agregar la hora, minutos, segundos y zona horaria predeterminados si no están presentes en DataComment
    if (!DataComment.contains('T')) {
        DataComment = DataComment + 'T00:00:00.000Z'
    }

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    def dateTime = LocalDateTime.parse(DataComment, formatter)
    logger.info('Fecha Update -> ' + dateTime)

    // Calcular la diferencia en días entre las dos fechas
    long daysDifference = ChronoUnit.DAYS.between(dateTime, fechaHoy)

    if (daysDifference >= 7) {
        logger.info('La fecha de actualización es 1 semanas posterior a la fecha de hoy asi que enviamos mail')
        
        if (result.body.fields.status.name == "wait on user"){
             def resp = post("/rest/api/2/issue/${issueKey}/notify")
                    .header("Content-Type", "application/json")
                    .body([
                            subject: 'We are working in ${issueKey}',
                            textBody: "Dear ${name_reportero}

							We would like to inform you that there is an open ticket that requires your attention. Your prompt response in providing the requested information is greatly appreciated as it will help us expedite the resolution process. If we do not receive the necessary details within a reasonable timeframe, it may be necessary to automatically close the ticket. Nevertheless, we want to assure you that you will have the opportunity to reopen the ticket within the next 14 days. If this time limit expires, we kindly ask you to submit a new ticket.
							Thank you for your understanding and cooperation.

							Best regards,

							Hola Support Team",
                            htmlBody: "<p>Body</p>",
                            to: [
                                users: [[
                                        name: Id_reportero,
                                        active: true
                                    ]]
                            ]
                        ])
                    .asString()
                def commentResp = post("/rest/api/2/issue/" + issueKey +"/comment")
                    .header('Content-Type', 'application/json')
                    .body([
                            body: 'Hi ${name_reportero}, 

We would like to inform you that there is an open ticket that requires your attention. Your prompt response in providing the requested information is greatly appreciated as it will help us expedite the resolution process. If we do not receive the necessary details within a reasonable timeframe, it may be necessary to automatically close the ticket. Nevertheless, we want to assure you that you will have the opportunity to reopen the ticket within the next 14 days. If this time limit expires, we kindly ask you to submit a new ticket.
Thank you for your understanding and cooperation.

Best regards,

Hola Support Team'
                        ])
                    .asObject(Map)
        }
        
        
    } else {
        logger.info('La fecha de actualización es menos de 1 semanas posterior a la fecha de hoy.')
        // Realiza las acciones que necesitas cuando la diferencia no es de 2 semanas exactamente
    }
} else {
    return "Error al encontrar el problema: Estado: ${result.status} ${result.body}"
}
