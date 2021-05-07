package com.ubertob.functors

import java.sql.ResultSet


//for real db look at https://github.com/aaberg/sql2o
//

typealias DbReader<T> = (ResultSet) -> T?

class DBConn(connStr: String) {
    fun <T: Any> execStmt(sql: String, reader: DbReader<T>): Sequence<T> = TODO()
}


fun articleReader(resultSet: ResultSet): Article = TODO()

@JvmInline
value class Email(val raw: String)
data class Author(val name: String, val email: Email)
data class Article(val title: String, val author: Author)


//0. let's read from db
fun readArticleByTitle_0(title: String, conn: DBConn): Article =
    conn.execStmt("select * from article where title = '$title'", ::articleReader).single()


//
////1. let's curry the Connection
//fun readArticleByTitle_1(title: String): DBConn.() -> Article = {
//    execStmt("select * from article where title = '$title'", ::articleReader).single()
//}
//
//
////2. let's consider the general case
//typealias ReadFromDb<T> = (DBConn) -> Sequence<T>
//fun readArticleByTitle_2(title: String): ReadFromDb<Article>  = { conn ->
//    conn.execStmt("select * from article where title = '$title'", ::articleReader)
//}
//
////3. let's put ReadFromDb in a class for composition
//data class Database<T>(val sqlStmt: String, val dbReader: DbReader<T>): (DBConn) -> Sequence<T> {
//    override fun invoke(c: DBConn): Sequence<T> = c.execStmt(sqlStmt, dbReader)
//}
//
//fun readArticleByTitle_3(title: String): ReadFromDb<Article>  =
//    Database("select * from article where title = '$title'", ::articleReader)

//x. we can put together sql+dbReader

//x. query for authors and article by authors

//x. we can also write on db

//x. let's see how use the data (fmap)

//x. let's see how to compose reads (bind)

//x. how to use transactions

//x. impossible to lose connections or transactions

//not in scope: create the


class ExampleReified {
    inline fun <reified T> findStuff(): T = when(T::class) {
         Author::class -> Author("John", Email("john@gmail.com")) as T
         Email::class ->  Email("john@gmail.com") as T
        else -> TODO()
    }
}