package com.ubertob.functionLiteralsWithReceiver


interface ExtService

data class ShopArticle(val id: Int)

data class Client(
    private val extService: ExtService
){
    fun login() {/* some logic here using extService */}
    fun buyStuff(article: ShopArticle) {/* some logic here using extService*/}
    fun pay() {/* some logic here using extService*/}
    fun receiveStuff(): Boolean = true //some logic here
}

class HttpService: ExtService {
    fun process(block: Client.() -> Boolean) {
        val u = Client( this) //

        if (block(u))
            println("Success!")
        else
            println("Failure!")
    }
}



//use example, almost a DSL
fun main(){

    HttpService().process {
        login()
        buyStuff(ShopArticle(42))
        pay()
        receiveStuff()
    }

}