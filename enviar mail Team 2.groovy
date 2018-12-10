def issueKey = issue.key

def roleToAssignId = 10209 //TEAM 2

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
            
            it.actors.any(){
                logger.info("actor: " + it.name)
                def actorname = it.name
                if(actorname!=null && actorname!=""){
                    
                    def resultAssign = put("/rest/api/2/issue/${issueKey}/assignee")
                    .header('accept', 'application/json')
                    .header('Content-Type', 'application/json')
                    .body("{\"name\":\"" + actorname + "\"}}")
                    .asJson()
					
                    if (resultAssign.status >= 200 || resultAssign.status < 300){
                        logger.info("assign ok")
                        assigned = true
                        return "end"
                    }
                    else
                    logger.info("assign failed")
                }
            }
        }
    } else {
        return "Failed to find issue: Status: ${result.status} ${result.body}"
    }
    
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}