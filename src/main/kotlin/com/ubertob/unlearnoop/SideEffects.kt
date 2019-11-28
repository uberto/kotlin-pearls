package com.ubertob.unlearnoop

import java.io.File


fun getGreetingsText(userName: String): List<String> {
    val template = File("mytemplate")
        .readLines()

    return template
        .map { it.replace("{username}", userName) }
}



fun getGreetingsText(userName: UserName,
                     reader: () -> List<String>): GreetingsText =
    reader()
        .map { it.replace("{username}", userName.name) }
        .let(::GreetingsText)


data class GreetingsText(val lines: List<String>)

