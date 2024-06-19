//project in (HST, "HOLA CONNECT") AND status = "Wait on user" ORDER BY created DESC, updated DESC
def issueKey = issue.key //'HST-9100'
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    issue = result.body
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def statusData = LocalDateTime.parse(issue.fields.statuscategorychangedate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
def fechaHoy = LocalDateTime.now()
long daysDifference = ChronoUnit.DAYS.between(statusData.toLocalDate(), fechaHoy.toLocalDate())
def transitionId
if (issue.fields.issuetype.id == "10435")transitionId = 51 // Access request
if (issue.fields.issuetype.id == "10138" || issue.fields.issuetype.id == "10218" || issue.fields.issuetype.id == "10217" || 
issue.fields.issuetype.id == "10167")transitionId = 221 //* issue /Bug, Envia sol, Send a q information
if (issue.fields.issuetype.id == "10464")transitionId = 61 // Crash
if (issue.fields.issuetype.id == "10220" || issue.fields.issuetype.id == "10221") transitionId = 111 // Ask a question suggestion
if (issue.fields.issuetype.id == "10463")transitionId = 351 // rollouts

def statusId = issue.fields.status.id
logger.info 'statuscategorychangedate->' + statusData
logger.info 'status->' + statusId
logger.info 'daysDifference->'+daysDifference

if (daysDifference >= 14 && statusId == "10201") {
    logger.info('La fecha de actualización es 14 dias o más a la fecha de hoy.')
    // Realiza las acciones que necesitas cuando la diferencia es de 2 semanas o más
    def resultTrans = post('/rest/api/2/issue/' + issueKey + '/transitions')
                .header('accept', 'application/json')
                .header('Content-Type', 'application/json')
                .body(['transition' : [ 'id': transitionId ]])
                .asJson()
     def commentResp = post("/rest/api/2/issue/" + issueKey +"/comment")
                    .header('Content-Type', 'application/json')
                    .body([
                            body: """There have been no updates on the ticket in the past 2 weeks. We will proceed to close the ticket, and if you require it, you may reopen it within the next 14 days. If you haven't been able to reopen it within this period, we recommend that you open a new ticket. Thank you for your understanding.""" //LasComment//Copy comment by duplicate
                        ])
                    .asObject(Map)   
            // Verifica si la transición fue exitosa
            if ((resultTrans.status >= 200 && resultTrans.status < 300) && (commentResp.status >= 200 && commentResp.status < 300)) {
                logger.info('Transición exitosa: ' + resultTrans.toString())
            } else {
                // Si la transición no fue exitosa, devuelve un mensaje de error
                return "Error en la transición de issue ${issueKey}: ${resultTrans.body} :${commentResp.body} "
            }
} else {
    logger.info('La fecha de actualización es menos de 2 semanas anterior a la fecha de hoy.')
    // Realiza las acciones que necesitas cuando la diferencia es menor de 2 semanas
}
