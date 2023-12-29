def issueKey = 'ADS-461'
def issue
def result = get('/rest/api/2/issue/' + issueKey)
        .header('Content-Type', 'application/json')
        .asObject(Map)
if (result.status == 200){
    issue = result.body
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}

def sp = 'xx'
def aa = 'xx'
def fecha = "xx/xx/xxxx"
def mk = 'xx'
if (issue.fields.customfield_10735) sp = issue.fields.customfield_10735.value
if (issue.fields.customfield_10736) aa = issue.fields.customfield_10736.value
if (issue.fields.customfield_10737) fecha = issue.fields.customfield_10737
if (issue.fields.customfield_10738) mk = issue.fields.customfield_10738

def partsdata = fecha.split("-")
def data = partsdata[2]+'/'+partsdata[1]+'/'+partsdata[0]
def partsaa = aa.split(" ")
aa = partsaa[0]
if (sp == 'Yes'){
    sp = 'Sp'
}
def campo = sp+'-'+aa+'-'+data+'-'+mk+' '+issue.fields.summary
return campo
/*
if (sp != 'xx' && aa!= 'xx' && mk  != 'xx'){
    
    def update = put("/rest/api/2/issue/${issueKey}") 
        .queryString("overrideScreenSecurity", Boolean.TRUE) 
        .header('Content-Type', 'application/json')
        .body([
            fields: [
                    summary: campo+' '+
            ]
        ])
        .asString()
    
    if (update.status == 204) { 
        return 'Success'
    } else {
        return "${update.status}: ${update.body}"
    }
}
*/