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
    result.append("${project.name} -> ${project.projectTypeKey}\n")
}

return result.toString()
