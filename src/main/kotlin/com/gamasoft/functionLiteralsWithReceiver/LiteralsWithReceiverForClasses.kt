package com.gamasoft.functionLiteralsWithReceiver

data class User(val id: Int, val name: String){
    fun login() {/* some logic here */}
    fun buyStuff() {/* some logic here */}
    fun pay() {/* some logic here */}
}

fun process(block: (User) -> Boolean) {
    val u = User(123, "Fred")

    if (block(u))
        println("Success!")
    else
        println("Failure!")
}


//use example
fun main(){

    process {
        it.login()
    }

}