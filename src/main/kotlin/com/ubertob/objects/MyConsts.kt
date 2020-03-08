package com.ubertob.objects

import kotlin.random.Random

val remoteUrl = "http://${RemoteServer.host}/connect"

val sessionId = Random.nextInt(9000) + 1000

val lazyRemoteUrl = "http://${lazyRemoteServer.host}/connect"
