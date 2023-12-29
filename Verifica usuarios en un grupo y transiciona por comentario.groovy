import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

// Clave de la issue que se va a procesar
def issueKey = "HST-10441" // Reemplaza con la clave de tu issue
def parts = issueKey.split("-")
def prefix
def issue
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
    issue = result.body
    // Aquí puedes realizar operaciones con la información de la issue
} else {
    // Si la consulta no fue exitosa, devuelve un mensaje de error
    return "Error al encontrar la issue: Estado: ${result.status} ${result.body}"
}
def status= issue.fields.status.name
// Obtiene la cantidad de comentarios en la issue
def size = issue.fields.comment.comments.size()
def userId
// Obtiene el último comentario de la issue
def lastcomment=  issue.fields.comment.comments[size-1]
boolean encontrado = false
// Obtiene el ID del autor del último comentario
def autor = lastcomment.author.accountId
logger.info('autor last comment->'+autor)
// ID del grupo al que se desea verificar la pertenencia del autor
def groupId = 'f72d3948-1297-4126-be44-0cf5d895d13f'

// ID de la transición que se ejecutará si el autor pertenece al grupo
def transitionId = '171' // ID de la transición "Under Review"

// Variable para almacenar todos los usuarios en el grupo
def usersInGroup = []

// Inicializa la página en 0
def startAt = 0
def maxResults = 50  // Número máximo de resultados por página
for(def w=0;w<4;w++){
    
    // Continúa obteniendo usuarios hasta que no haya más páginas
    // Consulta los miembros del grupo utilizando la API de Jira con paginación
    def result2 = get("/rest/api/3/group/member?groupId=" + groupId + "&startAt=" + startAt + "&maxResults=" + maxResults)
        .header('Content-Type', 'application/json')
        .asJson()
        def xx = 0
    // Verifica si la consulta de miembros del grupo fue exitosa
    if (result2.status == 200) {
        // Obtiene la lista de usuarios en la página actual
        def usersList = result2.body.toString()
        def usersLists = new JsonSlurper().parseText(usersList)
        def usuariosDeLaLista = usersLists.values.accountId
        
      // Si la página actual tiene menos de 'maxResults' usuarios, hemos alcanzado el final
                for(def z=startAt;z< usuariosDeLaLista.size(); z++){
                    if(usuariosDeLaLista == autor){
                    logger.info('usersList->'+usuariosDeLaLista[z])
                    userId = usuariosDeLaLista
                        if(autor == userId){
                           encontrado = true
                           logger.info('Usuario encontrado->'+userId)
                        }
                    }    
                }

        // Incrementa el valor de 'startAt' para obtener la siguiente página
        //startAt += maxResults
        logger.info('1.- maxResults->'+maxResults+', and startAt->'+startAt)
                    maxResults= maxResults+50
                    startAt = startAt+50
                    logger.info('2.- maxResults->'+maxResults+', and startAt->'+startAt)
    } else {
        // Si la consulta no fue exitosa, devuelve un mensaje de error
        return "Error al obtener los miembros del grupo: Estado: ${result2.status} ${result2.body}"
    }


    
}

// Registra los IDs de todos los usuarios en el grupo en el registro
logger.info('Usuarios en el grupo: ' + userId)
logger.info('encontrado->'+encontrado+', status->'+status)
// Itera a través de los usuarios en el grupo
    if ((encontrado == true && status.toLowerCase() == "wait on user") || autor == issue.fields.reporter.accountId && status.toLowerCase() == "wait on user") { // añadir el reporter 
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

