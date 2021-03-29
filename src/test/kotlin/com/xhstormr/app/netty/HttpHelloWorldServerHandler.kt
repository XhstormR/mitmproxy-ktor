package com.xhstormr.app.netty

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus

@Sharable
class HttpHelloWorldServerHandler : SimpleChannelInboundHandler<HttpRequest>() {

    public override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest) {
        val response = DefaultFullHttpResponse(
            msg.protocolVersion(),
            HttpResponseStatus.OK,
            Unpooled.wrappedBuffer("Hello World!".toByteArray())
        ).apply {
            headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .setInt(HttpHeaderNames.CONTENT_LENGTH, content().readableBytes())
        }
        ctx.writeAndFlush(response)
    }
}
