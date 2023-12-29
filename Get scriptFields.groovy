def scriptedFieldName = "Last Replied"
def issueKey = 'EUMHD-10319'

def returnValue = ""

def scriptedFieldsForIssue = get("rest/api/2/issue/${issueKey}/properties")
        .header('Content-Type', 'application/json')
        .asObject(Map)
        .body
        .keys
        .findAll { it['key'].contains('scripted-field-')}['self']

if (scriptedFieldsForIssue == []) {
    return "No Scripted Field value exists yet on issue ${issueKey} with the name ${scriptedFieldName}."
}

scriptedFieldsForIssue.each {
    def potentialVal = get("${it}")
        .header('Content-Type', 'application/json')
        .asObject(Map)
        .body

    if (potentialVal.value.name == "${scriptedFieldName}") {
        returnValue = potentialVal.value.value
    }
}

return returnValue