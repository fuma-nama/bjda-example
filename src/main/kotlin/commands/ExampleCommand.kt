package commands

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.modal.Form.Companion.form
import bjda.ui.component.Content
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField
import bjda.ui.core.*
import bjda.ui.types.Children
import net.dv8tion.jda.api.interactions.components.Modal
import java.awt.Color

class ExampleCommand : SuperCommand("example", "Run example form command") {
    override fun run() {
        UI(RegisterUI()).reply(event, true)
    }
}

fun onRegister(lang: String, name: String) {
    println("register: $lang $name")
}

class RegisterUI : Component<IProps>(IProps()) {
    private val modal: Modal by form {
        title = "Register";

        onSubmit = {event ->
            val lang = event.value("language").lowercase()
            val name = event.value("name")

            //check inputs here
            when (lang) {
                "en", "zh" -> {
                    if (name.any {c -> Character.UnicodeScript.of(c.code) == Character.UnicodeScript.HAN}) {
                        error.update(event, "Name cannot contains chinese")
                    } else {
                        onRegister(lang, name)

                        //destroy the message after register
                        ui.edit(event) {
                            ui.destroy()
                        }
                    }
                }
                else -> {
                    error.update(event, "Invalid Language format")
                }
            }
        }

        render = {
            + row {
                + TextField("language") {
                    label = "Language (en/zh)"
                }
            }
            + row {
                + TextField("name") {
                    label = "Game Name"
                }
            }
        }
    }

    private val onRegister = ButtonClick {
        it.replyModal(modal).queue()
    }

    private val error = useState<String?>(null)

    override fun onRender(): Children {
        return {
            + on(error.get() != null) {
                Embed()..{
                    title = "Error"
                    description = error.get()
                    color = Color.RED
                }
            }

            + Content("Click the button below to register")

            + Row()-{
                + Button(id = use(onRegister)) {
                    label = "Register"
                }
            }
        }
    }
}

