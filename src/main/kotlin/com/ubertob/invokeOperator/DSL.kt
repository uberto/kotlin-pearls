package com.ubertob.invokeOperator


object Console {
    operator fun invoke (block: (String) -> String): Nothing {
        while (true) {
            val l = readLine()
            if (!l.isNullOrBlank())
                println(block(l))
        }
    }


}


fun main() {


    Console {
        val parts = it.split(' ')
        when (parts[0]) {
            "go" -> "going ${parts[1]}"
            "eat" -> "eating ${parts[1]}"
            "quit" -> throw InterruptedException("Program has been terminated by user")
            else -> "I don't think so..."
        }
    }

}