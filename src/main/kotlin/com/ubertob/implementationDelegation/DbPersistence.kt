package com.ubertob.implementationDelegation

import com.ubertob.functionLiteralsWithReceiver.User


interface Persistence{
    fun fetchUser(userId: Int): User
    fun fetchAll(): List<User>
}

class UserPersistence(val db: SqlDb): Persistence {

    override fun fetchUser(userId: Int): User {
        return db.exec("select * from users where id = $userId")
    }

    override fun fetchAll(): List<User> {
        return db.exec("select * from users")
    }
}

interface SqlDb {
    fun <T> exec(sql: String): T
}


class UserDb(up: UserPersistence): Persistence by up




fun main() {

}
