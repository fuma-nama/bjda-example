package commands

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.*
import bjda.ui.utils.UIStore
import commands.context.LanguageContext
import commands.context.Languages
import database.getTodos
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

val TodoCommands = SuperCommandGroup.create("todo", "Todo Commands",
    CreateTodo()
)

val todoStore = UIStore<User>()

private class CreateTodo: SuperCommand(name = "create", description = "Create a Todo List") {
    override fun run() {
        val language = when(event.userLocale) {
            DiscordLocale.CHINESE_TAIWAN, DiscordLocale.CHINESE_CHINA -> Languages.Chinese
            else -> Languages.English
        }

        event.replyAsync(todoStore) { update ->
            getTodos(event.user.idLong) {
                val ui = UI(
                    TodoApp(it, language)..{
                        owner = event.user
                    }
                )

                update(ui)
            }
        }
    }
}

fun<T: IReplyCallback> T.replyAsync(store: UIStore<User>, execute: (update: (UI) -> Unit) -> Unit) {
    val ui = store[user]

    if (ui == null) {

        deferReply().queue {
            execute {ui ->
                store[user] = ui

                ui.edit(hook) {
                    ui.listen(it)
                }
            }
        }
    } else {
        ui.reply(this) {
            ui.listen(it)
        }
    }
}