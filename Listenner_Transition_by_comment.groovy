// Clave de la issue que se va a procesar
def issueKey = issue.key  // Reemplaza con la clave de tu issue
def parts = issueKey.split("-")
def prefix
if (parts.size() > 0) {
     prefix = parts[0]
}
def projectKey = prefix // Reemplaza con la clave de proyecto
// Consulta la información de la issue utilizando la API de Jira
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

// Verifica si la consulta fue exitosa (código de estado 200)
if (result.status == 200){
    // Aquí puedes realizar operaciones con la información de la issue
} else {
    // Si la consulta no fue exitosa, devuelve un mensaje de error
    return "Error al encontrar la issue: Estado: ${result.status} ${result.body}"
}
def status= result.body.fields.status.name
// Obtiene la cantidad de comentarios en la issue
def size = result.body.fields.comment.comments.size()

// Obtiene el último comentario de la issue
def lastcomment=  result.body.fields.comment.comments[size-1]

// Obtiene el ID del autor del último comentario
def autor = lastcomment.author.accountId

// ID del grupo al que se desea verificar la pertenencia del autor
def groupId = 'e2d7d5ce-6a77-467d-8b99-ef8469a2f482'

// ID de la transición que se ejecutará si el autor pertenece al grupo
def transitionId = '171' // ID de la transición "Under Review"

// Consulta los miembros del grupo utilizando la API de Jira
def result2 = get("/rest/api/3/group/member?groupId=" + groupId)
    .header('Content-Type', 'application/json')
    .asJson()

// Verifica si la consulta de miembros del grupo fue exitosa
if (result2.status == 200) {
    // Obtiene la lista de usuarios en el grupo y sus IDs
    def usersList = result2.body.object.values
    def usersInGroup = usersList.collect { it.accountId }
    
    // Registra los IDs de los usuarios en el grupo en el registro
    logger.info('Usuarios en el grupo: ' + usersInGroup)
    
    // Obtiene la cantidad de usuarios en el grupo
    def x = usersInGroup.size()
    
    // Itera a través de los usuarios en el grupo
    for (def y = 0; y < x.size(); y++) {
        if ((autor == usersInGroup[y] && status.toLowerCase() == "wait on user") || autor == result.body.fields.reporter.accountId && status.toLowerCase() == "wait on user") { // añadir el reporter 
            // Si el autor pertenece al grupo, se ejecuta la transición "Under Review"
            def resultTrans = post('/rest/api/2/issue/' + issueKey + '/transitions')
                .header('accept', 'application/json')
                .header('Content-Type', 'application/json')
                .body(['transition' : [ 'id': transitionId ]])
                .asJson()
            
            // Verifica si la transición fue exitosa
            if (resultTrans.status >= 200 && resultTrans.status < 300) {
                logger.info('Transición exitosa: ' + resultTrans.toString())
            } else {
                // Si la transición no fue exitosa, devuelve un mensaje de error
                return "Error en la transición de issue ${issueKey}: ${resultTrans.body}"
            }
        }
    }
} else {
    // Si la consulta de miembros del grupo no fue exitosa, devuelve un mensaje de error
    return "Error al obtener los miembros del grupo: Estado: ${result2.status} ${result2.body}"
}
