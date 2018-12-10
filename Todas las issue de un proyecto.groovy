def projectKey = 'RDPINTEG'
/*
{ "startAt": 0,
"maxResults": 50, no poner más de 100 porque peta por todos lados
"total": 250,
"issues": [] }
 */
def result = get('/rest/api/2/search?jql=project='+projectKey+'&maxResults=100&startAt=0')
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200){
    logger.info('Total Issues = ' + result.body.total)
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def links = result.body.issues.fields.issuelinks
def tipo = result.body.issues.fields.issuelinks.outwardIssue.fields.issuetype
if (links){
    links.each{
        def TypeTask = it.outwardIssue.fields.issuetype //subtarea o historia
        def idTask = TypeTask.id //10302 = subtarea  1300 = historia
        def status = it.outwardIssue.fields.status.name  //estado de la Issue
        def NameTask = it.outwardIssue.key // nombre de la Issue "RDPELE-222"
        logger.info('Tipo-->'+TypeTask.name+' - name-->'+NameTask+' - estado-->'+status)
        if (TypeTask.name){
             logger.info('Entra 1 y TypeTask.name-->'+TypeTask.name)
           /* if (TypeTask.name == 'Historia'){*/
                logger.info('XXXXxxxxXXXXxxxxxXXXXXxxxxxxXXXXXXxxxxxXXXXxxxxXXXX')
        //Si es historia y esta en estimacion buscamos        
        if (status == 'ESTIMACION' ||  status == 'PTE APROBACIÓN'){
            logger.info('entra en historia')
            //buscar las story que estan en estimacion y si tienen SubTask mover la Historia
            def result2 = get('/rest/api/2/issue/' + NameTask)
            .header('Content-Type', 'application/json')
            .asObject(Map)
            if (result2.status == 200){
                logger.info('succes ok- busqueda de Stories')
            } else {
                return "Failed to find issue: Status: ${result2.status} ${result2.body}"
            }
            def planificacion = '471'
            //si la historia tiene sub tareas ...
            if (result2.body.fields.subtasks){
                def estadoSubTask = result2.body.issues.fields.issuelinks.outwardIssue.fields.status.name
                logger.info('Tipo-->'+TypeTask.name+' - name-->'+NameTask+' - estado-->'+estadoSubTask)
                def resultTransP = post('/rest/api/2/issue/' + NameTask + '/transitions')
                .header('accept', 'application/json')
                .header('Content-Type', 'application/json')
                .body('{\"transition\":{\"id\": \"' + planificacion + '\"}}')
                .asJson()
                if (resultTransP.status >= 200 && resultTransP.status < 300) {
                    logger.info('transitionId=' + planificacion)
                    logger.info( 'success y transitionId = ' + planificacion)
                } else {
                    return "Failed to find issue: Status: ${resultTransP.status} ${resultTransP.body}"
                }
                logger.info('Tipo-->'+TypeTask.name+' - name-->'+NameTask+' - estado-->'+estadoSubTask)
            }else{
                logger.info('no hay sub tareas asi que no se mueve nada')
            }
            //si las stories estan planificacion o pasado dejarlas, sino pasarlas a planificacion
            //buscar 
        }else if ((TypeTask.name == 'Historia' || idTask == 10300) &&
            status.toLowerCase() == 'en desarrollo' || status == 'PTE APROBACIÓN' ||  
            status == 'PLANIFICACIÓN' || status == 'DESCOMPOSICIÓN EN TAREAS' ||
            status == 'ENTREGA NO SATISFACTORIA'){
            
            def result3 = get('/rest/api/2/issue/' + NameTask)
            .header('Content-Type', 'application/json')
            .asObject(Map)
            if (result3.status == 200){
                logger.info('succes ok- busqueda de Stories')
            } else {
                return "Failed to find issue: Status: ${result3.status} ${result3.body}"
            }
            if (result3.body.fields.subtasks){
                def num = result3.body.fields.subtasks.size()
                for(def x=0; x < num; x++){
                    def estadoSubTask = result3.body.issues.fields.issuelinks[x].outwardIssue.fields.status.name
                    if (estadoSubTask.toLowerCase() == 'cerrada'){
                        taskCerrada++
                    }else if(estadoSubTask.toLowerCase() == 'cancelada'){
                        taskCancel++
                    }
                }
                def suma = taskCerrada+taskCancel
                if (num == suma){
                    def DesarrolloFinalizado = '391'
                    //transicionar story a desarrollo finalizado
                    def resultTrans = post('/rest/api/2/issue/' + NameTask + '/transitions')
                    .header('accept', 'application/json')
                    .header('Content-Type', 'application/json')
                    .body('{\"transition\":{\"id\": \"' + DesarrolloFinalizado + '\"}}')
                    .asJson()
                    if (resultTrans.status >= 200 && resultTrans.status < 300) {
                        logger.info('transitionId=' + DesarrolloFinalizado)
                        logger.info( 'success y transitionId = ' + DesarrolloFinalizado)
                    } else {
                        return "Failed to find issue: Status: ${resultTrans.status} ${resultTrans.body}"
                    }
                }
                    
                
            }
        }
            /*}else{
    logger.info('No es Historia')
}*/
        }else{
    logger.info('campo vacio')
}
    }
}else{
    logger.info('no hay tareas')
}


//hdg-319