package com.ubertob.invokeOperator

typealias FIntToString = (Int) -> String


//a class that inherit from a function type
class ReceiptText(val template: String) : (Int) -> String {
    override fun invoke(amount: Int): String =
        template.replace("%", amount.toString())

}


//a function that return another function
fun receiptText(template: String): (Int) -> String = { amount ->
    template.replace("%", amount.toString())
}

sealed class TemplateString

//an object with invoke operator
object ReceiptTextObj : TemplateString() {

    operator fun invoke(amount: Int): String =
        receiptText("My receipt for $%")(amount)

    operator fun invoke(template: String, amount: Int): String =
        receiptText(template)(amount)
}



