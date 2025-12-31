package singh.akaalroop

import io.github.cdimascio.dotenv.dotenv
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import io.github.cdimascio.dotenv.dotenv
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageChangedEvent
import com.slack.api.model.event.MessageDeletedEvent
import com.slack.api.model.event.MessageEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

fun sendAIRequest(apiKey: String, item: String, items: List<String>): String? {
    val client = OkHttpClient()
    val url = "https://ai.hackclub.com/proxy/v1/chat/completions"
    val mediaType = "application/json; charset=utf-8".toMediaType()

    val jsonBody = JSONObject().apply {
        put("model", "google/gemini-2.5-flash-lite-preview-09-2025")
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", "The user has asked for a minecraft of recipe of $item but it couldn't be found. Please do not mix recipes such as iron block and block of raw iron are completely different. Use your knowledge of minecraft for this task. Please check for typos and synonyms and if could be found in this list: [$items] ONLY RETURN THE CORRECT ITEM FROM THE LIST EXACTLY, IF NOT AVAILABLE RETURN NOTHING")
            })
        }
        put("messages", messages)
    }

    val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")
        .post(jsonBody.toString().toRequestBody(mediaType))
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return "Error: ${response.code}"

        val responseData = response.body?.string() ?: return null
        val jsonResponse = JSONObject(responseData)

        return jsonResponse.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }
}

//Get item image using minecraft-api.vercel.app
fun fetchItemImage(item: String?): BufferedImage {
    if (item == null) return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val itemURL = "https://minecraft-api.vercel.app/images/items/$item"

    return try {
        ImageIO.read(URL(itemURL))
    } catch (e: Exception) {
        ImageIO.read(File("data/missing.png"))
    }

}

//Generate crafting recipe images at runtime
fun buildImage(item: String, items: List<String?>): File {

    val positions = arrayOf(
        Pair(60,34),  Pair(96,34),  Pair(132,34),
        Pair(60,70),  Pair(96,70),  Pair(132,70),
        Pair(60,106), Pair(96,106), Pair(132,106),

        Pair(248, 70) //Output
    )

    val craftingTableUI: BufferedImage = ImageIO.read(File("data/craftingTableUI.png"))

    val itemImgs: MutableList<BufferedImage> = ArrayList()

    for (item in items) { itemImgs.add(fetchItemImage(item));}

    val graphics: Graphics2D = craftingTableUI.createGraphics()

    for (i in 0 until itemImgs.size) {
        graphics.drawImage(itemImgs[i], positions[i].first, positions[i].second, null)
    }

    graphics.drawImage(fetchItemImage(item), positions[9].first, positions[9].second, null)

    graphics.dispose()

    val outputFile = File("data/tempItem.png")
    ImageIO.write(craftingTableUI, "png", outputFile)
    return outputFile
}

//Get recipe and run buildImage
fun getRecipe(item: String): File {
    //Lambda to convert item name to file name e.g., Acacia Boat -> acacia_boat.png
    val convert: (String) -> String = { itemName ->
        val formatted = itemName.replace(" ", "_").lowercase()
        "$formatted.png"
    }

    val formattedItem = item.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
    }

    var recipe = JSONObject();

    val jsonString: String = File("data/recipes.json").readText()
    val jsonArray = JSONArray(jsonString)

    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        if (obj.getString("item") == formattedItem) {
            recipe = obj
            break
        }
    }

    val recipeItems: List<String?> = List(recipe.getJSONArray("recipe").length()) { index ->
        val ingredient = recipe.getJSONArray("recipe").optString(index, null)
        ingredient?.let { convert(it) }
    }

    return buildImage(convert(formattedItem), recipeItems)
}

fun main() {
    val dotenv = dotenv()

    val botToken = dotenv["SLACK_BOT_TOKEN"]
    val appToken = dotenv["SLACK_APP_TOKEN"]
    val signingSecret = dotenv["SLACK_SIGNING_SECRET"]
    val apiKey = dotenv["API_KEY"]

    val itemsFile = File("data/items.json")
    val json = JSONObject(itemsFile.readText())
    val items = json.getJSONArray("items").map { it.toString() }

    val config = AppConfig.builder()
        .singleTeamBotToken(botToken)
        .signingSecret(signingSecret)
        .build()

    val app = App(config)

    app.event(MessageEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(MessageChangedEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(MessageDeletedEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        val event = payload.event
        val replies = ctx.client().conversationsReplies { it
            .channel(event.channel)
            .ts(event.threadTs)
        }
        if (replies.isOk) {
            ctx.ack()
            } else {
                ctx.logger.info("Received a mention in channel ${event.channel} from ${event.user}")
                ctx.logger.info("Received text is: ${event.text}")
                var processedText = event.text.replace("<@U0A5X0FV9V4>", "")
                processedText = processedText.removePrefix(" ")
                processedText = processedText.removeSuffix(" ")
                processedText = processedText.lowercase()
                processedText = processedText.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
                }
                processedText = processedText.replace("Of", "of")
                processedText = processedText.replace("And", "and")
                ctx.logger.info("The text after processing is: $processedText")
                if (processedText in items) {
                    ctx.logger.info("$processedText was found in the items list!")
                    val index = items.indexOf(processedText)
                    val fileName = items[index]
                    val file = getRecipe(fileName)
                    app.client.filesUploadV2 { builder ->
                        builder.channel(event.channel)
                            .file(file)
                            .filename(fileName.replace(" ".toRegex(), "_"))
                            .threadTs(event.ts)
                            .initialComment("The recipe is:")
                    }
                } else {
                    ctx.client().chatPostMessage {
                        it.channel(event.channel)
                            .text("Couldn't find the recipe in my database. Asking AI if there are any typos")
                            .threadTs(event.ts)
                    }
                    var response = sendAIRequest(apiKey, processedText, items)
                    ctx.logger.info("AI responded with $response")
                    response = response?.replace("Of", "of")
                    response = response?.replace("And", "and")
                    if (response in items) {
                        ctx.logger.info("After AI usage, $processedText was turned into $response which was found in the items list!")
                        val index = items.indexOf(response)
                        val fileName = items[index]
                        val file = getRecipe(fileName)
                        app.client.filesUploadV2 { builder ->
                            builder.channel(event.channel)
                                .file(file)
                                .filename(fileName.replace(" ".toRegex(), "_"))
                                .threadTs(event.ts)
                                .initialComment("The recipe is:")
                        }
                    } else {
                        ctx.client().chatPostMessage {
                            it.channel(event.channel)
                                .text("Even after using AI I couldn't find the recipe you're looking for. If it is a truly valid recipe, please search google. I am sorry. I am pinging my maker <@U08D22QNUVD> to notify him.")
                                .threadTs(event.ts)
                        }
                    }
                }
            }
        ctx.ack()
        }
    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.start()
}