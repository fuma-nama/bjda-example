package todo

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.*
import bjda.ui.utils.UIStore
import commands.TodoApp
import net.dv8tion.jda.api.entities.User

val TodoCommands = SuperCommandGroup.create("todo", "Todo Commands",
    CreateTodo()
)

val users = UIStore<User>()

private class CreateTodo: SuperCommand(name = "create", description = "Create a Todo List") {
    override fun run() {
        val ui = users.getUI(event.user) {
            UI( TodoApp() )
        }

        ui.reply(event) {
            ui.listen(it)
        }
    }
}