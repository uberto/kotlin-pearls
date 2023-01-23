package com.ubertob.multipleReceivers

import java.io.File
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

//not sure how to use them
//typealias FileR = (File) -> Unit
//typealias TimeR = () -> Instant


//context(File) fun String.print() = this@File.writeText(this@String)
//
//interface ConsoleR {
//    fun writeOnConsole(text: String)
//}
//
//interface TimeR {
//    fun now(): Instant
//}
//
//context(TimeR) fun needsTheDate(): LocalDate = LocalDate.ofInstant(now(), ZoneId.systemDefault())
//
//context (ConsoleR) fun writeAThing(date: LocalDate) {
//    writeOnConsole(date.toString())
//}
//
////add an indirection level
//class GlobalContextR : TimeR, ConsoleR {
//    override fun writeOnConsole(text: String) {
//        println(text)
//    }
//
//    override fun now(): Instant =
//        Instant.now()
//
//}
//
//context(TimeR, ConsoleR) fun topIshLevelFun() {
//    val d = needsTheDate()
//    writeAThing(d)
//}
//
//fun <T> T.topIshLevelFun2() where T : ConsoleR, T : TimeR {
//    val d = needsTheDate()
//    writeAThing(d)
//}
//
//
//interface Functor<T> {
//    fun <U> fmap(fn: (T) -> U): Functor<U>
//}
//
//context(FUNCTR) operator fun <T, FUNCTR : Functor<T>, U> T.invoke(fn: (T) -> U): Functor<U> = TODO()
////add contextual opeerators to types you cannot change
//
//data class RouteSegment(val element: String)
//
//data class Routes(val segments: Collection<RouteSegment>) {
//    fun toURL(): URL = URL(segments.map(RouteSegment::element).joinToString("/"))
//}
//
//context(Routes) operator fun String.div(other: String): Routes = TODO()
//context(Routes) operator fun String.div(other: Routes): Routes = TODO()
//context(Routes) operator fun Routes.div(other: String): Routes = TODO()

typealias User = String
typealias Item = String
typealias TransId = Int


interface PurchaseHub {
    context (AuthAdapter, DbAdapter)
    fun purchaseItem(u: User, i: Item): Result<TransId>

    context (DbAdapter)
    fun purchased(u: User): Result<List<Item>>
}

interface DbAdapter {}
interface AuthAdapter {}

object StubDb : DbAdapter

object StubAuth : AuthAdapter

object StubAll : DbAdapter, AuthAdapter
//
//class PurchaseHubDeps(val purchasesDb: DbAdapter, val userAuth: AuthAdapter) : PurchaseHub {
//    override fun purchaseItem(u: User, i: Item): Result<TransId> {
//        println("Domain logic here")
//        return Result.success(42)
//    }
//
//    override fun purchased(u: User): Result<List<Item>> {
//        println("Domain logic here")
//
//        return Result.success(emptyList())
//    }
//}

class PurchaseHubContext() : PurchaseHub {

    context (DbAdapter, AuthAdapter)
    override fun purchaseItem(u: User, i: Item): Result<TransId> {
        println("Domain logic here")
        return Result.success(43)
    }

    context (DbAdapter)
    override fun purchased(u: User): Result<List<Item>> {
        println("Domain logic here")
        return Result.success(emptyList())
    }
}

context (DbAdapter)
class PurchaseHubClassContext() : PurchaseHub {

    context (AuthAdapter)
    override fun purchaseItem(u: User, i: Item): Result<TransId> {
        println("Domain logic here")

        return Result.success(44)
    }

    override fun purchased(u: User): Result<List<Item>> {
        println("Domain logic here")
        return Result.success(emptyList())
    }
}

context (DbAdapter, AuthAdapter)
fun messingAround(ph: PurchaseHub) {
    val r = ph.purchaseItem("bob", "stuff")

    println(r)
}

fun main() {
//    with(GlobalContextR()) {
//        topIshLevelFun()
//
////        topIshLevelFun2() Internal Compiler error
//    }
//
//    val f: (TimeR, ConsoleR) -> Unit = ::topIshLevelFun


//    val f2 = ConsoleR::topIshLevelFun2

//
    val h1 = PurchaseHubContext()

    with(StubAll) {
        h1.purchaseItem("bob", "tv")

        h1.purchased("bob")
    }

    val h2 = with(StubDb) {
        PurchaseHubClassContext()
    }

    with(StubAuth) {
        h2.purchaseItem("bob", "tv")
    }

    h2.purchased("bob")
//
//    messingAround(h2)


}