package com.ubertob.companionObjects

import java.time.LocalDate


class RecordTransactionCommandBuilder {
    var accountId: AccountTrackerId? = null
    var transactionAmount: Money = Money(500, "EUR")
    var counterpart = "The counterpart"
    var happenedOn = LocalDate.now().minusDays(2)
    var trackedOn = LocalDate.now()
    var notes = "default"
    fun withAccountId(accountId: AccountTrackerId?): RecordTransactionCommandBuilder {
        this.accountId = accountId
        return this
    }

    fun withTransactionAmount(amount: Money): RecordTransactionCommandBuilder {
        transactionAmount = amount
        return this
    }

    fun withHappenedOn(happenedOn: LocalDate): RecordTransactionCommandBuilder {
        this.happenedOn = happenedOn
        return this
    }

    fun build(): RecordTransaction {
        return RecordTransaction(accountId, transactionAmount, counterpart, happenedOn, trackedOn, notes)
    }
}

data class AccountTrackerId(val value: String)
data class Money(val amount: Int, val curr: String)
data class RecordTransaction(
    val accountId: AccountTrackerId?,
    val transactionAmount: Money,
    val counterpart: String,
    val happenedOn: LocalDate,
    val trackedOn: LocalDate,
    val notes: String
)


fun aTransaction(transactionAmount: Money) =
    RecordTransaction(
        accountId = null,
        transactionAmount = transactionAmount,
        counterpart = "counterpart",
        happenedOn = LocalDate.of(2021, 1, 1),
        trackedOn = LocalDate.of(2021, 1, 2),
        notes = "my notes"
    )



val aTx = aTransaction(Money(123,"USD"))
val anotherTx = aTx.copy(counterpart = "someonelse", notes = "")


infix fun String.received(amount: Money): Pair<Money, String> = amount to this

infix fun Pair<Money, String>.from(accountId: String): RecordTransaction =
    RecordTransaction(
        accountId = AccountTrackerId( accountId),
        transactionAmount = this.first,
        counterpart = this.second,
        happenedOn = LocalDate.now(),
        trackedOn = LocalDate.now(),
        notes = ""
    )

val Int.EURO: Money get() = Money(this, "EUR")


val tx = "Alberto" received 50.EURO from "ABC"

