package com.ubertob.functionLiteralsWithReceiver

class ShopService(): ExtService {
    fun process(block: Order.() -> Boolean) {
        val u = Order( this) //

        if (block(u))
            println("Success!")
        else
            println("Failure!")
    }

    fun fetchArticleById(id: Int): ShopArticle = ShopArticle(id)
}

class Order(val extService: ShopService) {

    val client = Client(extService)

    fun Int.toArticle() : ShopArticle = extService.fetchArticleById(this)

    fun Client.quickOrder(article: ShopArticle) : Order {
        this.login()
        this.buyStuff(article)
        this.pay()
        return this@Order
    }

}


//use example, with new methods to Client and Int
fun main(){

    ShopService().process {
        client.quickOrder( 42.toArticle())

        client.receiveStuff()
    }
}