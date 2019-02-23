package com.gamasoft.functionLiteralsWithReceiver

data class Client(
    private val extService: HttpService
){
    fun login() {/* some logic here using extService */}
    fun buyStuff() {/* some logic here using extService*/}
    fun pay() {/* some logic here using extService*/}
    fun receiveStuff(): Boolean = true //some logic here
}

class HttpService {
    fun process(block: Client.() -> Boolean) {
        val u = Client( this) //

        if (block(u))
            println("Success!")
        else
            println("Failure!")
    }
}


//use example
fun main(){

    HttpService().process {
        login()
        buyStuff()
        pay()
        receiveStuff()
    }

}