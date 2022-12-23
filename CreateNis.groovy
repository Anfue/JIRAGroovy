def issueKey = 'UOC-16'
String value
def arr 
def arry = ['']
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    value = result.body.fields.description.split(/\|(?!\|\[)/)
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def values = value.split('h1')
for(def x = 0 ; x < values.size(); x++){
    arr = values as String[]
}
logger.info('Valor de arr Francisco->'+arr[2].split(/\s*,\s*/))
for(def y = 0; y < arr.size(); y++){
    //logger.info('arr->'+arr[y].split(''))
    arry[y] = arr[y].split(/\s*,\s*/) as String[]
}

if (result.body.fields.customfield_10037 == null || result.body.fields.customfield_10037 == '' ){
for(def x = 1 ; x < values.size(); x++){
        def val_mail = arry[x][1]
        def val_name = arry[x][0]
        def val_nis = arry[x][2]
        logger.info('val_mail->'+val_mail)
        logger.info('val_nis->'+val_nis)
        logger.info('val_name->'+val_name)
        if (val_name.contains(result.body.fields.creator.displayName)){
            logger.info('Entra y modifica NIS')
            def resultInsert = put('/rest/api/2/issue/' + issueKey)
                    .header('Content-Type', 'application/json')
                    .queryString("overrideScreenSecurity", Boolean.TRUE) 
                    .body([
                    fields:[
                            customfield_10037: val_nis
                    ]
                ])
                        .asString()
                if (resultInsert.status == 204) {
                    return 'Success'
                } else {
                    return "${resultInsert.status}: ${resultInsert.body}"
                }
        }
    }
}else{
    logger.info('No esta vacio')
}
