package com.ubertob.objects

interface InterfaceRemoteServer {
    val randomSessionId: String
    val host: String
}

object RemoteServer {
    val randomSessionId = "session$sessionId"
    val host = "BlablaDb:1234"
}


val lazyRemoteServer by lazy {
    object : InterfaceRemoteServer {
        override val randomSessionId = "session$sessionId"
        override val host = "LazyBlablaDb:1234"
    }
}


fun main() {
    println("RemoteUrl ${remoteUrl}") //if we comment this line the next work
    println("RemoteUrl ${lazyRemoteUrl}") //if we comment this line the next work
    println("RandomUser ${RemoteServer.randomSessionId}")
    println("RandomUser ${lazyRemoteServer.randomSessionId}")
}