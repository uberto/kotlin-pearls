package com.ubertob.implementationDelegation

import com.ubertob.functionLiteralsWithReceiver.User


interface Persistence{
    fun fetchUser(userId: Int): User
    fun fetchAll(): List<User>
}

class UserPersistence(val db: SqlDb<User>): Persistence {

    override fun fetchUser(userId: Int): User {
        return db.fetchSingle("select * from users where id = $userId")
    }

    override fun fetchAll(): List<User> {
        return db.fetchMulti("select * from users")
    }
}


typealias Row = Map<String, Any>

interface SqlDb<out T> {
    fun builder(row: Row): T
    fun execSql(sql: String): List<Row>
}

fun <T> SqlDb<T>.fetchSingle(sql: String): T = builder( execSql(sql).first() )
fun <T> SqlDb<T>.fetchMulti(sql: String): List<T> = execSql(sql).map { builder(it) }



object UserDb: SqlDb<User>, Persistence by UserPersistence(UserDb) {
    override fun builder(row: Row): User = User(
        id = row["id"] as Int,
        name = row["name"] as String)

    override fun execSql(sql: String): List<Row> = //connection to db etc...
        listOf( mapOf("id" to 5, "name" to "Joe") )
}


fun main() {

    val joe = UserDb.fetchUser(5)

}
