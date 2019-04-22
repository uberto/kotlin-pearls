package com.ubertob.implementationDelegation

import com.ubertob.functionLiteralsWithReceiver.User


interface UserPersistence{
    fun fetchUser(userId: Int): User
    fun fetchAll(): List<User>
}

class UserPersistenceBySql(val db: SqlDb<User>): UserPersistence {

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

//declared outside interface to avoid overriding
fun <T> SqlDb<T>.fetchSingle(sql: String): T = builder( execSql(sql).first() )
fun <T> SqlDb<T>.fetchMulti(sql: String): List<T> = execSql(sql).map { builder(it) }



data class UserSql(val dbConn: String) : SqlDb<User> {
    override fun builder(row: Row): User = User(
        id = row["id"] as Int,
        name = row["name"] as String)

    override fun execSql(sql: String): List<Row> =
        //connection to db, connection pool, run sql and return resultset as map
        listOf( mapOf("id" to 5, "name" to "Joe") )

}



object UserDb: UserPersistence by UserPersistenceBySql(UserSql("db.company.com"))

fun main() {

    val joe = UserDb.fetchUser(5)

}
