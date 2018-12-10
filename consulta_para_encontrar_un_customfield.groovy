/*     //con este scripts se sabe el total de SubTasks, pero no se puede poner junto porque se satura
        def result = post("/rest/api/2/search/")
            .header('Content-Type', 'application/json')
            .header('accept', 'application/json')
            .body('{"jql":"issuetype in subTaskIssueTypes()"}')
            .asJson()
        if (result.status == 200){
            int cont = result.body.array[0].total
            //logger.info('total issues '+cont)
            }else cont = 0
      */  
// Recuperamos una lista de todos los campos en esta instancia de JIRA
def fields = get("/rest/api/2/field")
        .asObject(List)
assert fields.status == 200

def customField = fields.delegate.body
def num = customField.size()
customField.each{
    def totalSubTask = '925'
    if (it.name && it.id){
        logger.info('success')
    //try{
    def aMano = it.name
    String query = "issuetype in subTaskIssueTypes() and '"+ aMano +"' is EMPTY"
        def result2 = post("/rest/api/2/search/")
            .header('Content-Type', 'application/json')
            .header('accept', 'application/json')
            //.queryString("startAt", 0)
            //.queryString("maxResults", 10) // If we search for too many issues we'll reach the 30s script timeout
            //.body('{"jql":"issuetype in subTaskIssueTypes() and \"'+it.name+'\" is EMPTY"}')
            .body('{"jql":"'+query+'"}')
            .asJson()
        if (result2.status == 200){
            int contCustom = result2.body.array[0].total
            }else contCustom = 0
       
        if (contCustom > 0){
            logger.info('XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX')
        }
      /*  }
        catch(Exception ee){
            log.info("error: " + ee)
    }*/
    logger.info('Total Issues ='+totalSubTask+', campo-->'+it.name+', id-->'+it.id+', total custom-->'+contCustom)
    }
}
