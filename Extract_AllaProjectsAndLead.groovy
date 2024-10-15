// Obtener todos los proyectos de JIRA
def projectsResponse = get('/rest/api/2/project')
        .header('Content-Type', 'application/json')
        .asObject(List)

if (projectsResponse.status != 200) {
    return "Error: No se pudieron obtener los proyectos. Estado: ${projectsResponse.status}."
}

def projects = projectsResponse.body

// Crear la salida en formato humano
def result = new StringBuilder()

projects.each { project ->

    // Obtener detalles del proyecto incluyendo el lead
    def projectDetailsResponse = get('/rest/api/3/project/' + project.key)
        .header('Content-Type', 'application/json')
        .asObject(Map)
    
    def lead = 'Sin responsable'
    
    if (projectDetailsResponse.status == 200) {
        lead = projectDetailsResponse.body.lead?.displayName ?: 'Sin responsable'
    } else {
        lead = "Error al obtener lead: Status: ${projectDetailsResponse.status}"
    }

    // Agregar el resultado del proyecto y su lead a la salida
    result.append("${project.name} -> ${project.projectTypeKey}\nResponsable: ${lead}\n")
}

return result.toString()
