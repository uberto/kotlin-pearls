package com.ubertob.multipleReceivers

import java.io.File
import java.time.Instant

//not sure how to use them
//typealias FileR = (File) -> Unit
//typealias TimeR = () -> Instant

interface FileR{
    fun writeAThing(date: Instant)
}

interface TimeR{
    fun needsTheDate(): Instant
}


class GlobalContextR: TimeR, FileR{
    override fun writeAThing(date: Instant) {
        println(date)
    }

    override fun needsTheDate(): Instant =
        Instant.now()

}
context(GlobalContextR) fun topIshLevelfun() {
    val d = needsTheDate()
    writeAThing(d)
}

fun main(){
    with(GlobalContextR()){
        topIshLevelfun()
    }
}

