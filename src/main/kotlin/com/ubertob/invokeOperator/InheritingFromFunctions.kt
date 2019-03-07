package com.ubertob.invokeOperator


class ReceiptText(val template: String): (Int) -> String {
    override fun invoke(amount: Int): String =
        template.replace("%", amount.toString())

}