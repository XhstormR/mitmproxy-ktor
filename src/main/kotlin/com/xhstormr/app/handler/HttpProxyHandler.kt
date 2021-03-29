package com.xhstormr.app.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest

@Sharable
class HttpProxyHandler : SimpleChannelInboundHandler<HttpRequest>() {

    public override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest) {
        if (msg.method() === HttpMethod.CONNECT) {
            val hostHeader = msg.headers().get(HttpHeaderNames.HOST)
            ctx.pipeline().replace(this, "HttpsProxyHandler", HttpsProxyHandler(hostHeader))
        } else {
            ctx.fireChannelRead(msg)
            ctx.pipeline().remove(this)
        }
    }
}
