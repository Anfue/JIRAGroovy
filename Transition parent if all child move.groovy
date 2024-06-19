import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

def childIssueKey = issue.key //'MSB-176' // La clave del issue hijo que se está moviendo
def epicTransitionId = '11' // ID de la transición para mover la épica a "Ready"

// Obtener el issue hijo
def childIssueResult = get('/rest/api/2/issue/' + childIssueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (childIssueResult.status != 200) {
    return "Failed to find child issue: Status: ${childIssueResult.status} ${childIssueResult.body}"
}

def childIssue = childIssueResult.body

// Obtener la clave de la épica a la que pertenece el issue hijo
def epicKey = childIssue.fields?.parent?.key // Asegúrate de usar el ID correcto del campo de enlace de épicas
if (!epicKey) {
    return "No epic link found for child issue ${childIssueKey}"
}

// Obtener la épica
def epicIssueResult = get('/rest/api/2/issue/' + epicKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (epicIssueResult.status != 200) {
    return "Failed to find epic issue: Status: ${epicIssueResult.status} ${epicIssueResult.body}"
}

def epicIssue = epicIssueResult.body
def epicStatus = epicIssue.fields.status.name

// Verificar si la épica ya está en estado "Ready"
if (epicStatus == "Ready") {
    return "Epic is already in 'Ready' state"
}

// Obtener los issues hijos de la épica
def epicLinkFieldId = 'customfield_10008' // Asegúrate de usar el ID correcto del campo de enlace de épicas
def jqlQuery = epicLinkFieldId + ' = ' + epicKey
def searchResult = get('/rest/api/2/search')
        .header('Content-Type', 'application/json')
        .queryString('jql', jqlQuery)
        .queryString('fields', 'key,status')
        .queryString('maxResults', 1000) // Puedes ajustar el número máximo de resultados según sea necesario
        .asObject(Map)

if (searchResult.status != 200) {
    return "Failed to find child issues: Status: ${searchResult.status} ${searchResult.body}"
}

def issuechilds = searchResult.body.issues

// Verificar si todos los hijos están en un estado distinto de "To Do"
def allNotToDo = issuechilds.every { issue ->
    def childStatus = issue.fields.status.name
    return childStatus != 'To Do'
}

if (allNotToDo) {
    // Mover la épica al estado "Ready"
    def transitionResult = post('/rest/api/2/issue/' + epicKey + '/transitions')
        .header('Content-Type', 'application/json')
        .body([
            transition: [
                id: epicTransitionId
            ]
        ])
        .asString()

    if (transitionResult.status != 204) {
        return "Failed to transition epic ${epicKey}: Status: ${transitionResult.status} ${transitionResult.body}"
    }

    return "Epic transitioned to 'Ready' state successfully"
}

return "Not all child issues are in a state other than 'To Do'"
