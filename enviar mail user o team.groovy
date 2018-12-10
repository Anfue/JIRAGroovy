def issueKey = issue.key
logger.info ("----------------------------------------------------------------------------")
logger.info ("/rest/api/3/issue")
logger.info ("----------------------------------------------------------------------------")

//buscamos el nombre del asignado
def resultAss = get('/rest/api/2/issue/' + issueKey)
.header('Content-Type', 'application/json')
.asObject(Map)
if (resultAss.status == 200){
    def asignado_final = resultAss.body.fields.assignee.name//nombre del asignado actual
} else {
    return "Failed to find issue: Status: ${resultAss.status} ${resultAss.body}"
}

def result = get('/rest/api/3/issue/' + issueKey +'/changelog')
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200){
    def num = result.body.values.author.size() //contamos el numero modificaciones
    def asignado = result.body.values.items //vemos los datos del item asignado
    def persona = result.body.values.author.name //sacamoes el nombre del ultimo asignado
    //preguntamos si el ultimo item esta escalado y si el asignado es el mismo que hace el movimiento
    if (asignado[num-1].toString[1] == 'Escalated' && persona[num-1] == asignado_final){ 
        def grupo= 'GrupoDePruebas' //si esta escalado y la persona es = al asignado 
        //enviamos el mail solo al grupo
        def resp = post("/rest/api/2/issue/${issueKey}/notify")
        .header("Content-Type", "application/json")
        .body([
                subject: 'Tiene asignada la Issue - ${issueKey}',
                textBody: "Body",
                htmlBody: "<p>Body</p>",
                to: [
                    users: [[
                            name: grupo,
                            active: true
                        ]]
                ]
            ])
        .asString()
        //hacemos otro if pq si hay un comentario no cogeria los datos del comentario y queremos el usuario
        //por eso le pedimos un numero más [num-2]
    }else if(asignado[num-2].toString[1] == 'Escalated' && persona[num-2] == asignado_final){ ){ //preguntamos si el ultimo item esta escalado
        def grupo= 'GrupoDePruebas' //si esta escalado y la persona es = al asignado  
        //enviamos el mail solo al grupo
        def resp = post("/rest/api/2/issue/${issueKey}/notify")
        .header("Content-Type", "application/json")
        .body([
                subject: 'Tiene asignada la Issue - ${issueKey}',
                textBody: "Body",
                htmlBody: "<p>Body</p>",
                to: [
                    users: [[
                            name: grupo,
                            active: true
                        ]]
                ]
            ])
        .asString()
    }else if(asignado[num-1].toString[1] == 'Escalated' && persona[num-1] != asignado_final){ ){ //preguntamos si el ultimo item esta escalado
        def person= persona[num-1] //si esta escalado y la persona es = al asignado  
        //enviamos el mail solo al grupo
        def resp = post("/rest/api/2/issue/${issueKey}/notify")
        .header("Content-Type", "application/json")
        .body([
                subject: 'Tiene asignada la Issue - ${issueKey}',
                textBody: "Body",
                htmlBody: "<p>Body</p>",
                to: [
                    users: [[
                            name: person,
                            active: true
                        ]]
                ]
            ])
        .asString()
    }else if(asignado[num-2].toString[1] == 'Escalated' && persona[num-2] != asignado_final){ ){ //preguntamos si el ultimo item esta escalado
        def person= persona[num-1] //si esta escalado y la persona es = al asignado  
        //enviamos el mail solo al grupo
        def resp = post("/rest/api/2/issue/${issueKey}/notify")
        .header("Content-Type", "application/json")
        .body([
                subject: 'Tiene asignada la Issue - ${issueKey}',
                textBody: "Body",
                htmlBody: "<p>Body</p>",
                to: [
                    users: [[
                            name: person,
                            active: true
                        ]]
                ]
            ])
        .asString()
            
    }else{
        logger.info('salta porque no se cumple ninguna condicion')
    }
            
        
    //logger.info (asignado.toString())
    /* for(def x=0 ; x < num; x++ ){
    if(asignado[x] != " "){
    def bob = asignado[x]
    //return asignado
    }
    }*/

    // return bob
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}