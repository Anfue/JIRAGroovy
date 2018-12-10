def issueKey1 = issue.key

/*def result = get('/rest/api/2/issue/' + issueKey1)
.header('Content-Type', 'application/json')
.asObject(Map)
result.body.
 */

String comentarioAnonimo = issue.fields.customfield_10693
def tipo = issue.fields.issuetype.name
def actualizar = issue.fields.summary

logger.info ("issueType -->" + tipo)
logger.info ("comment: " + comentarioAnonimo)

if(comentarioAnonimo && comentarioAnonimo.trim() != "."){
    logger.info('La IssueKey es --> '+issueKey1)
    
    //creamos comentario público
    def commentResp = post("/rest/api/2/issue/" + issueKey1 +"/comment")
    .header('Content-Type', 'application/json')
    .body([
            body: comentarioAnonimo
        ])
    .asObject(Map)
    assert commentResp.status >= 200 && commentResp.status < 300
}
else {
    logger.info("No hay comentario")
    return
}

//borramos contenido del campo comentario público
def result2 = put("/rest/api/2/issue/${issueKey1}")
.header('Content-Type', 'application/json')
.queryString("overrideScreenSecurity", Boolean.TRUE)
.body([
        fields: [
            customfield_10693: ""
        ]
    ])
.asString()
assert result2.status >= 200 && result2.status < 300

return
//actualizaremos la task de la siguiente manera

def aonimus = "" //customfield_10693

def resultt = get('/rest/api/2/issue/' + issueKey1)
.header('Content-Type', 'application/json')
.asObject(Map)
if (resultt.status == 200){
    aonimus = resultt.body.fields.customfield_10693
} else {
    logger.info( "Failed to find issue: Status: ${resultt.status} ${resultt.body}")
}
if(aonimus){
    def result22 = put('/rest/api/2/issue/' + issueKey1)
    .header('Content-Type', 'application/json')
    .body([
            fields:[
                customfield_10693: aonimus + ' '
            ]
        ])
    .asString()
    if (result22.status == 204) {
        logger.info( 'Success')
        logger.info(aonimus)
    } else {
        logger.info ("${result22.status}: ${result22.body}")
    }
}else{
    logger.info ('No hay customfield_10693')
}

