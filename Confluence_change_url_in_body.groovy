import java.util.regex.Pattern

// === CONFIGURACIÓN ===
// ID de la página que quieres analizar y actualizar
def pageId = "36339718"

// Mapa de dominios (clave = lo que quieres reemplazar, valor = por qué lo reemplazas)
def replacements = [
    "https://mysite-sandbox-904.atlassian.net/wiki/search?" : "https://mysite-sandbox-904.atlassian.net/dosearchsite.action?",
    "https://intranet.old.com"                                   : "https://intranet.new.com"
]

// === 1️⃣ Obtener la información de la página ===
def response = get("/wiki/api/v2/pages/${pageId}?body-format=storage")
        .asObject(Map)
        .body

if (!response) {
    logger.error("❌ No se encontró la página con ID ${pageId}")
    return
}

def pageTitle = response.title
def pageBody = response.body?.storage?.value ?: ""
def pageVersion = response.version?.number ?: 1

logger.info("📄 Título de la página: ${pageTitle}")
logger.info("📘 Versión actual: ${pageVersion}")

// === 2️⃣ Aplicar los reemplazos configurados ===
def updatedBody = pageBody
def replacementsDone = []

replacements.each { oldDomain, newDomain ->
    if (updatedBody.contains(oldDomain)) {
        updatedBody = updatedBody.replaceAll(Pattern.quote(oldDomain), newDomain)
        replacementsDone << "${oldDomain} → ${newDomain}"
    }
}

// === 3️⃣ Si hubo cambios, actualizar la página ===
if (updatedBody != pageBody) {
    logger.info("🔁 Se encontraron enlaces a reemplazar:")
    replacementsDone.each { logger.info("➡️ ${it}") }

    def updatePayload = [
        id: pageId,                        // 🔧 Campo obligatorio
        status: "current",                 // 🔧 Campo obligatorio
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

    def updateResponse = put("/wiki/api/v2/pages/${pageId}")
            .header("Content-Type", "application/json")
            .body(updatePayload)
            .asObject(Map)

    if (updateResponse.status >= 200 && updateResponse.status < 300) {
        logger.info("✅ Página actualizada correctamente a la versión ${(updateResponse.body?.version?.number) ?: '(desconocida)'}")
    } else {
        logger.error("❌ Error al actualizar la página: ${updateResponse.status} - ${updateResponse.body}")
    }

} else {
    logger.info("⚠️ No se detectaron enlaces coincidentes, no se realizaron cambios.")
}
