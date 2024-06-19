// Clave de la issue que se va a procesar
def issueKey =  issue.key //"HST-10441" // Reemplaza con la clave de tu issue
def issue
logger.info ('se trabaja sobre la '+issueKey)

// Consulta la información de la issue utilizando la API de Jira
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

// Verifica si la consulta fue exitosa (código de estado 200)
if (result.status == 200){
    issue = result.body
    //return  issue
    // Aquí puedes realizar operaciones con la información de la issue
} else {
    // Si la consulta no fue exitosa, devuelve un mensaje de error
    return "Error al encontrar la issue: Estado: ${result.status} ${result.body}"
}
if (issue.fields.customfield_10010.requestType.id ==  "488"){
def status= issue.fields.status.name
// Obtiene la cantidad de comentarios en la issue
def size = issue.fields.comment.comments.size()
def userId
// Obtiene el último comentario de la issue
def lastcomment=  issue.fields.comment.comments[size-1]
boolean encontrado = false
// Obtiene el ID del autor del último comentario
def autor = lastcomment.author.accountId
def lastcom = lastcomment.body
logger.info('autor last comment->'+autor)
logger.info('Last comment->'+lastcom)
if ( autor == '557058:f58131cb-b67d-43c7-b30d-6b58d40bd077') return success
if (lastcom.contains("+1")) {
    logger.info('Last comment in->' + lastcom)

    // Obtener la lista actual de participantes
    def currentParticipants = issue.fields.customfield_10767 ?: []

    // Asegurarse de que el autor no esté ya en la lista
    if (!currentParticipants.any { it.accountId == autor }) {
        currentParticipants.add([accountId: autor])
    }
    
    logger.info('currentParticipants->' + currentParticipants)

    // Actualizar el campo de participantes de la solicitud con la nueva lista
    def update = put("/rest/api/2/issue/${issueKey}")
        .queryString("overrideScreenSecurity", Boolean.TRUE)
        .header('Content-Type', 'application/json')
        .body([
            fields: [
                customfield_10767: currentParticipants
            ]
        ])
        .asString()

    if (update.status == 204) {
        return 'Success'
    } else {
        return "${update.status}: ${update.body}"
    }
}
}else{
    logger.info('No entra')
}
