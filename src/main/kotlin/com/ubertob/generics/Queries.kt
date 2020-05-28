package com.ubertob.generics


fun <T> queryHandler(q: Query<T>, block: Iterable<T>.() -> Unit) {
    block( q.fetchAll())
}


inline fun <reified T> queryHandler2(q: Query<T>): Iterable<T> =
    when (T::class){
        Int::class -> q.fetchAll() //  listOf(1,2,3) as Iterable<T>
        String::class -> q.fetchAll() //listOf("a", "b", "c") as Iterable<T>
        else -> TODO()
    }

interface Query<T> {
    fun fetchAll(): Iterable<T>
}

data class MyQueryI(val id: Int): Query<Int>{
    override fun fetchAll() =  listOf(1,2,3)
}

data class MyQueryS(val id: String): Query<String>{
    override fun fetchAll() =  listOf("a", "b", "c")
}


fun <T: Any> createNull(): T? {
//    println(T::class)
    return null
}


fun main(){

    println(createNull<String>())

    val x: Int? = createNull()
    println(createNull<Int>())


}