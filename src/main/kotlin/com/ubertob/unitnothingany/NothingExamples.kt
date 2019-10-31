package com.ubertob.unitnothingany

import com.ubertob.functionLiteralsWithReceiver.User

fun neverReturn(): Nothing {
    throw Exception("never!")
}

data class UserId(val id: String)

data class HtmlPage(val html: String)

sealed class DbError
data class InvalidStatement(val parseError: String) : DbError()
data class ConnectionClosed(val dbConn: String) : DbError()
object UserNotFound : DbError()

inline fun readUser(id: UserId, onError: (DbError) -> Nothing): User =
    if (id.id == "frank")
        User(5, "Frank")
    else
        onError(UserNotFound)

fun createUserPage(id: UserId): HtmlPage {
    val user = readUser(id) { err ->
        when (err) {
            is InvalidStatement -> throw Exception(err.parseError)
            is ConnectionClosed -> return@createUserPage HtmlPage("Db connection ${err.dbConn} is closed!")
            UserNotFound -> return@createUserPage HtmlPage("No such user!")
        }
    }

    return HtmlPage("User ${user.name}")
}


