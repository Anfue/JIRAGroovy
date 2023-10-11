def issueKey = 'WS-28141'

def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    //return result.body.fields
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def transitionId 
def linkissue
def issueLinks = result.body.fields.issuelinks
if (issueLinks && issueLinks.size() > 0) {
    for (def x=0;x<issueLinks.size();x++){
        if (issueLinks[x].type.id == "10002"){
            if (issueLinks[0].inwardIssue){
                issueKey2= issueLinks[0].inwardIssue.key
            }
            if (issueLinks[0].outwardIssue){
                issueKey2= issueLinks[0].outwardIssue.key
            }
        }
    }
    def parts = issueKey2.split("-")
    def prefix
        if (parts.size() > 0) {
             prefix = parts[0]
        }
    def projectKey = prefix
    def status= result.body.fields.status.name
    def fielProdVal = result.body.fields.customfield_10343
    
    logger.info('Project code='+projectKey)
    logger.info('issueLinks.id->'+issueLinks.id)
    logger.info('status.ToLowerCase()->'+status.toLowerCase())
    logger.info("fielProdVal->"+fielProdVal)
    
    //return result.body.fields

    if(issueLinks.id == "10002" && status..toLowerCase() != "done" && fielProdVal == "OK"){
        def resultTrans = post('/rest/api/2/issue/' + issueKey2 + '/transitions')
                    .header('accept', 'application/json')
                    .header('Content-Type', 'application/json')
                    .body(['transition' : [ 'id': transitionId ]])
                    .asJson()
    }else{
        logger.info('No cumple las condiciones')
    }
}