// Definir el proyecto y las consultas
def projectKey = "HST"
def startDate = "2023-07-01"
def endDate = "2023-12-31"
def consulta1 = "project = HST AND resolved >= $startDate AND resolved <= $endDate and 'Area[Dropdown]' = Platform"
def consulta2 = "project = HST and resolved >= $startDate AND resolved <= $endDate and assignee in (712020:de89743b-d39b-4f83-abe5-04e539f32fcf, 64268b186b29c052ab2fa643, 642690345534b0bf744337ff)"

// Función para obtener el total de issues de una consulta
def getTotalIssues(jqlQuery) {
    def result = get('/rest/api/2/search')
        .header('Content-Type', 'application/json')
        .header("Accept", "application/json")
        .queryString("jql", jqlQuery)
        .asJson()

    if (result.status == 200) {
        return result.body.object.total
    } else {
        throw new RuntimeException("Failed to find issue: Status: ${result.status} ${result.body}")
    }
}

// Obtener el total de issues para cada consulta
def platforms = getTotalIssues(consulta1)
def hst = getTotalIssues(consulta2)

// Calcular el porcentaje y determinar si es superior al 80%
float porcentaje = hst * 100 / platforms
def resultado = porcentaje > 80 ? "Superior al 80%" : "Inferior o igual al 80%"

// Loggear resultados
logger.info("Total de Issues Plataformas = $platforms")
logger.info("Total de Issues HSTeam = $hst")
logger.info("Porcentaje = $porcentaje% ($resultado)")

return resultado
