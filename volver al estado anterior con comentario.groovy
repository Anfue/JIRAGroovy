def issueKey = 'SDYT-16'
def p = 0
def result = get("/rest/api/2/issue/${issueKey}?expand=changelog")
.header('Content-Type', 'application/json')
.asObject(Map)
if (result.status == 200){
    def l = result.body.changelog.size()
    def st = result.body.changelog.histories.items
    for (def x = 0; x < l; x++){
        if (st.field == "status"){
            if (st.toString != "Work in progress" ){
                def result2 = put("/rest/api/2/issue/${issueKey}?expand=changelog")
                .header('Content-Type', 'application/json')
                .body([
                        changelog:[
                            histories:[
                                items:[
                                    toString : "Work in progress"
                                ]
                            ]
                        ]
                    ])
                .asString()
                if (result2.status >= 200 || result2.status <= 300) {
                    return 'Success'
                } else {
                    return "${result2.status}: ${result2.body}"
                }          
            }
        }
    }
    return st.field[p-1]
    
} else {
    return "Failed to find issue: Status: ${result.status} ${result.body}"
}
