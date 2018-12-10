def issueKey = 'DEM-186'

def result = get('/rest/api/2/issue/' + issueKey)
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200){
    def links = result.body.fields.issuelinks
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

def links = result.body.fields.issuelinks //Busco todos los issuelinks de la DEM para saber el tipo de issue que cuelga de él
//def es_story =  result.body.fields.issuelinks //Veo el campo de la Story
def status = result.body.fields.issuelinks.inwardIssue.fields.status // busco los estados en el que está la STORY que depende de la DEM
def num = result.body.fields.issuelinks.size() //numero de tareas que cuelgan de la DEM

for(def x = 0; x < num; x++){
    //preguntamos por los primeros estados del WF antes de hacer un movimiento
    if ((links[x].inwardIssue.fields.issuetype.name == 'Historia' || links[x].inwardIssue.fields.issuetype.id == '10300') && (status[x].name == 'PRIORIZACIÓN' || status[x].name == 'DESCOMPOSICIÓN EN TAREAS' || status[x].name == 'EN DESARROLLO' || status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGA NO SATISFACTORIA' || status[x].name ==  'ANALISIS STORY')){
    //si esta en un estado anterior a "ENTREGADA (PTE. ACEPTACIÓN)" no haremos que avance la DEM
        logger.info('entra en if')
        logger.info(links[x].inwardIssue.key + ' esta en estado ' + status[x].name)
        //preguntamos por los estados en los que ya hace movimientos la DEMANDA
    }else if ((links[x].inwardIssue.fields.issuetype.name == 'Historia' || links[x].inwardIssue.fields.issuetype.id == '10300') && (status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGADA (PTE. ACEPTACIÓN)' || status[x].name == 'ACEPTADA Y PENDIENTE PaP' || status[x].name == 'DEPLOY BD' || status[x].name == 'DEPLOY CONFIG' || status[x].name == 'NO INCORPORADA')){
        //si esta en un estado posterios a "ENTREGADA (PTE. ACEPTACIÓN)" si haremos que avance la DEM
        //poniendo el mismo codigo que ya hay en la post-function de 
        logger.info('entra en else if')
        logger.info(links[x].inwardIssue.key + ' esta en estado ' + status[x].name)
        //el único estado que no está reflejado es el else final, cuando está cerrada
    }else{
        logger.info('else 2=='+links[x].inwardIssue.key)
    }
}
//ENTREGADA (PTE. ACEPTACIÓN)



/*
 * 
 * 
 * 
 * 
 * 
 * */
def streamDestino = issue.fields.customfield_10673?.value
def streamOrigenKey = issue.fields.project.key
///////////////////////
def num = issue.fields.issuelinks.size()//buscamos el numero de issuelinks
def links = issue.fields.issuelinks //buscamos el valor de los issuelinks
/*nuevo codigo 01/10/2018*/
def status = issue.fields.issuelinks.inwardIssue.fields.status // busco los estados en el que está la STORY que depende de la DEM
/*nuevo codigo 01/10/2018*/
//hacemos un bucle para buscar si esta clonada (10001) y si es una historia
for(def x = 0; x< num; x++){
//si esta clonada (10001) y es una historia salimos del bucle porque no ha de crear ninguna Story
    if (links[x].type.id == '10001' && links[x].inwardIssue?.fields.issuetype.name == 'Historia'){
        return false
    }
    logger.info('entra en el for1')
}
/*nuevo codigo 01/10/2018*/
logger.info('antes del for')
for(x = 0; x < num; x++){
    logger.info('entra en el for2')
    //preguntamos por los primeros estados del WF antes de hacer un movimiento
    if ((links[x].inwardIssue.fields.issuetype.name == 'Historia' || links[x].inwardIssue.fields.issuetype.id == '10300') && (status[x].name == 'PRIORIZACIÓN' || status[x].name == 'DESCOMPOSICIÓN EN TAREAS' || status[x].name == 'EN DESARROLLO' || status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGA NO SATISFACTORIA' || status[x].name ==  'ANALISIS STORY')){
    //si esta en un estado anterior a "ENTREGADA (PTE. ACEPTACIÓN)" no haremos que avance la DEM
        logger.info('entra en if y hacemos un return')
        logger.info(links[x].inwardIssue.key + ' esta en estado ' + status[x].name)
        return false
        //preguntamos por los estados en los que ya hace movimientos la DEMANDA
    }else if ((links[x].inwardIssue.fields.issuetype.name == 'Historia' || links[x].inwardIssue.fields.issuetype.id == '10300') && (status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGADA (PTE. ACEPTACIÓN)' || status[x].name == 'ACEPTADA Y PENDIENTE PaP' || status[x].name == 'DEPLOY BD' || status[x].name == 'DEPLOY CONFIG' || status[x].name == 'NO INCORPORADA')){
        //si esta en un estado posterios a "ENTREGADA (PTE. ACEPTACIÓN)" si haremos que avance la DEM
        //poniendo el mismo codigo que ya hay en la post-function de 
        
        /*codigo antiguo*/
        
if(streamDestino!=null && streamDestino!="")
    logger.info("stream destino: " + streamDestino)
else{
    logger.info("stream destino está vacío")
    
    if(streamOrigenKey=="DEM"){
        logger.info("stream destino está vacío, streamOrigen es " + streamOrigenKey + ". Crear STO en proyecto RDP Elena")
        return true
    }
}

if(streamDestino!=null && streamDestino!="" && streamDestino=="RDP Elena")
    return true
    
    //si no tiene, entonces seguimos con la condición y creamos la Story.

        /*codigo antiguo*/
        
        
        logger.info('entra en else if')
        logger.info(links[x].inwardIssue.key + ' esta en estado ' + status[x].name)
        //el único estado que no está reflejado es el else final, cuando está cerrada
    }else{
        logger.info('else 2=='+links[x].inwardIssue.key)
    }
}
//ENTREGADA (PTE. ACEPTACIÓN)
/*nuevo codigo 01/10/2018*/






////////////////////////nuevo codigo 01/10/2018


def typeClonerId = '10001' //id del tipo de enlace Clonar
def transitionId = '' //transicion (Finalizar desarrollo) DEMANDA a [Entregada]
/*
//passat a condition
if(issue.fields.issuetype.id=='10300' && (issue.fields.status.id=="10320" || issue.fields.status.name=='ENTREGADA (PTE. ACEPTACIÓN)'))
{}//si es Story en estado [DESARROLLO FINALIZADO]
else{
	logger.info("no cumple: " + issue.fields.issuetype.name + ", " + issue.fields.status.name + ". Salir")
	return
}
*/
/*------------------EDU--------------------*/
def link = issue.fields.issuelinks //Busco todos los issuelinks de la DEM para saber el tipo de issue que cuelga de él
def status = issue.fields.issuelinks.inwardIssue.fields.status // busco los estados en el que está la STORY que depende de la DEM
def num_issue = issue.fields.issuelinks.size() //numero de tareas que cuelgan de la DEM
def x = 0
def num = 10000
Integer[] arraynum =[]
for(x = 0; x < num_issue; x++){
    //preguntamos por los primeros estados del WF antes de hacer un movimiento
    if ((link[x].inwardIssue.fields.issuetype.name == 'Historia' || link[x].inwardIssue.fields.issuetype.id == '10300') 
    && (status[x].name == 'PRIORIZACIÓN' || status[x].name == 'DESCOMPOSICIÓN EN TAREAS' || status[x].name == 'EN DESARROLLO'||
    status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGA NO SATISFACTORIA' || 
    status[x].name ==  'ANALISIS STORY')){
        num=0
        arraynum[x] = num
    //después preguntamos por el resto que hará avanzar 
    }else if ((link[x].inwardIssue.fields.issuetype.name == 'Historia' || link[x].inwardIssue.fields.issuetype.id == '10300')
    && (status[x].name == 'DESARROLLO FINALIZADO' || status[x].name == 'ENTREGADA (PTE. ACEPTACIÓN)' ||
    status[x].name == 'NO INCORPORADA' || status[x].name == 'DEPLOY CONFIG' || status[x].name == 'DEPLOY BD' ||
    status[x].name == 'ACEPTADA Y PENDIENTE PaP' ))){
        num=1
        arraynum[x] = num
    }else if ((link[x].inwardIssue.fields.issuetype.name == 'Historia' || link[x].inwardIssue.fields.issuetype.id == '10300')
    && status[x].name == 'CERRADA'){
        num=2
        arraynum[x] = num
    }
}        
def estado_demanda_por_story = arraynum.min() //cojo el estado de la demanda más bajo porque será el que
//menos habrá evoluvionado en el tablero correspondiente
        
if (estado_demanda_por_story == 0){
    transitionId = '71'//DEMANDA --> en desarrollo
}else if (estado_demanda_por_story == 1){
    transitionId = '231'//DEMANDA --> Entregas PEndientes
}else if (estado_demanda_por_story == 2){
    transitionId = '241'//DEMANDA --> Cerrada

        /*codigo original */
        logger.info("entra a comprobar si tiene DEMANDA origen")

        def links = (List<Map<String, Object>>) issue.fields.issuelinks //get all links
	
            links.each{ 
            	if(it.type.id==typeClonerId && it.outwardIssue!=null){//if clones another issue
            		if(it.outwardIssue.fields.issuetype.id == '10304' || it.outwardIssue.fields.issuetype.name =='Demanda'){//the linked issue is 'Demanda'
            			logger.info("Demanda linkada: " + it.outwardIssue.key)
            			
            			//TODO: obtener targetas linkadas a la demanda
            			//TODO: ver si todas están validadas
            			
            			//transition Demanda
            			def demandaKey = it.outwardIssue.key
            			
            			def resultTrans = post('/rest/api/2/issue/'+demandaKey+'/transitions')
            					.header('accept', 'application/json')
            					.header('Content-Type', 'application/json')
            					.body('{\"transition\":{\"id\": \"'+transitionId+'\"}}')
            					.asJson()
            			if (resultTrans.status >= 200 && resultTrans.status < 300){
            				logger.info("result transicionar: " + resultTrans.status + '; ' + resultTrans.statusText)
            			} else {
            				return "Failed to find issue: Status: ${resultTrans.status} ${resultTrans.body}"
            			}
            		}
            		else
            		    logger.info("no tiene enlaces a ninguna DEMANDA")
            	}
            }
        /*codigo original*/
        
        
    }else{
        logger.info('estan las dos en ENTREGADA (PTE. ACEPTACIÓN)=='+links[x].inwardIssue.key)
    }

//ENTREGADA (PTE. ACEPTACIÓN)
/*------------------EDU--------------------*/
