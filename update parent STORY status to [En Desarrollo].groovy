//def transitionId = '61' //transicion (iniciar) STORY a [En curso]
def transitionId = '301' //transicion All to STORY a [En DESARROLLO]
//def storyInicialStatusName="DESCOMPOSICIÓN EN TAREAS"
//def storyInicialStatusId="10313"

logger.info("parent: " + issue.fields.parent.key)
logger.info("parent status: " + issue.fields.parent.fields.status.name)

if(issue.fields.parent.fields.status.name.toLowerCase()=="en desarrollo")
{
    logger.info("Parent ya está EN DESARROLLO. No es necesario transicionar.")
    return
}
/*
if(issue.fields.parent.fields.status.name==storyInicialStatusName || issue.fields.parent.fields.status.id==storyInicialStatusId){//solo se puede aplicar la transición 61 si está en este estado
	logger.info("se transicionaria")
*/
	def resultTrans = post('/rest/api/2/issue/' + issue.fields.parent.key + '/transitions')
		.header('accept', 'application/json')
		.header('Content-Type', 'application/json')
		.body('{\"transition\":{\"id\": \"' + transitionId + '\"}}')
		.asJson()
	if (resultTrans.status >= 200 && resultTrans.status < 300){
		logger.info("result: " + resultTrans.status + '; ' + resultTrans.statusText)
	} else {
		logger.info("Failed to find issue: Status: ${resultTrans.status} ${resultTrans.body}")
	}
/*}
else
	logger.info("no se transiciona")
*/