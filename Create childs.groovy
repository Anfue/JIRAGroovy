def issueKey = issue.key
logger.info('Working on the issue->' + issueKey)

// Get issue information
/*
def issueResponse = get('/rest/api/2/issue/' + issueKey)
    .header('Content-Type', 'application/json')
    .asObject(Map)

if (issueResponse.status != 200) {
    return "Failed to find issue: Status: ${issueResponse.status} ${issueResponse.body}"
}

def issue = issueResponse.body
*/
if (issue.fields.customfield_10801.value != 'Yes' || issue.fields.issuetype.name != 'Epic' || issue.fields.customfield_10802 != null) {
    return 'Te value from child issuetype or value empty is OUT'
}

def fixversion = issue.fields.versions?.getAt(0)
def affectedVersion = issue.fields.customfield_10300

logger.info('Fix value->' + fixversion)

def countries = [
    'France': '11203', 'Germany': '11206', 'Italy': '11212', 'UK': '11243', 'Mexico': '11221',
    'Australia': '11459', 'Belgium': '11189', 'Denmark': '11199', 'Spain': '11238', 'Greece': '11207',
    'Turkey': '11242', 'Switzerland': '11240', 'Austria': '11188', 'Ireland': '11210', 'Sweden': '11239',
    'Norway': '11227', 'Finland': '11202', 'New Zealand': '11226', 'Poland': '11230', 'Luxembourg': '11217',
    'Netherlands': '11225', 'Bulgaria': '11191', 'Cyprus': '11197', 'Egypt': '11200', 'Israel': '11211',
    'Morocco': '11224', 'Malta': '11219', 'Peru': '11229', 'Tunisia': '11241', 'Uruguay': '11245',
    'Andorra': '11185', 'Estonia': '11201', 'Latvia': '11215', 'Lithuania': '11216'
]

def assignees = [
    'FR': '712020:77548515-8101-47e4-986b-0938accd189c', 'DE': '712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5',
    'IT': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64f', 'UK': '642693dbb05b4e3e7dac1cc8',
    'MX': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'AU': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'BE': '712020:77548515-8101-47e4-986b-0938accd189c', 'DK': '712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5',
    'ES': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64f', 'GR': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64f',
    'TR': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64f', 'CH': '712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5',
    'AT': '712020:164d762e-94c4-4cb3-9ae5-2c21ad667fd5', 'IE': '642693dbb05b4e3e7dac1cc8',
    'SE': '642693dbb05b4e3e7dac1cc8', 'NO': '642693dbb05b4e3e7dac1cc8', 'FI': '642693dbb05b4e3e7dac1cc8',
    'NZ': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'PL': '712020:77548515-8101-47e4-986b-0938accd189c',
    'LU': '712020:77548515-8101-47e4-986b-0938accd189c', 'NL': '712020:77548515-8101-47e4-986b-0938accd189c',
    'BG': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'CY': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'EG': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'IL': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'MA': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'MT': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'PE': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'RE': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'TN': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e', 'UY': '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e',
    'AD': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64', 'EE': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64',
    'LV': '712020:7f209b9d-68b8-4e08-944d-f56e509ca64', 'LT': '642693dbb05b4e3e7dac1cc8'
]

def projectKey = 'MSB'
def projectId = get("/rest/api/2/project/${projectKey}").asObject(Map).body.id
def issueTypes = get("/rest/api/2/issuetype/project?projectId=${projectId}").asObject(List).body

def taskTypeId = issueTypes.find { it['name'] == 'Rollouts' }['id']
def taskTypeIdTask = issueTypes.find { it['name'] == 'Task' }['id']

countries.each { countryKey, countryId ->
    def assignee = assignees[countryKey] ?: '712020:5ca420e3-377a-4a17-b83d-c17100e9f05e'
    
    def affectedCountryArray = [[id: countryId]]
    logger.info('Creating child issue for country->' + countryKey)

    def createIssueResponse = post('/rest/api/2/issue')
        .header('Content-Type', 'application/json')
        .body([
            fields: [
                summary: " ${issue.fields.summary} - [${countryKey}]",
                parent: [id: issue.id],
                customfield_10318: affectedCountryArray,
                project: [key: projectKey],
                assignee: [accountId: assignee],
                issuetype: [id: taskTypeId],
                fixVersions: issue.fields.fixVersions,
                customfield_10015: issue.fields.customfield_10015,
                components: issue.fields.components,
                customfield_10440: issue.fields.customfield_10440,
                labels: issue.fields.labels,
                customfield_10781: issue.fields.customfield_10781,
                customfield_10762: issue.fields.customfield_10762,
                customfield_10820: issue.fields.customfield_10820,
                customfield_10845: issue.fields.customfield_10845,
                customfield_10762: issue.fields.customfield_10762,
                customfield_10844: issue.fields.customfield_10844,
                customfield_10858: issue.fields.customfield_10858
            ]
        ])
        .asString().body
    
    def createIssueJson = new groovy.json.JsonSlurper().parseText(createIssueResponse)
    if (createIssueJson != null) {
        logger.info("Child issue created successfully for country ${countryKey}.")
    } else {
        logger.error("Failed to create child issue for country ${countryKey}.")
    }
}
