package com.ubertob.unlearnoop

interface ContractDatabase{
    fun readByUser(userId: UserId): List<Contract>
}

fun amountByUser(userId: UserId, db: ContractDatabase): Amount =
    db.readByUser(userId)
        .map { it.total }
        .reduce{a, b -> a + b}



fun amountByUser(userId: UserId,
                 reader: (UserId)-> List<Contract>): Amount =
    reader(userId)
        .map { it.total }
        .reduce{a, b -> a + b}





operator fun Amount.plus(other: Amount): Amount = Amount(this.amount + other.amount)
