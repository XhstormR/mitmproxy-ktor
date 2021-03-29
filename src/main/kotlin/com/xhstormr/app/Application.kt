package com.xhstormr.app

import com.xhstormr.app.handler.HttpsProxyHandler
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.features.CallLogging
import io.ktor.features.origin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.contentType
import io.ktor.request.document
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.httpMethod
import io.ktor.request.httpVersion
import io.ktor.request.path
import io.ktor.request.port
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.server.engine.EngineAPI
import io.ktor.server.netty.NettyApplicationCall
import org.slf4j.event.Level

@EngineAPI
fun Application.main() {

    install(CallLogging) {
        level = Level.INFO
    }

    val client = HttpClient(CIO) {
        expectSuccess = false
    }

    intercept(ApplicationCallPipeline.Setup) {
        with(call) {
            if (this is NettyApplicationCall) {
                if (request.httpMethod.value == io.netty.handler.codec.http.HttpMethod.CONNECT.name()) {
                    val hostHeader = request.header(HttpHeaders.Host)!!
                    context.pipeline().addBefore("http1", "HttpsProxyHandler", HttpsProxyHandler(hostHeader))

                    call.respond(HttpStatusCode.Continue)

                    finish()
                }
            }
        }
    }

    intercept(ApplicationCallPipeline.Call) {
        val request = call.request
        val version = request.httpVersion // "HTTP/1.1"
        val httpMethod = request.httpMethod // GET, POST...
        val uri = request.uri // Short cut for `origin.uri`
        val path = request.path() // The uri without the query string
        val document = request.document() // The last component after '/' of the uri
        val scheme = request.origin.scheme // "http" or "https"
        val remoteHost = request.origin.remoteHost // The IP address of the client doing the request
        val host = request.host() // The host part without the port
        val port = request.port() // Port of request
        val contentLength = request.contentLength()
        val contentType = request.contentType()
        println("version: $version")
        println("httpMethod: $httpMethod")
        println("uri: $uri")
        println("path: $path")
        println("document: $document")
        println("scheme: $scheme")
        println("remoteHost: $remoteHost")
        println("host: $host")
        println("port: $port")
        println("contentLength: $contentLength")
        println("contentType: $contentType")
        request.headers.sanitize().forEach { s, list -> println("$s || $list") }
        println("---------------")

        val response = client.requestApplicationRequest(call.request)

        call.respondHttpResponse(response)

        finish()
    }
}
