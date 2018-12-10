def issueKey = 'sdyt-40'
def bol = false
//buscamos el nombre del asignado
def resultAss = get('/rest/api/2/issue/' + issueKey)
.header('Content-Type', 'application/json')
.asObject(Map)
if (resultAss.status >= 200 && resultAss.status <= 300){
    def asignado_final = resultAss.body.fields.assignee.name//nombre del asignado actual
    def num = resultAss.body.fields.assignee.size()
} else {
    return "Failed to find issue: Status: ${resultAss.status} ${resultAss.body}"
}
def asignado_final = resultAss.body.fields.assignee.name//nombre del asignado actual
def numero = resultAss.body.fields.assignee.size()
logger.info('asignado final-- '+asignado_final)
//buscamos el usuario que esta hacienco la accion
def currentUser = get('/rest/api/2/myself').asObject(Map)
assert currentUser.status == 200
def currentUsername = currentUser.body.name //nombre del currentUser()
logger.info('current user-- ' + currentUsername)
//buscamos el estado escalado
def result = get('/rest/api/3/issue/' + issueKey +'/changelog')
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status >= 200 && result.status <= 300){
    def num = result.body.values.size() //contamos el numero modificaciones
    def asignado = result.body.values.items //vemos los datos del item asignado
    //preguntamos si el ultimo item esta escalado y si el asignado es el mismo que hace el movimiento
    //con un bucle for
    for(def x = 0; x < num; x++){
        for(def y = 0; y < 3; y++){
            if (asignado[x].toString[y] == 'Escalated' && currentUsername == asignado_final){
                def grupo= 'GrupoDePruebas' //si esta escalado y la persona es = al asignado 
                //enviamos el mail solo al grupo
                def resp = post("/rest/api/2/issue/${issueKey}/notify")
                .header("Content-Type", "application/json")
                .body([
                        subject: 'Tiene asignada la Issue - '+ issueKey +' como Grupo Nivel 2',
                        textBody: "Body",
                        htmlBody: "<p>Se le asigna al grupo N2 esta Issue</p>",
                        to: [
                            groups: [[
                                    name: grupo
                                ]]
                        ]
                    ])
                .asString()
                bol = true
                //to do esborrar assigneee
                logger.info('mail enviado a-->' + grupo)
                return 'sucess grupo'
            }else{
                bol = false

            }
        }
    }
    if (bol == false){
        def user= currentUsername //si esta escalado y la persona es = al asignado 
        logger.info('usuario-->'+user)
        //enviamos el mail solo al grupo
        def resp = post("/rest/api/2/issue/${issueKey}/notify")
        .header("Content-Type", "application/json")
        .body([
                subject: 'Tiene asignada como agente de N2 la Issue - '+ issueKey +' como Grupo Nivel 2',
                textBody: "Body",
                htmlBody: "<p>Se le asigna a "+user+"  grupo N2 esta Issue</p>",
                to: [
                    users: [[
                            name: user, //buscar username de el current user
                            active: true
                        ]]
                ]
            ])
        .asString()
        bol = true
        logger.info('mail enviado a-->'+user)
        //to do esborrar assigneee
        return 'sucess user'
    }
}