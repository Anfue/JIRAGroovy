//primero averiguamos el tipo de issue que es
def tipoIssue = issue.fields.issuetype
def status = issue.fields.status.name
logger.info('Tipo de Issue-->'+tipoIssue.name.toLowerCase()+' y issue-->'+issue.key)
//si es una SubTask 
if(tipoIssue.name.toLowerCase() == 'subtarea' || tipoIssue.id == '10302'){
    logger.info('entra en el if porque es SubTask')
    if (status.toLowerCase() == 'cancelada'){
        def pater = issue.fields.parent?.key
        logger.info( "padre--> " + pater)
        def transitionId1 = '391' // Desarrollo finalizado
        def transitionId2 = '301' //en desarrollo (Story)
        def transitionId3 = '321' //Descomposicion en tareas (Story)
        def hijoTipo = issue.fields.issuetype // veo el tipo de issue que es
        def hijoTipoST = issue.fields.issuetype.subtask // veo el tipo de issue que es
        def paterStatus = issue.fields.parent.fields.status.name.toLowerCase() //veo el estado del padre

        logger.info("Tipo de hijo--> " + hijoTipo.name)

        def result = get('/rest/api/2/issue/' + pater) //consulto los datos del padre
        .header('Content-Type', 'application/json')
        .asObject(Map)
        def Nhijos = result.body.fields.subtasks.size() //miro cuantos hijos tiene para recorrer un foreach
        logger.info("hijos--> " + Nhijos)

        def TD = 0  //to do (SubTask)
        def ED = 0  //en desarrollo (SubTask)
        def DC = 0 // desarrollo completado (SubTask)
        def CC = 0 //cancelada (subTask)

        if (result.status >= 200 && result.status < 300 ){
            for (def x = 0 ; x < Nhijos; x++){ //for para mirar el estado de cada hijo
                def res = result.body.fields.subtasks.fields.status.name[x].toLowerCase()
                if (res.toLowerCase() == "por hacer" || res.toLowerCase() == "to do"){// si la subTask esta por hacer sumamos el contador
                    TD++                                                                       //de TO DO (Descomposicion)
                } else if (res.toLowerCase() == "en desarrollo"){  
                    ED++ // si la subTask estaen otros estados sumamos el contador de TOTALES (EN DESARROLLO)
                } else if (res.toLowerCase() == "desarrollo completado" ){
                    DC++ 
                    //si no es ninguna de las demás no haremos sino que sumar en otros
                } else if (issue.fields.status.name.toLowerCase() == 'cancelada'){
                    CC++
                }
                logger.info("Desarrollo Completado--> " + DC)
                logger.info("TO DO--> " + TD)
                logger.info("En Desarollo--> " + ED)
            }
            result = get('/rest/api/2/issue/' + pater) //busco el GET del padre para obtener sus datos
            .header('Content-Type', 'application/json')
            .asObject(Map)
            if (result.status == 200){
                if ( (Nhijos - CC) == TD ) { // si todas las SubTasks estan en TO DO pasaremos al padre a DESCOMPOSICION TAREAS (321)
                    logger.info("Pater--> " + paterStatus.toLowerCase())
                    if(paterStatus.toLowerCase()=="descomposicion en tareas"){
                        logger.info("Parent ya está en DESCOMPOSICION. No es necesario transicionar.")
                        return
                    }else{
                        logger.info("entra en el else")
                        def resultTranss = post('/rest/api/2/issue/' + pater + '/transitions')
                        .header('accept', 'application/json')
                        .header('Content-Type', 'application/json')
                        .body('{\"transition\":{\"id\": \"' + transitionId3 + '\"}}')
                        .asJson()
                        logger.info("Pide valores de transicion")
                        if (resultTranss.status >= 200 && resultTranss.status < 300){
                            logger.info("veremos el estado para el padre transicion ´(321)--> " + resultTranss.status)
                            logger.info("result: " + resultTranss.status + '; ' + resultTranss.statusText)
                        } else {
                            logger.info("Failed to find issue: Status: ${resultTranss.status} ${resultTranss.body}")
                        }
	
                    }
                } else if ((ED- CC) >= 1){ // si alguna de las SubTasks estan en desarrollo pondremos la Story EN DESARROLLO (301)
                    logger.info("Pater--> " + paterStatus.toLowerCase())
                    if(paterStatus.toLowerCase()=="en desarrollo"){
                        logger.info("Parent ya está EN DESARROLLO. No es necesario transicionar.")
                        return
                    }else{
                        logger.info("entra en el else")
                        def resultTransss = post('/rest/api/2/issue/' + pater + '/transitions')
                        .header('accept', 'application/json')
                        .header('Content-Type', 'application/json')
                        .body('{\"transition\":{\"id\": \"' + transitionId2 + '\"}}')
                        .asJson()
                        if (resultTransss.status >= 200 && resultTransss.status < 300){
                            logger.info("veremos el estado para el padre transicion ´(301)--> " + resultTransss.status)
                            logger.info("result: " + resultTransss.status + '; ' + resultTransss.statusText)
                        } else {
                            logger.info("Failed to find issue: Status: ${resultTransss.status} ${resultTransss.body}")
                        }
	
                    }
                }else if (DC >= 1){ // si alguna de las SubTasks estan en desarrollo pondremos la Story EN DESARROLLO (301)
                    logger.info("Pater--> " + paterStatus.toLowerCase())
                    if(paterStatus.toLowerCase()=="desarrollo completado"){
                        logger.info("Parent ya está DESARROLLO COMPLETADO. No es necesario transicionar.")
                        return
                    }else{
                        logger.info("entra en el else DC")
                        def resultTransss = post('/rest/api/2/issue/' + pater + '/transitions')
                        .header('accept', 'application/json')
                        .header('Content-Type', 'application/json')
                        .body('{\"transition\":{\"id\": \"' + transitionId1 + '\"}}')
                        .asJson()
                        if (resultTransss.status >= 200 && resultTransss.status < 300){
                            logger.info("veremos el estado para el padre transicion ´(301)--> " + resultTransss.status)
                            logger.info("result: " + resultTransss.status + '; ' + resultTransss.statusText)
                        } else {
                            logger.info("Failed to find issue: Status: ${resultTransss.status} ${resultTransss.body}")
                        }
                    }
                }else{
                    return "Failed to find issue: Status: ${result.status} ${result.body}"
                }
            }
        }
    } else{
        logger.info('la Issue es una '+tipoIssue.name+' y su estado es '+status+' con lo cual no hay movimientos')
        return 'la Issue es una '+tipoIssue.name+' y su estado es '+status+' con lo cual no hay movimientos'
    }
}else{
    logger.info('End')
}