if(issue.fields.issuetype.name == 'Demanda' || issue.fields.issuetype.id = '10304' ){
    logger.info('la issue '+issue.key+' es una '+issue.fields.issuetype.name+' clona-->'+issue.fields.issuelinks.type.outward)
    def link = issue.fields.issuelinks
    if (link){
        link.each{
            //primero averiguamos el tipo de issue que es
            def tipoIssue = issue.fields.issuetype
            def clonaA =' '

            if(it.type.outward){
                clonaA = it.type.outward
            }
            int totalSP = 0
            if ((tipoIssue.name == 'Historia' || tipoIssue.id == '10300') && clonaA == "clona a"){
                logger.info('clonaA2-->'+clonaA)
                def demandaKey = it.outwardIssue.key //SI TIENE UNA DEMANDA
                def tipoClon = it.outwardIssue.fields.issuetype.name
                if(tipoClon == 'Demanda'){
                    def resultD = get('/rest/api/2/issue/' + demandaKey)
                    .header('Content-Type', 'application/json')
                    .asObject(Map)
                    if (resultD.status == 200){
                        logger.info('success ok')
                    } else {
                        return "Failed to find issue: Status: ${resultD.status} ${resultD.body}"
                    }
    
                    //vemos los hijos que tiene la Demanda
                    def links =resultD.body.fields.issuelinks
                    //Vemos los Storypoints que tiene asignada la demanda
                    def storyPointsDemanda = resultD.body.fields.customfield_10713 //campo StoryPoints Demanda
                    logger.info('Story Points de la demanda son-->'+storyPointsDemanda)
                    links.each{
                        logger.info('it.inwardIssue.fields.issuetype.name.toLowerCase()-->'+it.inwardIssue.fields.issuetype.name.toLowerCase())
                        if (it.inwardIssue.fields.issuetype.name.toLowerCase() == 'historia' && (it.type.outward == 'clona a' || it.type.id == '10001')){
                            //vemos los storypoints que tiene la Story
                            def storyHija = it.inwardIssue.key
                            def result2 = get('/rest/api/2/issue/' + storyHija)
                            .header('Content-Type', 'application/json')
                            .asObject(Map)
                            if (result2.status == 200){
                                logger.info('succes ok')
                            } else {
                                return "Failed to find issue: Status: ${result2.status} ${result2.body}"
                            }
                            if (result2.body.fields.customfield_10119){
                                def storyPointsStory = result2.body.fields.customfield_10119 //campo story point Story
                                storyPointsStory = storyPointsStory.toInteger()
                                //iremos sumando las storyPoints
                                totalSP = totalSP+storyPointsStory
                                logger.info('sumamos la '+storyHija+' con '+storyPointsStory+' StoryPoints')
                            }else{
                                def storyPointsStory = 0
                            }
                        }
                    }
                    logger.info('total de story points-->'+totalSP)
                    //añadiremos el total de story points de las stories a la demanda
                    def resultDem = put('/rest/api/2/issue/'+demandaKey) 
                    .queryString("overrideScreenSecurity", Boolean.TRUE) //para saltarse los campos que no se ven en pantalla
                    .header('Content-Type', 'application/json')
                    .body([
                            fields: [
                                customfield_10713: totalSP.toString()+' SP'
                            ]
                        ])
                    .asString()
                    if (resultDem.status == 204) { 
                        logger.info('Success')
                    } else {
                        return "${resultDem.status}: ${resultDem.body}"
                    }
                    logger.info('se ha modificado la '+demandaKey+' con '+totalSP+' Storypoints')
                }else {
                    logger.info('2')
                }
            }else {
                logger.info('No es historia o no clona a nadie')

            }
        }

        //Script de hora actual
        String oldDate = '20150702'
        def Date date = Date.parse( 'yyyyMMdd', oldDate )
        String currentDate = new Date().format( 'dd/MM/yyy' )
        def Date data = new Date();   // given date
        def Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(data);   // assigns calendar to given date 
        logger.info('Se actualiza el dia '+currentDate+' a las '+calendar.get(Calendar.HOUR_OF_DAY) +':'+calendar.get(Calendar.MINUTE))
        return 'Se actualiza el dia '+currentDate+' a las '+calendar.get(Calendar.HOUR_OF_DAY) +':'+calendar.get(Calendar.MINUTE)
    }else {
        logger.info('no hay DEMANDA para actualizar la ' + issue.fields.issuetype.name +'-->'+issue.key)
        //Script de hora actual
    }
}else{
    
    logger.info( 'No es Demanda')
    return
}