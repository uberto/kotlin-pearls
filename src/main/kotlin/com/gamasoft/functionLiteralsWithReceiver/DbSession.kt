package com.gamasoft.functionLiteralsWithReceiver


data class User(val id: Int, val name: String)

interface UserRepository{
    fun getUser(id: Int): User

    fun getAllUsers(): List<User>

    fun saveUser(user: User)

}

class DbSession(val dbConn: String, val block: (UserRepository) -> Unit){

    fun execute(){
        val userRepo = UserDb(dbConn)
        userRepo.open()

        block(userRepo)

        userRepo.close()
    }
}

class UserDb(dbConn: String) : UserRepository {
    override fun getUser(id: Int): User {TODO()}

    override fun getAllUsers(): List<User> {TODO()}

    override fun saveUser(user: User) {}

    fun open(){}

    fun close(){}

}
