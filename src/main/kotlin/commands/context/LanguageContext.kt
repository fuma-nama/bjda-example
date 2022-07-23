package commands.context

import bjda.ui.core.hooks.Context

val en = mapOf(
    "todo" to "TODO",
    "title" to "Todo Panel",
    "add" to "Add Todo",
    "edit" to "Modify Todo",
    "delete" to "Delete Todo",
    "placeholder" to "No Todo yet...",
    "menu.placeholder" to "Select a Todo",
    "form.new_content" to "New Content",
    "close" to "Close Panel"
)

val ch = mapOf(
    "todo" to "待辦事項",
    "title" to "待辦事項面板",
    "add" to "添加待辦事項",
    "edit" to "編輯待辦事項",
    "delete" to "刪除待辦事項",
    "placeholder" to "還沒有待辦事項",
    "menu.placeholder" to "選擇一個待辦事項",
    "form.new_content" to "新內容",
    "close" to "關閉面板"
)

enum class Languages(private val map: Map<String, String>) {
    Chinese(ch), English(en);

    operator fun get(name: String): String {
        return map[name]!!
    }
}

val LanguageContext = Context.create<Languages>()