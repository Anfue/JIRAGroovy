def issueKey = 'HST-10229'
def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    issue = result.body
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
def customfield_10506 = issue.fields.customfield_10506
def labels = issue.fields.labels
def tipo = issue.fields.issuetype
def componente = issue.fields.components
def versions = issue.fields.versions
def customfield_10297 = issue.fields.customfield_10297

def customfield_10109 = issue.fields.customfield_10109
def fixVersions = issue.fields.fixVersions
def priority = issue.fields.priority
def customfield_10343 = issue.fields.customfield_10343
def customfield_10342 = issue.fields.customfield_10342

def reporter
def projectKey
def taskType
def projectId


if (issue.fields.assignee == '712020:fa1c874a-f53a-489e-9b61-886400add6f8' && tipo.id == '10218'){
projectKey = 'WS'
projectId = get("/rest/api/2/project/${projectKey}").asObject(Map).body.id
taskType = get("/rest/api/2/issuetype/project?projectId=${projectId}").asObject(List).body.find { it['name'] == 'Bug' }['id']
reporter = '64268b186b29c052ab2fa643'
}
// Crear la nueva issue clonada
def createIssueResponse = post('/rest/api/2/issue')
        .header('Content-Type', 'application/json')
        .body(
                [
                        fields: [
                                summary    : issue.key +' - ' + issue.fields.summary,
                                description: issue.fields.description,
                                project    : [
                                        key: projectKey
                                ],
                                issuetype  : [
                                        id: taskType
                                ],
                                customfield_10033 : [id: "10024"], // Typology
                                customfield_10297 : [id: "11134"], // Environment
                                assignee : [
                                    accountId : reporter
                                ],
                                labels : labels,
                                components : componente,
                                versions : versions,
                                customfield_10297 : customfield_10297
                        ]
                ])
        .asObject(Map)

if (createIssueResponse.status == 201) {
    // La nueva issue clonada se creó con éxito
    def clonedIssueKey = createIssueResponse.body.key

    // Adjuntar archivos adjuntos a la nueva issue
    if (issue.fields.attachment) {
        issue.fields.attachment.each { attachment ->
            def url = attachment.content
            def is = Unirest.get(url).asBinary().body
            def attachResponse = post("/rest/api/2/issue/${clonedIssueKey}/attachments")
                    .header("X-Atlassian-Token", "no-check")
                    .field("file", is, attachment.filename)
                    .asObject(Map)
            if (attachResponse.status != 200) {
                // Manejar errores si la adjunción de archivos falla
                return "Failed to attach file: Status: ${attachResponse.status} ${attachResponse.body}"
            }
        }
    }

    // Devolver la clave de la nueva issue clonada
    return "Cloned issue key: ${clonedIssueKey}"
} else {
    // Manejar errores si la creación de la issue clonada falla
    return "Failed to create cloned issue: Status: ${createIssueResponse.status} ${createIssueResponse.body}"
}
