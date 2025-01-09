import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

def issueKey = issue.key
/*
def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200) {
    issue = result.body
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
*/
// Método para obtener el historial de cambios
def getChangeHistory(issueKey) {
    def result = get("/rest/api/2/issue/${issueKey}/changelog")
        .header("Content-Type", "application/json")
        .queryString("maxResults", 1000)
        .asObject(Map)

    if (result.status == 200) {
        return result.body
    } else {
        throw new RuntimeException("Failed to fetch change history: ${result.status} ${result.body}")
    }
}

// Obtener la fecha de inicio (fecha de creación de la issue)
def startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(issue.fields.created)
)

// Estados definidos por sus nombres
final List<String> CONTENT_STATES = ["Ready to Content", "Content in Progress", "OK to Publish"]
final List<String> REQUESTER_STATES = ["AEM Validation", "Published", "Ready for Agency", "Agency in Progress"]
final List<String> REQUESTER_STATES2 = ["AEM Validation", "Published", "BLOCKED / PAUSED",  "Ready for Agency", "Agency in Progress"]
List<String> REQUESTER_STATES_Final
// si el campo 10443 tiene PENDING REQUESTER 12672
if( issue.fields.customfield_10443?.id == "12672") REQUESTER_STATES_Final = REQUESTER_STATES2
if( issue.fields.customfield_10443?.id != "12672") REQUESTER_STATES_Final = REQUESTER_STATES


// Añade más festivos aquí
def isWorkday(Calendar calendar) {
    List<String> HOLIDAYS = ["2024-01-01", "2024-01-06", "2024-04-01", "2024-12-25", "2024-12-26", "2024-12-24",  "2024-12-31", 
"2025-01-01", "2025-01-06", "2025-04-18",  "2025-04-21", "2025-05-01", "2025-06-09", "2025-06-24", "2025-08-15", "2025-09-11", "2025-09-24", 
"2025-10-12", "2025-12-06", "2025-12-08", "2025-12-24", "2025-12-25", "2025-12-26", "2025-12-31" ] 
    def dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    def date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime())
    println('date->'+date)
    println('HOLIDAYS->'+HOLIDAYS) // Verificar contenido de HOLIDAYS en logs
    return !(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY || HOLIDAYS.contains(date))
}

// Calcular horas laborables entre dos fechas
def calculateWorkHoursBetween(Calendar start, Calendar end) {
    def totalMillis = 0
    while (!start.after(end)) {
        if (isWorkday(start)) {
            def startMillis = Math.max(start.getTimeInMillis(), getWorkdayStart(start).getTimeInMillis())
            def endMillis = Math.min(end.getTimeInMillis(), getWorkdayEnd(start).getTimeInMillis())
            if (startMillis < endMillis) {
                totalMillis += (endMillis - startMillis)
            }
        }
        start.add(Calendar.DAY_OF_MONTH, 1)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)
    }
    return TimeUnit.MILLISECONDS.toHours(totalMillis)
}

// Obtener el inicio de un día laborable
def getWorkdayStart(Calendar calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar
}

// Obtener el final de un día laborable
def getWorkdayEnd(Calendar calendar) {
    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
        calendar.set(Calendar.HOUR_OF_DAY, 14)
    } else {
        calendar.set(Calendar.HOUR_OF_DAY, 17)
    }
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    return calendar
}

// Función para calcular horas en un conjunto de estados por nombres
def calculateHoursInStates(changeHistory, stateNames) {
    float  totalHours = 0
    def currentStateStart = null
    def dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    changeHistory.values.each { history ->
        def historyCreated = dateFormat.parse(history.created)
        def historyCalendar = Calendar.getInstance()
        historyCalendar.setTime(historyCreated)
        
        history.items.each { item ->
            if (item.field == "status") {
                def fromState = item.fromString
                def toState = item.toString

                if (stateNames.contains(fromState)) {
                    // Salir de un estado de interés
                    if (currentStateStart) {
                        def endCalendar = Calendar.getInstance()
                        endCalendar.setTime(historyCreated)
                        totalHours += calculateWorkHoursBetween(currentStateStart, endCalendar)
                        currentStateStart = null
                    }
                }
                if (stateNames.contains(toState)) {
                    // Entrar a un estado de interés
                    currentStateStart = Calendar.getInstance()
                    currentStateStart.setTime(historyCreated)
                }
            }
        }
    }

    // Si el estado actual sigue activo, sumar hasta ahora
    if (currentStateStart) {
        def nowCalendar = Calendar.getInstance()
        totalHours += calculateWorkHoursBetween(currentStateStart, nowCalendar)
    }

    return totalHours
}

// Obtener SLA estimado según Request Typology
def getEstimatedSLA(issue) {
    def typologyId = issue.fields.customfield_11000?.id
    def estimatedSLA = null

    if (typologyId == "13754")       estimatedSLA = 16 //Legal
    else if (typologyId == "13755")  estimatedSLA = 8   //BBLL
    else if (typologyId == "13756")  estimatedSLA = 4   //Cookies
    else if (typologyId == "13757")  estimatedSLA = 40  //Offers
    else if (typologyId == "13758")  estimatedSLA = 24  //Carworld
    else if (typologyId == "13759")  estimatedSLA = 16  //CC Lite
    else if (typologyId == "13760")  estimatedSLA = 24  //Campaing
    else if (typologyId == "13761")  estimatedSLA = 24  //Landing
    else if (typologyId == "13762")  estimatedSLA = 24  //Page
    else if (typologyId == "13763")  estimatedSLA = 8   //Brand Universe
    else if (typologyId == "13764")  estimatedSLA = 24  //New Forms (CRM)
    else if (typologyId == "13765")  estimatedSLA = 40  //New Forms (Dilema)
    else if (typologyId == "13766")  estimatedSLA = 16  //Old Forms (answer text)
    else if (typologyId == "13768")  estimatedSLA = 120 //MYCO Updates
    else if (typologyId == "13769")  estimatedSLA = 24  //CONNECT
    else if (typologyId == "13770")  estimatedSLA = 40  //FAQs
    else if (typologyId == "13771")  estimatedSLA = 4   //Copy Update
    else if (typologyId == "13772")  estimatedSLA = 8  //Image Update
    else if (typologyId == "13773")  estimatedSLA = 4  //Video Update
    else if (typologyId == "13774")  estimatedSLA = 4  //CTA Update
    else if (typologyId == "13775")  estimatedSLA = 4  //Navigation Maintenance
    else if (typologyId == "13776")  estimatedSLA = 8  //Navigation Creation
    else if (typologyId == "13777")  estimatedSLA = 8  //Home Maintenance
    else if (typologyId == "13778")  estimatedSLA = 60  //Dealers Migration 

    if(issue.fields.customfield_11004) estimatedSLA = issue.fields.customfield_11004
    
    return estimatedSLA
}

// Obtener historial de cambios
def changeHistory = getChangeHistory(issueKey)

// Calcular horas en estados
long hoursInContent = calculateHoursInStates(changeHistory, CONTENT_STATES)
long hoursInRequester = calculateHoursInStates(changeHistory, REQUESTER_STATES_Final)

def TextContent
def TextRequester


// Obtener SLA estimado
def estimatedSLAs = getEstimatedSLA(issue)

if (hoursInContent <= estimatedSLAs) TextContent = "Within SLA"
if (hoursInContent > estimatedSLAs) TextContent = "Over SLA"
if (hoursInRequester <= estimatedSLAs) TextRequester = "Within SLA"
if (hoursInRequester > estimatedSLAs) TextRequester = "Over SLA"

// Resultado

def result = put("/rest/api/2/issue/${issueKey}") 
    .queryString("overrideScreenSecurity", Boolean.TRUE) 
    .header('Content-Type', 'application/json')
    .body([
        fields: [
                customfield_11003: TextContent, // Content SLA,
                customfield_11002: TextRequester // Requester SLA
        ]
    ])
    .asString()

if (result.status == 204) { 
    logger.info ('Success')
} else {
    return "${result.status}: ${result.body}"
}

return "Hours in Content: ${hoursInContent}, Hours in Requester: ${hoursInRequester}, Estimated SLA: ${estimatedSLAs}"
