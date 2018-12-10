def hecho = false

while(hecho == false){ //mientras sea false que lo haga 
    def links = issue.fields.issuelinks //miramos los hijos guardados en una array
        def numero = fields.issuelinks.size() //mirará el numero de hijas que tiene
        if (numero >= 1){ //si tiene hijas buscará como se llaman y si estas hijas tienen hijas
            links.each{
                def x = 0
                x++
                def Stories = it.inwardIssue.key //busco el nombre KEY de las stories hijas
                def resultDem = get('/rest/api/2/issue/' + Stories) //aqui busco todos los datos de cada hija de la demanda
                    .header('Content-Type', 'application/json')
                    .asObject(Map)
                if (resultDem.status == 200){
                    logger.info('succes demanda hija'+x+'-->'+Stories)
                } else {
                    return "Failed to find issue: Status: ${resultDem.status} ${resultDem.body}"
                }
                def SubTask = resultDem.body.fields.subtasks //aqui tengo todas las subtasks de la story hija de la demanda
                if(SubTask){
                SubTask.each{
                    subTasKey = it.key
                    //cancela SubTask
                    def resultDel = post("/rest/api/3/issue/" + subTasKey + "/transitions")
                        .header("Content-Type", "application/json")
                        .body([
                            "transition": [
                                "id": "391"
                            ]
                        ])
                        .asString()
                        assert resultDel.status == 204     
                        logger.info(resultDel.toString())
                    }
                }else{
                //cancela Story
                def resultDel = post("/rest/api/3/issue/" + Stories + "/transitions")
                        .header("Content-Type", "application/json")
                        .body([
                            "transition": [
                                "id": "531"
                            ]
                        ])
                        .asString()
                        assert resultDel.status == 204     
                        logger.info(resultDel.toString())
                    }
                }
            }else{
               hecho = true 
            }
        
        hecho = true
    }
