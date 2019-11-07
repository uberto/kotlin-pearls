package com.ubertob.objects


interface Name {
    val value: String

}

object Frank: Name {
    override val value: String = "FranK"
    override fun toString(): String = value
}

interface NamedObject {
    val name: Name
}

object MyObject: NamedObject {
    override val name = Frank

    val doubleName = "name is " + name
}



fun main() {
    println(MyObject.name)
    println(MyObject.doubleName)
}

//interface PublicRoutes{
//    val public: PathTemplate<Unit>
//}
//object JwfGatewayRoutes: PublicRoutes {
//    override val public: PathTemplate<Unit> = root + “public”
//    val returnToProduction = public + “return-to-production”
//    val fetchAllArticles = public + “fetch-all-articles”
//}
