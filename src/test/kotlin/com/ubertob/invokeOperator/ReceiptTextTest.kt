package com.ubertob.invokeOperator

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ReceiptTextTest{

    @Test
    fun `print receipt`() {

        val r = ReceiptText("Thank you for you donation of $%!")

        assertThat(r(123))
            .isEqualTo("Thank you for you donation of $123!")

    }
}