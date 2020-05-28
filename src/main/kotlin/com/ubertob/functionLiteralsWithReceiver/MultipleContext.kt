package com.ubertob.functionLiteralsWithReceiver

class MyHttpServer(val dbConn: MyConnection) {

    private fun getAuthenticated(): User = TODO()

    // Allow block to use a safely opened connection to db
    fun <T> contextConnection(block: MyConnection.() -> T) = block(dbConn)

    // Allow block to use the authenticated user
    fun <T> contextUser(block: (User) -> T) = block(getAuthenticated())

    // Allow block to use both (this and it)
    fun <T> contextConnectionAndUser(block: MyConnection.(User) -> T) = block(dbConn, getAuthenticated())
}

class MyConnection {
    fun getOrders(userId: Int): List<Order> = TODO()
}

data class User(val id: Int, val name: String)



fun main() {

    MyHttpServer(MyConnection()).run {

        contextConnection {
            getOrders(123)
        }

        contextUser {
            println("Hello ${it.name}")
        }

        contextConnectionAndUser {
            println("Hello ${it.name}")
            getOrders(it.id)
        }

    }

}
