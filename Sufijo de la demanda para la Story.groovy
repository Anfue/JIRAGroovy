if(issue.fields.issuetype.name == 'Historia' || issue.fields.issuetype.id = '10300' ){
def Existe_demanda = false
def DemandaKey = ''
def tipoIssue= issue.fields.issuetype //busco el tipo de issue que es
def summaryStory = issue.fields.summary //guardo el summary que luego usaremos
def links = issue.fields.issuelinks //busco en datos del padre
def separacion = "-->"

String[] parts = summaryStory.split(separacion); //separo en dos el nombre para trabajarlo debajo
def comprobar = parts.size() //muro si tiene 2 partes y no una
logger.info('Tiene '+comprobar+' partes')
if (comprobar >= 2){
    logger.info('parte 1-->'+parts[0])
    logger.info('parte 2-->'+parts[1])
    logger.info('ya esta con sufijo, no se repite')
    return 'ya esta con sufijo, no se repite'
}else { //si solo tiene 1 parte es que no se le ha puesto el sufijo de la Demanda
links.each{
if (it.outwardIssue){ //buscamos si el padre tiene algo, o si existe
    if (tipoIssue.name.toLowerCase() == 'historia' || tipoIssue.id == 10300){ //pregunto si la actualizada es historia
    DemandaKey = it.outwardIssue.key //el nombre de el padre
    def esDemanda = it.outwardIssue.fields.issuetype //miro el tipo del padre
    def clonaA = it.type.outward //si es su padre aqui habrá algo
    if((esDemanda.name.toLowerCase() == 'demanda' || esDemanda.id == 10304)&& clonaA == 'clona a'){//pregunto si es demanda y si es su padre 
        Existe_demanda = true //cambio el estado para salir del bucle al acabar
            logger.info ('existe demanada-->' +Existe_demanda)
            def issueKey = issue.key 
            def newSummary = issue.fields.summary + '-->'+'[' + DemandaKey + ']' //pongo el titulo antiguo + el sufijo que será la key del padre
             logger.info ('Nuevo sumario-->' +newSummary)
            def result = put("/rest/api/2/issue/" + issueKey) //añado con un put el nuevo summary
                .header('Content-Type', 'application/json')
                .body([
                    fields: [
                            summary: newSummary
                    ]
                ])
                .asString()
            if (result.status == 204) { 
                return 'Success'
            } else {
                return "${result.status}: ${result.body}"
            } 
        } 
        logger.info('No tiene un padre demanda 1 en algún ciclo')
        Existe_demanda = true //cambio el estado para salir del bucle al acabar
    } 
     Existe_demanda = true //cambio el estado para salir del bucle al acabar
        }else{
        logger.info('No tiene un padre demanda 2 en algún ciclo')
        Existe_demanda = true //cambio el estado para salir del bucle al acabar
        return 'No tiene un padre demanda 2 en algún ciclo'
        }
    }
}
}else{
    logger.info('No es Historia')
}


