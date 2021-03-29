package com.xhstormr.app.netty

import com.xhstormr.app.handler.HttpProxyHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class HttpHelloWorldServerInitializer : ChannelInitializer<SocketChannel>() {

    private val loggingHandler = LoggingHandler(LogLevel.INFO)
    private val httpProxyHandler = HttpProxyHandler()
    private val httpHelloWorldServerHandler = HttpHelloWorldServerHandler()

    public override fun initChannel(ch: SocketChannel) {
        with(ch.pipeline()) {
            addLast(loggingHandler)
            addLast(HttpServerCodec())
            addLast(httpProxyHandler)
            addLast(httpHelloWorldServerHandler)
        }
    }
}
