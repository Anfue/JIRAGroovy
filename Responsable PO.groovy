def issueKey = issue.key
def equipoDesarrollo = issue.fields.customfield_10663
//Rols:  PO-10206, QA-10205, QAprocess-10210, AcepStory - 10215
def AceptStory = 10215
def roleQAGenId = 10205
def roleQAProcessId = 10210
def assigned = false

def projectKey = issue.fields.project.key
logger.info("project: " + projectKey)

def rolQAToAssignId = AceptStory 
/*if(equipoDesarrollo){
    if(equipoDesarrollo.id == 10611 || equipoDesarrollo.value == "Process Suite"){
        rolQAToAssignId = roleQAProcessId
    }
    logger.info("Dev Team: " + equipoDesarrollo)
}
else{
    logger.info("No tiene equipo de desarrollo asignado")
}*/

def result2 = get("/rest/api/2/project/${projectKey}/role/" + rolQAToAssignId)
	.header('Content-Type', 'application/json')
	.asJson()

if (result2.status == 200){
	//return result.body.array
	
	result2.body.array.any(){
		if(assigned) return "end"
		
		it.actors.any(){
			logger.info("actor: " + it.name)
			def actorname = it.name
			if(actorname!=null && actorname!="")
			{
				issueInput.update.assignee = [[set: [name: actorname]]]
				//issueInput.fields.assignee = [name: actorname] as Map
				logger.info("assign ok")
				assigned = true
				return "end"
			}
		}
	}
} else {
	return "Failed to find roles: Status: ${result2.status} ${result2.body}"
}