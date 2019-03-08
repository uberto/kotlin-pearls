package com.ubertob.invokeOperator

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ReceiptTextTest{

    @Test
    fun `receipt function class`() {

        val receipt = ReceiptText("Thank you for you donation of $%!")

        val text = receipt(123)

        assertThat(text).isEqualTo(
            "Thank you for you donation of $123!")

    }


    @Test
    fun `function returning lambda`() {

        val receipt = receiptText("Thank you for you donation of $%!")

        val text = receipt(123)

        assertThat(text).isEqualTo(
            "Thank you for you donation of $123!")

    }

    @Test
    fun `object invoke`() {

        val text1 = ReceiptTextObj(123)

        assertThat(text1).isEqualTo("My receipt for \$123")


        val text2 = ReceiptTextObj("My receipt for \$%",123)

        assertThat(text2).isEqualTo("My receipt for \$123")

    }


    @Test
    fun `put all functions in a container`(){

        val functions = mutableListOf<FIntToString>()

        functions.add(receiptText("TA %"))
        functions.add(ReceiptText("Thank you for $%!"))
        functions.add(ReceiptTextObj::invoke)

        val receipts = functions
            .map { it(123) }

        assertThat(receipts.toString()).isEqualTo("[TA 123, Thank you for \$123!, My receipt for \$123]")
    }

}