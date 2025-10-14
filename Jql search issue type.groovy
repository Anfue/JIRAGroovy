/*
 * ScriptRunner Cloud
 * Cuenta cuántos issues de cada tipo existen en toda la instancia.
 * Compatible con el nuevo endpoint: GET /rest/api/3/search/jql
 */

def jqlQuery = "created >= -300d"
def issueTypeCounts = [:].withDefault { 0 }
def nextPageToken = null
def maxResults = 100

while (true) {
    // Construcción de la petición
    def req = get("/rest/api/3/search/jql")
        .header("Accept", "application/json")
        .queryString("jql", jqlQuery)
        .queryString("maxResults", maxResults)
        .queryString("fields", "issuetype")

    if (nextPageToken) {
        req.queryString("nextPageToken", nextPageToken)
    }

    def response = req.asObject(Map)

    if (response.status != 200) {
        throw new RuntimeException("Error en la búsqueda: ${response.status} - ${response.body}")
    }

    def data = response.body
    def issues = data?.issues ?: []

    // Contamos los tipos
    issues.each { issue ->
        def typeName = issue.fields?.issuetype?.name ?: "Unknown"
        issueTypeCounts[typeName]++
    }

    // Si no hay más páginas, salimos del bucle
    if (data.isLast || !data.nextPageToken) break
    nextPageToken = data.nextPageToken
}

// Ordenamos resultados descendentes
def sortedResults = issueTypeCounts.sort { -it.value }

// Construimos salida en texto legible
def output = "📊 *Issue Types in Instance*\n\n"
sortedResults.each { type, count ->
    output += "- ${type}: ${count}\n"
}
logger.info(output)
return output
