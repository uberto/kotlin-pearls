package com.ubertob.implementationDelegation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ApiClientTest {

    fun printIt(value: Any?){
        println(value)
    }

    @Test
    fun simpleDelegation() {
        val client = HttpApiClient()
        repeatCall(client, 5)
            .forEach(::printIt)
    }

    @Test
    fun wrapperDelegation() {
        val client = DoSomethingWrapper(  HttpApiClient())
        repeatCall(client, 5)
            .forEach(::printIt)
    }
}

