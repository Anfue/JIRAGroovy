def issueKey = issue.key //'DEAL-1210'
def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    issue = result.body
    //return result.body.fields
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def reporter = issue.fields.reporter.accountId
def status = issue.fields.status.id // Done = 10002
def transitionId = "181"
def size = issue.fields.comment.comments.size()

// Obtiene el último comentario de la issue
def lastcomment=  issue.fields.comment.comments[size-1]

// Obtiene el ID del autor del último comentario
def autor = lastcomment.author.accountId
logger.info('lastcomment->'+lastcomment)
logger.info('autor->'+autor)
logger.info('reporter->'+reporter)
logger.info('status->'+status)
if (status == "10002" && reporter == autor){
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
}else{
    logger.info ("El autor del comentario ${autor} no es el reporter ${reporter}")
}
