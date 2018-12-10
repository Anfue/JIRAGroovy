def transitionId = '41' //transicion A Descomposición que va a (DESCOMPOSICION) Story 
def bool = true
def sprintId = sprint.id
def board = sprint.originBoardId
def issueKey = ''
//buscamos la pizarra donde esta el sprint para buscar las issues y moverlas
/*
def result = get("/rest/agile/1.0/sprint/"+sprintId)
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200) {
logger.info('entra')
logger.info(result.toString())
} else {
return "Error retrieving issue ${result}"
}
 */
//datos de la pizarra donde están issues
def resultBoard = get('/rest/agile/1.0/board/'+board +'/sprint/'+sprintId+'/issue')
.header('Content-Type', 'application/json')
.asObject(Map)
if (resultBoard.status == 200) {
    logger.info('entra')
    logger.info(resultBoard.toString())
} else {
    return "Error retrieving issue ${resultBoard}"
}
//definimos el tipo de issue que encontramos en la pizarra
def typeIssueBoard = resultBoard.body.issues //mira,os que sea una story y no otra cosa
//creamos un bucle para buscar las issues de la pizarra
typeIssueBoard.each {
    if (it.fields.issuetype.id == '10300' || it.fields.issuetype.name == 'Historia'){
        //una vez buscada la issue correspondiente en este caso las stories se le asigna al get de abajo para hacer la transicion correspondiente
        storyKey = it.key
        logger.info('Entra en el if y la transitionId-->'+transitionId)
        def resultTrans = post('/rest/api/2/issue/' + storyKey + '/transitions')
        .header('accept', 'application/json')
        .header('Content-Type', 'application/json')
        .body('{\"transition\":{\"id\": \"' + transitionId + '\"}}')
        .asJson()
        if (resultTrans.status >= 200 && resultTrans.status < 300) {
            logger.info('entra')
            logger.info(resultTrans.toString())
        } else {
            return "Error retrieving issue ${resultTrans}"
        }
    }else{
        logger.info('la que no entra es ' +storyKey+' estado--> '+ it.fields.issuetype)
    }

}
