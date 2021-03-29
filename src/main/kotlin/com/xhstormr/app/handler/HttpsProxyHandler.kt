package com.xhstormr.app.handler

import com.xhstormr.app.SslContextFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import java.net.URL

class HttpsProxyHandler(hostHeader: String) : SimpleChannelInboundHandler<HttpRequest>() {

    private val host: String
    private val port: Int

    init {
        val hostHeaders = hostHeader.split(':')
        host = hostHeaders[0]
        port = hostHeaders.getOrNull(1)?.toInt() ?: 443
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        val sslCtx = SslContextFactory.getSslContext(host)

        ctx.writeAndFlush(DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK))
        ctx.pipeline().addFirst("ssl", sslCtx.newHandler(ctx.alloc()))
    }

    public override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest) {
        msg.uri = URL("https", host, port, msg.uri()).toString()

        ctx.fireChannelRead(msg)
    }
}

/*
curl -ivkx 127.0.0.1:8080 https://www.qq.com:666/123/123?aa=aa  -v

DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
CONNECT www.qq.com:666 HTTP/1.1
Host: www.qq.com:666
User-Agent: curl/7.74.0
Proxy-Connection: Keep-Alive

DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET /123/123?aa=aa HTTP/1.1
Host: www.qq.com:666
User-Agent: curl/7.74.0
Accept:

curl -ivkx 127.0.0.1:8080 http://www.qq.com:666/123/123?aa=aa  -v

DefaultHttpRequest(decodeResult: success, version: HTTP/1.1)
GET http://www.qq.com:666/123/123?aa=aa HTTP/1.1
Host: www.qq.com:666
User-Agent: curl/7.74.0
Accept:
Proxy-Connection: Keep-Alive
*/
