package com.ubertob.implementationDelegation

import com.ubertob.functionLiteralsWithReceiver.User




typealias Row = Map<String, Any> //a db row is expressed as a Map field->value

interface DbConnection {
    //abstraction on the db connection/transaction etc
    fun executeQuery(sql: String): List<Row>
}


interface SqlRunner<out T> { //out is because we can return T or subtypes
    // interface to execute sql statement and return domain objects
    fun builder(row: Row): T
    fun executeQuery(sql: String): List<Row>
}

//declared outside SqlRunner interface to avoid overriding and multiple implementations
fun <T> SqlRunner<T>.fetchSingle(sql: String): T = builder( executeQuery(sql).first() )
fun <T> SqlRunner<T>.fetchMulti(sql: String): List<T> = executeQuery(sql).map { builder(it) }


//real example would be: class JdbcDbConnection(dbConnString: String): DbConnection

class FakeDbConnection(): DbConnection{
    //trivial example but in reality manage connection pool, transactions etc and translate from JDBC
    override fun executeQuery(sql: String): List<Row> {
        return listOf( mapOf("id" to 5, "name" to "Joe") )
    }

}




//now how to use all this for retrieve Users

interface UserPersistence{
    //interface needed by the domain
    fun fetchUser(userId: Int): User
    fun fetchAll(): List<User>
}

class UserPersistenceBySql(dbConn: DbConnection): UserPersistence, SqlRunner<User> by UserSql(dbConn) {
    //translate domain in sql statements but still abstract from db connection,transactions etc.

    override fun fetchUser(userId: Int): User {
        return fetchSingle("select * from users where id = $userId")
    }

    override fun fetchAll(): List<User> {
        return fetchMulti("select * from users")
    }
}

class UserSql( dbConn: DbConnection) : SqlRunner<User>, DbConnection by dbConn {
    // implementation for User
    override fun builder(row: Row): User = User(
        id = row["id"] as Int,
        name = row["name"] as String)

    // note that we don't need to implement executeQuery because is already in DbConnection
}



object UserRepository: UserPersistence by UserPersistenceBySql(FakeDbConnection())

fun main() {

    val joe = UserRepository.fetchUser(5)

    println("fetched user $joe")

}
