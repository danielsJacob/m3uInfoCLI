// By Jacob Daniels 12/11/2025
// m3uInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking

data class Channel(
    val name: String,
    val url: String,
    val group: String? =null,
    val logo: String? =null
)

fun main(): Unit = runBlocking {
    println("Starting up...")
    println("Running tests...")
    println("=== M3U Inspector ===")
    println("Enter M3u Url: ")

    val urlInput = readln().trim()

    if (urlInput.isBlank()) {
        println("No URL was provided. Exiting...")
        return@runBlocking
    }

    val client = HttpClient(CIO)

    try {
        println("\n Downloading playlist...")
        val playlistText = client.get(urlInput).bodyAsText()

        val channels = praseM3u(playlistText)

        if (channels.isEmpty()) {
            println("No playlist. Exiting...")
        } else {
            println("Found ${channels.size} channels \n")

        }
    } catch (e: Exception) {
        println("Error while downloading playlist:")
        println("Error: ${e.localizedMessage}")
    } finally {
        client.close()
    }
}

fun praseM3u(playlistText: String): List<Channel> {
    val lines = playlistText.lines()
    val channels = mutableListOf<Channel>()

    var pendingExtInf: String? = null

    for (rawLine in lines){
        val line = rawLine.trim()
        if (line.isEmpty()) continue
        if (line.startsWith("#EXTINF", ignoreCase = true)) {
            pendingExtInf = line
        } else if (!line.startsWith("#")) {
            val channel = parseChannelFromExtInf(pendingExtInf, line)
            if (channel != null) {
                channels.add(channel)
            }
            pendingExtInf = null
        }
    }
    return channels
}

fun parseChannelFromExtInf(extInf: String?, streamUrl: String): Channel? {
    if (extInf == null) return null

    val commaIndex = extInf.indexOf(',')
    val metaPart = if (commaIndex >= 0) extInf.substring(0, commaIndex) else extInf
    val titlePart = if (commaIndex >= 0) extInf.substring(commaIndex + 1).trim() else "Unknown"

    fun extractAttr(name: String): String? {
        val regex = Regex("""$name="([^"]*)"""")
        return regex.find(metaPart)?.groupValues?.get(1)
    }

    val tvgName = extractAttr("tvg-name")
    val group = extractAttr("group-title")
    val logo = extractAttr("tvg-logo")

    val displayName = tvgName ?: titlePart

    return Channel(
        name = displayName,
        url = streamUrl,
        group = group,
        logo = logo
    )
}