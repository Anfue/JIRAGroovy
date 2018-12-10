def issueKey = 'SDYT-24'

def roleToAssignId = 10101

def assigned = false

def result = get("/rest/api/2/issue/${issueKey}")
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    def projectKey = result.body.fields.project.key
    logger.info("project: " + projectKey)   
    
    def result2 = get("/rest/api/2/project/${projectKey}/role/" + roleToAssignId)
        .header('Content-Type', 'application/json')
        .asJson()
    
    if (result2.status == 200){
        //return result.body.array
        result2.body.array.any(){
            if(assigned) return "end"
            
            it.actors.each(){
                logger.info("actor: " + it.name)
                def actorname = it.name
                
            }
        }
    } else {
        return "Failed to find issue: Status: ${result.status} ${result.body}"
    }
    
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

