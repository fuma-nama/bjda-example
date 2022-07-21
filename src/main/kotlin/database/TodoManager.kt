package database

import ctx
import org.jooq.Record
import org.jooq.ResultQuery
import models.tables.references.TODO as todo

fun saveTodos(user: Long, todos: Array<String?>) {
    ctx.insertInto(todo, todo.USER, todo.CONTENT)
        .values(user, todos)
        .onDuplicateKeyUpdate()
        .set(todo.CONTENT, todos)
        .executeAsync()
}

fun getTodos(user: Long, accept: (ArrayList<String>?) -> Unit) {
    ctx.selectFrom(todo)
        .where(todo.USER.eq(user))
        .fetchOneAsync(accept) {
            it.content?.let {todos ->
                arrayListOf(*todos) as ArrayList<String>
            }
        }
}

fun<R : Record?, T> ResultQuery<R>.fetchAsync(accept: (T) -> Unit, mapper: (org.jooq.Result<R>) -> T) {
    this.fetchAsync().thenAccept {
        accept(mapper(it))
    }
}

fun<R : Record?, T> ResultQuery<R>.fetchOneAsync(accept: (T) -> Unit, mapper: (R) -> T) {
    this.fetchAsync().thenAccept {
        accept(mapper(it[0]))
    }
}