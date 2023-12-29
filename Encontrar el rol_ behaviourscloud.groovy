def user = get('/rest/api/2/myself')
        .header('Content-Type', 'application/json')
        .asObject(Map)

def currentUser = user.body.accountId
//return currentUser
def issueKey = 'EUMHD-10319'
def parts = issueKey.split("-")
def prefix
def issue
if (parts.size() > 0) {
     prefix = parts[0]
}
// Retrieve information about the issue using Jira REST API
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)

// Check if the request was successful (status code 200)
if (result.status == 200) {
    // If successful, store the issue details in the 'issue' variable
    issue = result.body
} else {
    // If not successful, return an error message
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

def issuetype = issue.fields.issuetype.id
def description = issue.fields.description
//return issue
logger.info('Llega->'+currentUser)

int rol = 10002
def projectRoles = get('/rest/api/3/project/'+prefix+'/role/'+rol)
        .header('Content-Type', 'application/json')
        .asObject(Map)
def AdminRoles = projectRoles.body.actors.actorUser.accountId
for(def x = 0; x < AdminRoles.size(); x++){
    if (currentUser == AdminRoles[x] ){
        
    }
}
return AdminRoles