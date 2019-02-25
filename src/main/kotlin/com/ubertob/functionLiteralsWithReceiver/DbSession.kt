package com.ubertob.functionLiteralsWithReceiver

import java.io.Closeable


data class DbUser(val id: Int, val name: String)

interface UserRepository: Closeable {
    fun getUser(id: Int): DbUser

    fun getAllUsers(): List<DbUser>

    fun saveUser(user: DbUser)

}

class DbSession(val block: (UserRepository) -> Unit){

    fun execute(dbConn: String){
        val userRepo = UserDb(dbConn)
        userRepo.open().use {
            block(userRepo)
        }

    }
}

class UserDb(private val dbConn: String) : UserRepository {
    override fun getUser(id: Int): DbUser {TODO()}

    override fun getAllUsers(): List<DbUser> {TODO()}

    override fun saveUser(user: DbUser) {}

    fun open(): UserRepository {return this}

    override fun close(){TODO()}

}
