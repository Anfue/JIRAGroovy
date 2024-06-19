// modifica duedate de los childs a 2 semanas antes
def issueKey = issue.key //'ECOM-515'

// Obtener la información de la issue principal
def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    issue = result.body
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

// Obtener las subtasks
def subtasks = issue.fields.subtasks
def count = subtasks.size()

// Obtener la fecha de vencimiento original
def originalDueDate = issue.fields.duedate
def newDueDate = null

// Calcular la nueva fecha de vencimiento (2 semanas antes)
if (originalDueDate) {
    def originalDueDateTime = Date.parse("yyyy-MM-dd", originalDueDate)
    def twoWeeksBefore = originalDueDateTime - 14
    newDueDate = twoWeeksBefore.format("yyyy-MM-dd")
}
logger.info('originalDueDate->'+originalDueDate+', newDueDate->'+newDueDate)
// Actualizar la fecha de vencimiento de cada subtask
for (subtask in subtasks) {
    def update = put("/rest/api/2/issue/${subtask.key}") 
        .queryString("overrideScreenSecurity", Boolean.TRUE) 
        .header('Content-Type', 'application/json')
        .body([
            fields: [
                duedate: newDueDate
            ]
        ])
        .asString()
}

return 'Proceso completado.'
