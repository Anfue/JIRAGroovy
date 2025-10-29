import java.util.regex.Pattern
 
 
// === CONFIGURACIÓN ===
def spaceKey = "ZC"  // Clave del espacio
// Mapa de dominios (clave = lo que quieres reemplazar, valor = por qué lo reemplazas)
def replacements = [
    "https://myspace.com/portal/dosearchsite.action?"            : "https://myspace.atlassian.net/wiki/search?",
    "https://myspace.com/portal/portal/dosearchsite.action?"  : "https://myspace.atlassian.net/wiki/search?"
]
def limit = 250
// === FUNCIONES ===
def processPage(page,  replacements) {
    def pageId = page.id
    def pageTitle = page.title
    def pageBody = page.body?.storage?.value ?: ""
    def pageVersion = page.version?.number ?: 1
 
    def updatedBody = pageBody
    def replacementsDone = []
 
    replacements.each { oldDomain, newDomain ->
        if (updatedBody.contains(oldDomain)) {
            updatedBody = updatedBody.replaceAll(Pattern.quote(oldDomain), newDomain)
            replacementsDone << "${oldDomain} → ${newDomain}"
        }
    }
 
    if (updatedBody != pageBody) {
        logger.info("🔁 Actualizando página: ${pageTitle}")
        replacementsDone.each { logger.info("➡️ ${it}") }
 
        def updatePayload = [
            id: pageId,
            status: "current",
            title: pageTitle,
            body : [
                storage: [
                    value: updatedBody,
                    representation: "storage"
                ]
            ],
            version: [
                number: pageVersion + 1
            ]
        ]
        logger.info('pageId->'+pageId)
        def updateResponse = put("/wiki/api/v2/pages/${pageId}")
                .header("Content-Type", "application/json")
                .body(updatePayload)
                .asObject(Map)
 
        if (updateResponse.status >= 200 && updateResponse.status < 300) {
            logger.info("✅ Página '${pageTitle}' actualizada correctamente a la versión ${(updateResponse.body?.version?.number) ?: '(desconocida)'}")
        } else {
            logger.error("❌ Error al actualizar '${pageTitle}': ${updateResponse.status} - ${updateResponse.body}")
        }
    } else {
        logger.info("⚠️ No se detectaron enlaces en '${pageTitle}', no se realizaron cambios.")
    }
}
 
// === 0️⃣ Obtener ID numérico del espacio ===
def spaceInfo = get("/wiki/api/v2/spaces?keys=${spaceKey}")
        .asObject(Map)
        .body
 
if (!spaceInfo.results || spaceInfo.results.size() == 0) {
    logger.error("❌ No se encontró el espacio con key ${spaceKey}")
    return
}
 
def spaceId = spaceInfo.results[0].id  // ID numérico
 
// === 1️⃣ Obtener todas las páginas del espacio con paginación ===
//def next = "/wiki/api/v2/spaces/${spaceId}/pages?body-format=storage&limit=${limit}"
def next = "/wiki/api/v2/pages?space-id=${spaceId}&limit=${limit}"
def cursor = 'Vacio'
def num = 0
 
// === 3️⃣ Recorrer las páginas ===
def response = get(next).asObject(Map).body
def pages = response.results
 
if (!pages || pages.isEmpty()) {
    logger.warn("⚠️ No se encontraron páginas en este rango (start=${startIndex}, limit=${limit}).")
} else {
    pages.each { page ->
        processPage(page, replacements)
        logger.info('ID->'+page.id+', TITLE->'+page.title)
        num++
    }
    logger.info('num->'+num)
    cursor = response._links.next
}
 
logger.info("✅ Análisis completado para las páginas del espacio '${spaceKey}'.")
return cursor
 
