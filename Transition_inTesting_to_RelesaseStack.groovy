// Este script lo ponemos en la trancicion de lso WF que van de In test al siguiente estado
def issueKey = 'WS-12' // issue.key
def issueKey2
def parts = issueKey.split("-")
/*def transitionId1 = 411 // "WS", "PA", "SL"
def transitionId2 = 191 // "WC"
def transitionId3 = 171 // "OVS" ,
def transitionId4 = 111 // "MCC"*/
def transitionHST = 361
def prefix
if (parts.size() > 0) {
     prefix = parts[0]
}
def projectKey = prefix // Reemplaza con la clave de proyecto
def prefijos = ["WS", "PA", "SL", "WC", "OVS", "MCC", "ITSAMPLE"]
for (def x=0;x<prefijos.size();x++){
    if (prefijos[x] == projectKey){
                
        def result = get('/rest/api/2/issue/' + issueKey)
                .header('Content-Type', 'application/json')
                .asObject(Map)
        if (result.status == 200){
            def issueLinks = result.body.fields.issuelinks
            if (issueLinks && issueLinks.size() > 0) {
                // Aquí accedes a los enlaces de la issue
                logger.info('1 inward->>'+ issueLinks[0].id)

                if (issueLinks[0].id == '10001' || issueLinks[0].id == '10001'){
                    logger.info('2 inward->>'+ issueLinks.id)
                    if (issueLinks[0].inwardIssue){
                        issueKey2= issueLinks[0].inwardIssue.key
                    }
                    if (issueLinks[0].outwardIssue){
                        issueKey2= issueLinks[0].outwardIssue.key
                    }
										
                    def resultTrans = post('/rest/api/2/issue/' + issueKey2 + '/transitions')
                        .header('accept', 'application/json')
                        .header('Content-Type', 'application/json')
                        .body('{\"transition\":{\"id\": \"' + transitionHST + '\"}}') // aquí ponemos la transición a destino
                        .asJson()
                    if (resultTrans.status >= 200 && resultTrans.status < 300) {
                        logger.info('entra')
                        logger.info(resultTrans.toString())
                    } else {
                        return "Error retrieving issue ${resultTrans}"
                    }
                }
            } else {
                logger.info('La issue no tiene enlaces.')
                // Puedes agregar una lógica adicional aquí si no hay enlaces.
            }
        } else {
            return "Failed to find issue: Status: ${result.status} ${result.body}"
        }
    } else{
        logger.info "No tiene prefijos válidos"
    }
}
