import bjda.BJDA
import bjda.plugins.supercommand.SuperCommandModule
import bjda.plugins.ui.UIEventModule
import commands.ExampleCommand
import net.dv8tion.jda.api.JDABuilder
import todo.TodoCommands
import token.TOKEN

fun main() {
    val jda = JDABuilder.createDefault(TOKEN)
        .build()
        .awaitReady()

    BJDA.create(jda)
        .install(
            SuperCommandModule(
                ExampleCommand(),
                TodoCommands
            ),
            UIEventModule(),
        )
}