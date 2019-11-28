package com.ubertob.unlearnoop

import com.ubertob.outcome.Outcome
import com.ubertob.outcome.OutcomeError
import java.math.BigDecimal
import java.time.LocalDate

data class UserId(val id: String)

data class UserName (val name: String)

data class Amount(val amount: BigDecimal)

data class ItemId(val id: String)

data class ContractId (val id: String)

data class Contract(val id: ContractId, val userId: UserId, val total: Amount)

data class ContractError(override val msg: String): OutcomeError

data class ProcessResult(val dueDate: LocalDate)

