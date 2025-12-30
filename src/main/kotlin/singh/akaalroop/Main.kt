package singh.akaalroop

import io.github.cdimascio.dotenv.dotenv
import com.slack.api.bolt.App
import com.slack.api.bolt.AppConfig
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageEvent


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



    app.event(MessageEvent::class.java) { payload, ctx ->
        ctx.ack()
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        val event = payload.event

        ctx.ack()
    }


    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.start()
}