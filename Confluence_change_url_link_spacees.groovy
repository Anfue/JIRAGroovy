import java.util.regex.Pattern

// === CONFIGURACIÓN ===
def spaceKey = "IS"  // Clave del espacio
def replacements = [
    "https://devops.myspace.com/portal/dosearchsite.action?" : "https://myspace-sandbox-904.atlassian.net/wiki/search?",
    "https://intranet.old.com"                                   : "https://intranet.new.com"
]

// === FUNCIONES ===
def processPage(page, replacements) {
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
def next = "/wiki/api/v2/spaces/${spaceId}/pages?body-format=storage&limit=50"

while (next) {
    def response = get(next).asObject(Map).body
    def pages = response.results
    logger.info('Pages->'+pages.id)
    pages.each { processPage(it, replacements) }

    // Paginación
    next = response._links?.next
    if (next) {
        next = next.replace("/wiki", "") // Ajuste para la URL relativa en la API
    }
}
