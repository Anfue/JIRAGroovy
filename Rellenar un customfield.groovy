def issueKey = issue.key
//def issueKey = issue.key
def y = 0
def result = get('/rest/api/2/issue/' + issueKey)
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200){
    def links = result.body.fields.issuelinks
    def total = links.size()
    def tipo = result.body.fields.issuetype
    links.each{ 
        if (it.inwardIssue){
        def incidencia = it.inwardIssue.fields.issuetype.name
        }else if (it.outwardIssue){
        def incidencia = it.outwardIssue.fields.issuetype.name
        
        logger.info(incidencia)
        if ((tipo.name == 'Historia' || tipo.id == '10300' || tipo.name == 'Demanda' || tipo.id == '10304')
            && ( incidencia == 'Incident' ||  incidencia == 'Incident') ){
                y++
                if(links.size() == y){
                    logger.info('Total asociadas-->'+total+' total incidencias-->'+y) 
                }
            
        }else{
                logger.info('No es Incident')
            }
            }else{
                logger.info('No es Incident')
            }
    }

        def result2 = put("/rest/api/2/issue/${issueKey}") 
        .queryString("overrideScreenSecurity", Boolean.TRUE) 
        .header('Content-Type', 'application/json')
        .body([
                fields: [
                    customfield_10723: y.toString()+' incidencias asociadas' //masiva
                ]
            ])
        .asString()
        logger.info('tiene '+y+' incidencias asiciadas')
        if (result2.status == 204) { 
            logger.info('Success')
        } else {
            return "${result2.status}: ${result2.body}"
        }


}else{
    logger.info('No tiene incidencias enlazadas')
    return 'No tiene incidencias enlazadas'
}
