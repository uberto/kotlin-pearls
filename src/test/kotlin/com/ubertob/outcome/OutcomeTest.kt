package com.ubertob.outcome

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.junit.jupiter.api.Test

internal class OutcomeTest {


    data class NonPerfectSquareError(override val msg: String): OutcomeError

    fun squareRoot(x: Int): Outcome<NonPerfectSquareError, Int> =
        Math.sqrt(x.toDouble()).toInt().let {
            if (it * it == x)
                Success(it)
            else
                NonPerfectSquareError("Not a perfect square ${Math.sqrt(x.toDouble())}").asFailure()
        }


    @Test
    fun `a few checks`(){

        assertThat(squareRoot(4)).isEqualTo(Success(2))
        assertThat(squareRoot(9)).isEqualTo(Success(3))
        assertThat(squareRoot(25)).isEqualTo(Success(5))

        assertThat(squareRoot(2)).isInstanceOf (Failure::class.java)
        assertThat(squareRoot(3)).isInstanceOf (Failure::class.java)
        assertThat(squareRoot(5)).isInstanceOf (Failure::class.java)


    }

    @Test
    fun `non local return pattern`(){
        for (i in (100 .. 200)){
            doSomething(i)
        }
    }

    private fun doSomething(i: Int) {
        val r = squareRoot(i).onFailure{return}
        println("perfect square $i of $r")
    }

//    @Test
//    fun `non local return pattern adv`(){
//        for (i in (100 .. 200)){
//            doSomethingSmart(i)
//        }
//    }
//
//    private fun doSomethingSmart(i: Int) {
//        squareRoot(i).onFailure(returning{return this})
//        println(i)
//    }
//
//    inline fun returning(waitfor: (Error) -> Unit){
//        return waitfor(e)
//
//    }



}


