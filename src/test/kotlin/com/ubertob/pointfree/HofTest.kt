package com.ubertob.pointfree

import com.ubertob.pointFree.hofStyle1
import com.ubertob.pointFree.hofStyle2
import com.ubertob.pointFree.repeatExplicit
import com.ubertob.pointFree.repeatCompact
import org.junit.jupiter.api.Test

class HofTest {

    @Test
    fun compareStyle(){
        println(hofStyle1(5)(6))
        println(hofStyle2(5)(6))


        val repeatThrice = repeatExplicit(3)
        println(repeatThrice("hi"))

        val repeatTwice = repeatCompact(2)
        println(repeatTwice("help"))
    }
}