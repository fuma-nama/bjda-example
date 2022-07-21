package todo

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.*
import bjda.ui.utils.UIStore
import commands.TodoApp
import database.getTodos
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

val TodoCommands = SuperCommandGroup.create("todo", "Todo Commands",
    CreateTodo()
)

val todoUIs = UIStore<User>()

private class CreateTodo: SuperCommand(name = "create", description = "Create a Todo List") {
    override fun run() {
        event.replyAsync(todoUIs) { update ->
            getTodos(event.user.idLong) {
                val ui = UI( TodoApp(it)..{
                    owner = event.user
                })

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

private fun UI.edit(hook: InteractionHook, success: (t: Message) -> Unit) {
    hook.editOriginal(this.build()).queue(success)
}
