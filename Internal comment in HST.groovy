def issueKey = 'WS-28142' // issue.key
def issueKey2
boolean boleano = false
def parts = issueKey.split("-")
def prefix
def xx
def LasComment
if (parts.size() > 0) {
     prefix = parts[0]
}
if (prefix == "WS" || prefix == "PA" ||prefix == "SL" ||prefix == "WC" ||prefix == "OVS" 
||prefix == "MCC" || prefix == "HST"){
	def result = get('/rest/api/2/issue/' + issueKey)
			.header('Content-Type', 'application/json')
			.asObject(Map)
	if (result.status == 200){
		//return result.body.fields

	} else {
		return "Failed to find issue: Status: ${result.status} ${result.body}"
	}
	
	def issueLinks = result.body.fields.issuelinks
	def numLinks = issueLinks.size()
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
						logger.info('Llega->'+numLinks)

		for(def w=0;w<numLinks;w++){
				logger.info('issueLinks.idsss->'+issueLinks[w].type.id)

		    if(issueLinks[w].type.id == 10002 || issueLinks[w].type.id == '10002'){
		      boleano = true  
		    } 
		}
		 xx = result.body.fields.comment.comments.size()
    LasComment = result.body.fields.comment.comments[xx-1]
		//return result.body.fields
	}
	def summary = result.body.fields.summary
	def startSummary = summary[0]+summary[1]+summary[2]
	if (startSummary == 'HST' && (prefix == "WS" || prefix == "PA" ||prefix == "SL" ||
	prefix == "WC" ||prefix == "OVS" ||prefix == "MCC" )){
def commentResp = post("/rest/api/2/issue/" + issueKey2 +"/comment")
                    .header('Content-Type', 'application/json')
                    .body([
                            body: 'Probando' //LasComment//Copy comment by duplicate
                        ])
                    .asObject(Map)
	}else if(prefix == 'HST'){
	
	}
logger.info('issueLinks.id->'+issueLinks)

		if(boleano == true){
		   //transicionar comentario put o post 
			logger.info('Si cumple las condiciones')

		   
		}else{
			logger.info('No cumple las condiciones')
		}
	
}