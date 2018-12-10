def issueKey = 'SDYT-40'
def statusFrom='WORK IN PROGRESS'.toLowerCase()
def statusFrom2='Pending'.toLowerCase()
def statusTo='Escalated'.toLowerCase()

//recogemos los datos de la Issue del historico de cambios
def result = get("/rest/api/2/issue/${issueKey}?expand=changelog")
.header('Content-Type', 'application/json')
.asObject(Map)
//definimos variables
def author1 = result.body.fields.assignee?.name //nombre de la persona asignada
def h =  result.body.changelog.histories.size() //numero de movimientos historico

if (result.status >= 200 && result.status < 300){
    logger.info("historias hechas --> " + h)
    //definimos un boleano para trabajar con él
    def hasTransitionToEscalated = false
    //hacemos un bucle con result.body.changelog.histories y las metemos en hist
    for(hist in result.body.changelog.histories){
        if(hasTransitionToEscalated) break
        //volvemos a hacer un bucle para preguntar sobre los items de result.body.changelog.histories
        for(item in hist.items){
            //preguntamos por el ststus
            if(item.fieldId=="status"){
                logger.info('from: '+item.fromString+', to: '+item.toString)
                //si viene de pending o work in progress o esta en escalado seguimos la pregunta
                 if((item.fromString.toLowerCase()==statusFrom || item.fromString.toLowerCase()==statusFrom2) && item.toString.toLowerCase()==statusTo){
                    logger.info('Es escalado')
                    //trabajamos el boleano
                    hasTransitionToEscalated=true
                    break
                }else{
                    logger.info('No es escalado de N1 a N2')
                }
            }
        }
        
        if(hasTransitionToEscalated == true){
            logger.info("("+hist.id+")"+" ["+hist.author.name+"], ")
        //definimos result.body.changelog.histories.author.name
        def nom = hist.author.name
        //ponemos el nombre que hemos recogido antes
            def resultAssign = put("/rest/api/2/issue/${issueKey}/assignee")
                    .header('accept', 'application/json')
                    .header('Content-Type', 'application/json')
                    .body("{\"name\":\""+ nom +"\"}}")
                    .asJson()

            if (resultAssign.status >= 200 && resultAssign.status < 300){
                logger.info("result transicionar: " + resultAssign.status + '; ' + resultAssign.statusText)
                //break
            } else {
                logger.info("Failed to ASSIGN issue: Status: ${resultAssign.status} ${resultAssign.body}")
            }
        }
    }
    
    

//asignado actual
    def issueKey = 'SDYT-40'

def result = get("/rest/api/2/issue/${issueKey}?expand=changelog")
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    def asignado = result.body.fields.assignee
    return asignado
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
//estado en el que se encuentra
def issueKey = 'SDYT-40'

def result = get("/rest/api/2/issue/${issueKey}?expand=changelog")
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    def asignado = result.body.fields.status.name
    return asignado
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
 // buscamos el ultimo responsable
 def issueKey = 'SDYT-40'

def result = get("/rest/api/2/issue/${issueKey}?expand=changelog")
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    def num = result.body.changelog.histories.size()
    def asignado = result.body.changelog.histories.author[num-1]
    return asignado
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

//posible ultimo responsable
 def issueKey = 'SDYT-40'

def result = get("/rest/api/3/issue/${issueKey}/changelog")
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    def num = result.body.values.author.size()
    def asignado = result.body.values.items.toString
    return asignado
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}


    
    
}