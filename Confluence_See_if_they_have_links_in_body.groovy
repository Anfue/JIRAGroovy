// === CONFIGURACIÓN ===
// ID de la página que quieres analizar
def pageId = "36798465"

// === 1️⃣ Obtener la información de la página ===
def response = get("/wiki/api/v2/pages/${pageId}?body-format=storage")
        .asObject(Map)
        .body

if (!response) {
    logger.error("❌ No se encontró la página con ID ${pageId}")
    return
}

// === 2️⃣ Extraer datos ===
def pageTitle = response.title
def pageBody = response.body?.storage?.value ?: ""
logger.info("📄 Título de la página: ${pageTitle}")

// === 3️⃣ Extraer todos los enlaces dentro del contenido ===
// Esto incluye tanto enlaces internos (/wiki/spaces/...) como externos (https://)
def linkRegex = /(?:href|src)="([^"]+)"/
def matcher = (pageBody =~ linkRegex)
def links = matcher.collect { it[1] }.unique()

// === 4️⃣ Mostrar resultados en log ===
logger.info("📘 --- CONTENIDO DE LA PÁGINA ---")
logger.info(pageBody.take(50000)) // Muestra los primeros 5000 caracteres (puedes ampliar si quieres)

if (links) {
    logger.info("🔗 --- ENLACES ENCONTRADOS ---")
    links.each { link ->
        logger.info("➡️ ${link}")
    }
} else {
    logger.info("⚠️ No se encontraron enlaces en esta página.")
}
