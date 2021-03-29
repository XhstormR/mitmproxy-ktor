package com.xhstormr.app

import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)
/*
after io.ktor.server.netty.NettyChannelInitializer.initChannel
add before http1
*/
