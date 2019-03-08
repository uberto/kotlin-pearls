package com.ubertob.invokeOperator

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextInt

internal class PersonTest{

    @Test
    fun `currying a person`(){
        val frank = personBuilder("Frank")(32)(78.5)

        assertThat(frank.toString()).isEqualTo("Person(name=Frank, age=32, weight=78.5)")
    }


    @Test
    fun `currying some people`() {
        val names = listOf("Joe", "Mary", "Bob", "Alice")

        val people: List<Person> = names
            .map { personBuilder(it) } //choose the name
            .map { it(nextInt(80)) } //a random age
            .map { it(nextDouble(100.0)) } //a random weight

        assertThat(people).hasSize(4)
    }

}