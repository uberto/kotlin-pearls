package com.ubertob.objects

interface NamedObject {
    val name: String
}

object MyObject: NamedObject {
    override val name = "Frank"

    val doubleName = "$name and $name"
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
