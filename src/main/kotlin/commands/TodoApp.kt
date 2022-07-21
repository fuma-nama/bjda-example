package commands

import bjda.plugins.ui.hook.ButtonClick.Companion.onClick
import bjda.plugins.ui.hook.MenuSelect.Companion.onSelect
import bjda.plugins.ui.modal.Form.Companion.form
import bjda.ui.component.Row
import bjda.ui.component.RowLayout
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.component.action.Button
import bjda.ui.component.action.Menu
import bjda.ui.component.action.TextField
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.minus
import bjda.ui.core.rangeTo
import bjda.ui.types.Children
import database.saveTodos
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import todo.todoUIs

class TodoApp(initialTodos: ArrayList<String>?) : Component<TodoApp.Props>(Props()) {
    class Props : IProps() {
        lateinit var owner: User
    }

    private val state = useState(
        State(
            todos = initialTodos?: ArrayList()
        )
    )

    data class State(
        val todos: ArrayList<String>,
        var selected: Int? = null
    )

    private val onAddItem by onClick { event ->
        event.replyModal(addTodoForm).queue()
    }

    private val onEditItem by onClick { event ->
        event.replyModal(editTodoForm).queue()
    }

    private val onDeleteItem by onClick { event ->
        state.update(event) {
            todos.removeAt(selected!!)

            selected = null
        }
    }

    private val onClose by onClick { event ->
        val owner = props.owner

        event.deferEdit().queue()
        todoUIs.remove(owner)

        val (todos) = state.get()

        saveTodos(owner.idLong, todos.toTypedArray())

        ui.destroy()
    }

    private val onSelectItem by onSelect { event ->
        state.update(event) {
            selected = event.selectedOptions[0].value.toInt()
        }
    }

    override fun onRender(): Children {
        val (todos, selected) = state.get()

        return {
            + Text()..{
                content = "**TODO List**"
                type = TextType.LINE
            }

            + on (todos.isEmpty()) {
                Text()..{
                    content = "No Todos"
                    type = TextType.CODE_BLOCK
                }
            }

            + todos.mapIndexed {i, todo ->
                Text()..{
                    this.key = i
                    this.content = todo
                    type = TextType.CODE_BLOCK
                }
            }

            + RowLayout() -{
                addIf (todos.isNotEmpty()) {
                    Menu(onSelectItem) {
                        placeholder = "Select a Item"

                        options = todos.mapIndexed {i, todo ->
                            SelectOption.of(todo, i.toString()).withDefault(i == selected)
                        }
                    }
                }

                + Button(onAddItem) {
                    label = "Add"
                }


                if (selected != null) {

                    + Button(onEditItem) {
                        label = "Edit"
                        style = ButtonStyle.PRIMARY
                    }
                    + Button(onDeleteItem) {
                        label = "Delete"
                        style = ButtonStyle.DANGER
                    }
                }
            }

            + Row()-{
                + Button(onClose) {
                    label = "Close Todo"
                    style = ButtonStyle.DANGER
                }
            }
        }
    }

    private val addTodoForm by form {
        title = "Add Todo"

        onSubmit = {event ->
            state.update(event) {
                todos += event.value("todo")
            }
        }

        render = {
            + row {
                + TextField("todo") {
                    label = "TODO"
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }

    private val editTodoForm by form {
        title = "Modify Todo"

        onSubmit = {event ->
            val value = event.getValue("todo")!!.asString

            state.update(event) {
                todos[selected!!] = value
            }
        }

        render = {
            val (todos, selected) = state.get()

            + row {
                + TextField("todo") {
                    label = "New Content"
                    value = todos[selected!!]
                    style = TextInputStyle.PARAGRAPH
                }
            }
        }
    }
}