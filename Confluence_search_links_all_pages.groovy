import java.util.regex.Pattern

def spaceKey = "DTL"
def limit = 50

def processPage(pageId, pageTitle) {
    def response = get("/wiki/api/v2/pages/${pageId}?body-format=storage")
        .asObject(Map)
        .body

    if (!response) {
        logger.warn("⚠️ No se pudo obtener la página con ID ${pageId}")
        return
    }

    def pageBody = response.body?.storage?.value ?: ""
    def linkRegex = /(?:href|src)="([^"]+)"/
    def matcher = (pageBody =~ linkRegex)
    def links = matcher.collect { it[1] }.unique()

    logger.info("📄 Página: ${pageTitle} (ID: ${pageId})")

    if (links && links.size() > 0) {
        logger.info("🔗 Enlaces encontrados (${links.size()}):")
        links.each { link -> logger.info("➡️ ${link}") }
    } else {
        logger.info("⚠️ No se encontraron enlaces en esta página.")
    }

    logger.info("──────────────────────────────")
}

def spaceInfo = get("/wiki/api/v2/spaces?keys=${spaceKey}")
    .asObject(Map)
    .body

if (!spaceInfo.results || spaceInfo.results.size() == 0) {
    logger.error("❌ No se encontró el espacio con key ${spaceKey}")
    return
}

def spaceId = spaceInfo.results[0].id
logger.info("📘 Analizando espacio '${spaceKey}' (ID: ${spaceId})")

def next = "/wiki/api/v2/spaces/${spaceId}/pages?body-format=storage&limit=${limit}"

while (next) {
    def response = get(next).asObject(Map).body
    def pages = response.results
    pages.each { page ->
        def pageId = page.id
        def pageTitle = page.title
        processPage(pageId, pageTitle)
    }

    next = response._links?.next
    if (next) {
        next = next.replace("/wiki", "")
    }
}

logger.info("✅ Análisis completado para el espacio '${spaceKey}'.")
