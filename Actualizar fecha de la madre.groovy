def issueKey1 = issue.key
def result = get('restapi2issue' + issueKey1)
.header('Content-Type', 'applicationjson')
.asObject(Map)


def author = comment.author.displayName
def comentarioCreado = comment.body

def tipo = result.body.fields.issuetype.name
if(tipo == Demanda && comentarioCreado){}
else {
    logger.info(No es Demanda o comentario vacío. Salir)
    return
}

def clon = result.body.fields.issuelinks.type.inward
def nclon = result.body.fields.issuelinks.size()
def clonadaPor = result.body.fields.issuelinks.inwardIssue.key
def trans = result.body.fields.changelog

if(tipo == Demanda && comentarioCreado){
    logger.info('comentarioCreado --  '+ comentarioCreado)
    if (nclon = 1){
        for(def x = 0; x  nclon; x++ ){
            if(clonadaPor[x]!=null){
                logger.info('clonada por  ' + clonadaPor[x])
                logger.info('No hay key, no intentar crear comentario')
            }
            else{
                logger.info('clonada por  ' + clonadaPor[x])
                def commentResp = post(restapi2issue + clonadaPor[x] +comment)
                .header('Content-Type', 'applicationjson')
                .body([
                        body _${author}_ escribió n   + comentarioCreado
                        
                        ,author [key creado]
                    ])
                .asObject(Map)
                assert commentResp.status = 200 && commentResp.status  300
            }
        }
    }
}
