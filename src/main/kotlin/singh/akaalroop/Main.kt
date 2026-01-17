package singh.akaalroop

import io.github.cdimascio.dotenv.dotenv
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageChangedEvent
import com.slack.api.model.event.MessageDeletedEvent
import com.slack.api.model.event.MessageEvent
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

fun sendAIRequest(apiKey: String, item: String, items: List<String>): String? {
    val client = OkHttpClient()
    val url = "https://ai.hackclub.com/proxy/v1/chat/completions"
    val mediaType = "application/json; charset=utf-8".toMediaType()

    val jsonBody = JSONObject().apply {
        put("model", "google/gemini-2.5-flash-lite-preview-09-2025")
        val messages = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", """
                    You are a Minecraft crafting assistant. Your task is to match a user-requested item to the provided list: [$items].
                    Matching Rules:
                    	1.	Strict Identity: Distinguish between distinct items (e.g., "Iron Block" vs. "Block of Raw Iron" are not the same).
                        2.	Ambiguity Resolution: If a generic item is requested (e.g., "Bed"), select the White variant (e.g., "White Bed"). 
                        3.	Normalization: Correct typos, synonyms, and regional spellings (e.g., "Armour" to "Armor") to match the list's naming convention. 
                        4.	Expert Knowledge: Use internal Minecraft knowledge to map colloquial terms to the specific list entry.
                    Output Constraint:
                    	•	If a match is found, return ONLY the exact item name from the list.	
                        •	If no match exists, return nothing (an empty string).
                        •	Do not include explanations, formatting, or conversational text.
                    User Request: $item
                """.trimIndent())
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

fun main() {
    val dotenv = dotenv()

    val botToken = dotenv["SLACK_BOT_TOKEN"]
    val appToken = dotenv["SLACK_APP_TOKEN"]
    val signingSecret = dotenv["SLACK_SIGNING_SECRET"]
    val apiKey = dotenv["API_KEY"]

    val items = listOf(
        "Acacia Boat",
        "Acacia Button",
        "Acacia Door",
        "Acacia Fence",
        "Acacia Fence Gate",
        "Acacia Planks",
        "Acacia Pressure Plate",
        "Acacia Sign",
        "Acacia Slab",
        "Acacia Stairs",
        "Acacia Trapdoor",
        "Acacia Wood",
        "Activator Rail",
        "Andesite",
        "Andesite Slab",
        "Andesite Stairs",
        "Andesite Wall",
        "Anvil",
        "Armor Stand",
        "Arrow",
        "Arrow of Fire Resistance",
        "Arrow of Harming",
        "Arrow of Healing",
        "Arrow of Invisibility",
        "Arrow of Leaping",
        "Arrow of Luck",
        "Arrow of Night Vision",
        "Arrow of Poison",
        "Arrow of Regeneration",
        "Arrow of Slow Falling",
        "Arrow of Slowness",
        "Arrow of Splashing",
        "Arrow of Strength",
        "Arrow of Swiftness",
        "Arrow of Water Breathing",
        "Arrow of Weakness",
        "Arrow of the Turtle Master",
        "Barrel",
        "Beacon",
        "Beehive",
        "Beetroot Soup",
        "Birch Boat",
        "Birch Button",
        "Birch Door",
        "Birch Fence",
        "Birch Fence Gate",
        "Birch Planks",
        "Birch Pressure Plate",
        "Birch Sign",
        "Birch Slab",
        "Birch Stairs",
        "Birch Trapdoor",
        "Birch Wood",
        "Black Banner",
        "Black Bed",
        "Black Candle",
        "Black Carpet",
        "Black Concrete Powder",
        "Black Dye",
        "Black Shulker Box",
        "Black Stained Glass",
        "Black Stained Glass Pane",
        "Black Terracotta",
        "Black Wool",
        "Blackstone Slab",
        "Blackstone Stairs",
        "Blackstone Wall",
        "Blast Furnace",
        "Blaze Powder",
        "Block of Amethyst",
        "Block of Coal",
        "Block of Copper",
        "Block of Diamond",
        "Block of Emerald",
        "Block of Gold",
        "Block of Lapis Lazuli",
        "Block of Netherite",
        "Block of Raw Copper",
        "Block of Raw Gold",
        "Block of Raw Iron",
        "Block of Redstone",
        "Blue Banner",
        "Blue Bed",
        "Blue Candle",
        "Blue Carpet",
        "Blue Concrete Powder",
        "Blue Dye",
        "Blue Ice",
        "Blue Shulker Box",
        "Blue Stained Glass",
        "Blue Stained Glass Pane",
        "Blue Terracotta",
        "Blue Wool",
        "Bone Block",
        "Bone Meal",
        "Book",
        "Book and Quill",
        "Bookshelf",
        "Bow",
        "Bowl",
        "Bread",
        "Brewing Stand",
        "Brick Slab",
        "Brick Stairs",
        "Brick Wall",
        "Bricks",
        "Brown Banner",
        "Brown Bed",
        "Brown Candle",
        "Brown Carpet",
        "Brown Concrete Powder",
        "Brown Dye",
        "Brown Shulker Box",
        "Brown Stained Glass",
        "Brown Stained Glass Pane",
        "Brown Terracotta",
        "Brown Wool",
        "Bucket",
        "Bundle",
        "Buried Treasure Map",
        "Calibrated Sculk Sensor",
        "Cake",
        "Campfire",
        "Candle",
        "Carrot on a Stick",
        "Cartography Table",
        "Cauldron",
        "Chain",
        "Chainmail Boots",
        "Chainmail Chestplate",
        "Chainmail Helmet",
        "Chainmail Leggings",
        "Chest",
        "Chiseled Deepslate",
        "Chiseled Nether Bricks",
        "Chiseled Polished Blackstone",
        "Chiseled Quartz Block",
        "Chiseled Red Sandstone",
        "Chiseled Sandstone",
        "Chiseled Stone Bricks",
        "Clay",
        "Coal",
        "Coarse Dirt",
        "Cobbled Deepslate Slabs",
        "Cobbled Deepslate Stairs",
        "Cobbled Deepslate Wall",
        "Cobblestone Slab",
        "Cobblestone Stairs",
        "Cobblestone Wall",
        "Composter",
        "Conduit",
        "Cookie",
        "Copper Bulb",
        "Copper Door",
        "Copper Ingot",
        "Copper Trapdoor",
        "Crafting Table",
        "Crimson Button",
        "Crimson Door",
        "Crimson Fence",
        "Crimson Fence Gate",
        "Crimson Hyphae",
        "Crimson Planks",
        "Crimson Pressure Plate",
        "Crimson Sign",
        "Crimson Slab",
        "Crimson Stairs",
        "Crimson Trapdoor",
        "Crossbow",
        "Cut Copper",
        "Cut Copper Slab",
        "Cut Copper Stairs",
        "Cut Red Sandstone",
        "Cut Red Sandstone Slab",
        "Cut Sandstone",
        "Cut Sandstone Slab",
        "Cyan Banner",
        "Cyan Bed",
        "Cyan Candle",
        "Cyan Carpet",
        "Cyan Concrete Powder",
        "Cyan Dye",
        "Cyan Shulker Box",
        "Cyan Stained Glass",
        "Cyan Stained Glass Pane",
        "Cyan Terracotta",
        "Cyan Wool",
        "Dark Oak Boat",
        "Dark Oak Button",
        "Dark Oak Door",
        "Dark Oak Fence",
        "Dark Oak Fence Gate",
        "Dark Oak Planks",
        "Dark Oak Pressure Plate",
        "Dark Oak Sign",
        "Dark Oak Slab",
        "Dark Oak Stairs",
        "Dark Oak Trapdoor",
        "Dark Oak Wood",
        "Dark Prismarine",
        "Dark Prismarine Slab",
        "Dark Prismarine Stairs",
        "Daylight Detector",
        "Deepslate Brick Slabs",
        "Deepslate Brick Stairs",
        "Deepslate Brick Wall",
        "Deepslate Bricks",
        "Deepslate Tiles",
        "Deepslate Tile Slabs",
        "Deepslate Tile Stairs",
        "Detector Rail",
        "Diamond",
        "Diamond Axe",
        "Diamond Boots",
        "Diamond Chestplate",
        "Diamond Helmet",
        "Diamond Hoe",
        "Diamond Leggings",
        "Diamond Pickaxe",
        "Diamond Shovel",
        "Diamond Sword",
        "Diorite",
        "Diorite Slab",
        "Diorite Stairs",
        "Diorite Wall",
        "Dispenser",
        "Dried Kelp",
        "Dried Kelp Block",
        "Dripstone Block",
        "Dropper",
        "Emerald",
        "Empty Map",
        "Enchanting Table",
        "End Crystal",
        "End Rod",
        "End Stone Brick Slab",
        "End Stone Brick Stairs",
        "End Stone Brick Wall",
        "End Stone Bricks",
        "Ender Chest",
        "Exposed Cut Copper",
        "Exposed Cut Copper Slab",
        "Exposed Cut Copper Stairs",
        "Eye of Ender",
        "Fermented Spider Eye",
        "Fire Charge",
        "Firework Rocket",
        "Firework Star",
        "Fishing Rod",
        "Fletching Table",
        "Flint and Steel",
        "Flower Pot",
        "Furnace",
        "Glass Bottle",
        "Glass Pane",
        "Glistering Melon Slice",
        "Glow Item Frame",
        "Glowstone",
        "Gold Ingot",
        "Gold Nugget",
        "Golden Apple",
        "Golden Axe",
        "Golden Boots",
        "Golden Carrot",
        "Golden Chestplate",
        "Golden Helmet",
        "Golden Hoe",
        "Golden Leggings",
        "Golden Pickaxe",
        "Golden Shovel",
        "Golden Sword",
        "Granite",
        "Granite Slab",
        "Granite Stairs",
        "Granite Wall",
        "Grass Block",
        "Grindstone",
        "Gunpowder",
        "Hanging Roots",
        "Harness",
        "Hay Bale",
        "Heavy Weighted Pressure Plate",
        "Honey Block",
        "Honeycomb Block",
        "Hopper",
        "Iron Axe",
        "Iron Bars",
        "Iron Block",
        "Iron Boots",
        "Iron Chestplate",
        "Iron Door",
        "Iron Helmet",
        "Iron Hoe",
        "Iron Leggings",
        "Iron Pickaxe",
        "Iron Shovel",
        "Iron Sword",
        "Iron Trapdoor",
        "Item Frame",
        "Jack o'Lantern",
        "Jukebox",
        "Ladder",
        "Lantern",
        "Lapis Lazuli",
        "Lava Bucket",
        "Lead",
        "Leather Boots",
        "Leather Chestplate",
        "Leather Helmet",
        "Leather Leggings",
        "Lectern",
        "Lever",
        "Light Blue Banner",
        "Light Blue Bed",
        "Light Blue Candle",
        "Light Blue Carpet",
        "Light Blue Concrete Powder",
        "Light Blue Dye",
        "Light Blue Shulker Box",
        "Light Blue Stained Glass",
        "Light Blue Stained Glass Pane",
        "Light Blue Terracotta",
        "Light Blue Wool",
        "Light Gray Banner",
        "Light Gray Bed",
        "Light Gray Candle",
        "Light Gray Carpet",
        "Light Gray Concrete Powder",
        "Light Gray Dye",
        "Light Gray Shulker Box",
        "Light Gray Stained Glass",
        "Light Gray Stained Glass Pane",
        "Light Gray Terracotta",
        "Light Gray Wool",
        "Lightning Rod",
        "Lime Banner",
        "Lime Bed",
        "Lime Candle",
        "Lime Carpet",
        "Lime Concrete Powder",
        "Lime Dye",
        "Lime Shulker Box",
        "Lime Stained Glass",
        "Lime Stained Glass Pane",
        "Lime Terracotta",
        "Lime Wool",
        "Lodestone",
        "Loom",
        "Mace",
        "Magenta Banner",
        "Magenta Bed",
        "Magenta Candle",
        "Magenta Carpet",
        "Magenta Concrete Powder",
        "Magenta Dye",
        "Magenta Shulker Box",
        "Magenta Stained Glass",
        "Magenta Stained Glass Pane",
        "Magenta Terracotta",
        "Magenta Wool",
        "Melon",
        "Minecart",
        "Moss Block",
        "Moss Carpet",
        "Mossy Cobblestone",
        "Mossy Cobblestone Slab",
        "Mossy Cobblestone Stairs",
        "Mossy Cobblestone Wall",
        "Mossy Stone Bricks",
        "Mossy Stone Brick Slab",
        "Mossy Stone Brick Stairs",
        "Mossy Stone Brick Wall",
        "Mud Bricks",
        "Mud Brick Slab",
        "Mud Brick Stairs",
        "Nether Brick Fence",
        "Nether Brick Slab",
        "Nether Brick Stairs",
        "Nether Brick Wall",
        "Nether Bricks",
        "Netherite Axe",
        "Netherite Boots",
        "Netherite Chestplate",
        "Netherite Helmet",
        "Netherite Hoe",
        "Netherite Leggings",
        "Netherite Pickaxe",
        "Netherite Shovel",
        "Netherite Sword",
        "Note Block",
        "Oak Boat",
        "Oak Button",
        "Oak Door",
        "Oak Fence",
        "Oak Fence Gate",
        "Oak Planks",
        "Oak Pressure Plate",
        "Oak Sign",
        "Oak Slab",
        "Oak Stairs",
        "Oak Trapdoor",
        "Oak Wood",
        "Observer",
        "Orange Banner",
        "Orange Bed",
        "Orange Candle",
        "Orange Carpet",
        "Orange Concrete Powder",
        "Orange Dye",
        "Orange Shulker Box",
        "Orange Stained Glass",
        "Orange Stained Glass Pane",
        "Orange Terracotta",
        "Orange Wool",
        "Painting",
        "Paper",
        "Pink Banner",
        "Pink Bed",
        "Pink Candle",
        "Pink Carpet",
        "Pink Concrete Powder",
        "Pink Dye",
        "Pink Shulker Box",
        "Pink Stained Glass",
        "Pink Stained Glass Pane",
        "Pink Terracotta",
        "Pink Wool",
        "Piston",
        "Polished Andesite",
        "Polished Andesite Slab",
        "Polished Andesite Stairs",
        "Polished Basalt",
        "Polished Blackstone",
        "Polished Blackstone Slab",
        "Polished Blackstone Stairs",
        "Polished Blackstone Wall",
        "Polished Deepslate",
        "Polished Deepslate Slabs",
        "Polished Deepslate Stairs",
        "Polished Deepslate Wall",
        "Polished Diorite",
        "Polished Diorite Slab",
        "Polished Diorite Stairs",
        "Polished Granite",
        "Polished Granite Slab",
        "Polished Granite Stairs",
        "Powered Rail",
        "Pumpkin Pie",
        "Purple Banner",
        "Purple Bed",
        "Purple Candle",
        "Purple Carpet",
        "Purple Concrete Powder",
        "Purple Dye",
        "Purple Shulker Box",
        "Purple Stained Glass",
        "Purple Stained Glass Pane",
        "Purple Terracotta",
        "Purple Wool",
        "Quartz Block",
        "Quartz Pillar",
        "Quartz Slab",
        "Quartz Stairs",
        "Rail",
        "Recovery Compass",
        "Red Banner",
        "Red Bed",
        "Red Candle",
        "Red Carpet",
        "Red Concrete Powder",
        "Red Dye",
        "Red Shulker Box",
        "Red Stained Glass",
        "Red Stained Glass Pane",
        "Red Terracotta",
        "Red Wool",
        "Redstone Comparator",
        "Redstone Lamp",
        "Redstone Repeater",
        "Respawn Anchor",
        "Sandstone",
        "Sandstone Slab",
        "Sandstone Stairs",
        "Sandstone Wall",
        "Scaffolding",
        "Shears",
        "Shield",
        "Shulker Box",
        "Smithing Table",
        "Smoker",
        "Smooth Basalt",
        "Smooth Quartz",
        "Smooth Quartz Slab",
        "Smooth Quartz Stairs",
        "Smooth Sandstone",
        "Smooth Sandstone Slab",
        "Smooth Sandstone Stairs",
        "Smooth Stone",
        "Smooth Stone Slab",
        "Snow Block",
        "Soul Campfire",
        "Soul Lantern",
        "Soul Torch",
        "Spruce Boat",
        "Spruce Button",
        "Spruce Door",
        "Spruce Fence",
        "Spruce Fence Gate",
        "Spruce Planks",
        "Spruce Pressure Plate",
        "Spruce Sign",
        "Spruce Slab",
        "Spruce Stairs",
        "Spruce Trapdoor",
        "Spruce Wood",
        "Spyglass",
        "Sticks",
        "Sticky Piston",
        "Stone Axe",
        "Stone Brick Slab",
        "Stone Brick Stairs",
        "Stone Brick Wall",
        "Stone Bricks",
        "Stone Button",
        "Stone Hoe",
        "Stone Pickaxe",
        "Stone Pressure Plate",
        "Stone Shovel",
        "Stone Stairs",
        "Stone Sword",
        "Stonecutter",
        "String",
        "Sugar",
        "Target",
        "Tinted Glass",
        "TNT",
        "Torch",
        "Trapped Chest",
        "Tripwire Hook",
        "Waxed Cut Copper",
        "Waxed Cut Copper Slab",
        "Waxed Cut Copper Stairs",
        "Water Bucket",
        "Weighted Pressure Plate",
        "Wheat",
        "White Banner",
        "White Bed",
        "White Candle",
        "White Carpet",
        "White Concrete Powder",
        "White Dye",
        "White Shulker Box",
        "White Stained Glass",
        "White Stained Glass Pane",
        "White Terracotta",
        "White Wool",
        "Wolf Armor",
        "Wooden Axe",
        "Wooden Hoe",
        "Wooden Pickaxe",
        "Wooden Shovel",
        "Wooden Sword",
        "Woodland Explorer Map",
        "Yellow Banner",
        "Yellow Bed",
        "Yellow Candle",
        "Yellow Carpet",
        "Yellow Concrete Powder",
        "Yellow Dye",
        "Yellow Shulker Box",
        "Yellow Stained Glass",
        "Yellow Stained Glass Pane",
        "Yellow Terracotta",
        "Yellow Wool"
    )

    val recipeFileName = "Available_Recipes"
    val recipeFile = File.createTempFile(recipeFileName, ".txt")
    var i = 1
    for (item in items) {
        recipeFile.appendText("${i}. $item \n")
        i += 1
    }

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
                    val fileName = items[index].replace(" ".toRegex(), "_")
                    val file = File("recipe_images/$fileName.png")
                    app.client.filesUploadV2 { builder ->
                        builder.channel(event.channel)
                            .file(file)
                            .filename(fileName)
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
                        val fileName = items[index].replace(" ".toRegex(), "_")
                        val file = File("recipe_images/$fileName.png")
                        app.client.filesUploadV2 { builder ->
                            builder.channel(event.channel)
                                .file(file)
                                .filename(fileName)
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

    app.command("/list-recipes") { payload, ctx ->
        ctx.logger.info("Item list requested")
        app.client.filesUploadV2 { builder ->
            builder.channel(ctx.channelId)
                .file(recipeFile)
                .filename(recipeFileName)
                .initialComment("All available recipes are in the list provided!")
        }
        ctx.ack()
    }
    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.start()
}