package singh.akaalroop

import io.github.cdimascio.dotenv.dotenv
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.response.Response
import com.slack.api.bolt.socket_mode.SocketModeApp
import kotlin.text.get

fun main() {
    val dotenv = dotenv()

    val botToken = dotenv["SLACK_BOT_TOKEN"]
    val appToken = dotenv["SLACK_APP_TOKEN"]
    val signingSecret = dotenv["SLACK_SIGNING_SECRET"]

    val config = AppConfig.builder()
        .singleTeamBotToken(botToken)
        .signingSecret(signingSecret)
        .build()

    val app = App(config)

    app.message("Hello") { payload, ctx ->
        val event = payload.event

        ctx.say("I exist!!")

        ctx.ack()
    }

    app.event(MessageEvent.class, (payload, ctx) -> {
      return ctx.ack();
   });


    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.start()
}