// import com.atlassian.jira.component.ComponentAccessor
// import com.atlassian.jira.issue.CustomFieldManager
// import com.atlassian.jira.issue.fields.CustomField
// import java.util.Date
// 
// CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
// CustomField cf = cfm.getCustomFieldObjectByName("Last Replied")
// Date lastReplied = (Date)issue.getCustomFieldValue(cf)
// Date updated = issue.getUpdated()
// return lastReplied != null ? !updated.after(lastReplied) : false

// Define the key of the Jira issue to be checked
def issueKey = 'EUMHD-10319'

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

// Import necessary classes for working with dates and times
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter

// Initialize variables
boolean respuesta = false // 'respuesta' is Spanish for 'response'
def scriptedFieldName = "Last Replied"
def returnValue = ""

// Retrieve scripted field properties for the specified issue using Jira REST API
def scriptedFieldsForIssue = get("rest/api/2/issue/${issueKey}/properties")
        .header('Content-Type', 'application/json')
        .asObject(Map)
        .body
        .keys
        .findAll { it['key'].contains('scripted-field-')}['self']

// Check if there are no scripted fields for the issue
if (scriptedFieldsForIssue == []) {
    return "No Scripted Field value exists yet on issue ${issueKey} with the name ${scriptedFieldName}."
}

// Iterate over each scripted field for the issue
scriptedFieldsForIssue.each {
    // Retrieve the value of the scripted field using Jira REST API
    def potentialVal = get("${it}")
        .header('Content-Type', 'application/json')
        .asObject(Map)
        .body

    // Check if the scripted field has the specified name
    if (potentialVal.value.name == "${scriptedFieldName}") {
        // If yes, store its value in the 'returnValue' variable
        returnValue = potentialVal.value.value
    }
}

// Retrieve the 'updated' field of the issue
def updated = issue.fields.updated

// Check if 'returnValue' is not null and 'updated' is less than 'returnValue'
if(returnValue != null &&  updated < returnValue ) {
    // If true, set 'respuesta' to true
    respuesta = true
}

// Return the final response
return respuesta
