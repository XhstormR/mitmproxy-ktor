@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.xhstormr.app

import com.xhstormr.app.content.ApplicationRequestContent
import com.xhstormr.app.content.HttpResponseContent
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.request.ApplicationRequest
import io.ktor.request.header
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.util.appendFiltered
import sun.security.provider.X509Factory
import java.io.File
import java.io.InputStream
import java.io.SequenceInputStream
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.Base64
import java.util.Collections

inline fun <reified T> clazz() = T::class.java

operator fun InputStream.plus(inputStream: InputStream) =
    SequenceInputStream(Collections.enumeration(listOf(this, inputStream)))

suspend fun ApplicationCall.respondHttpResponse(response: HttpResponse) =
    respond(HttpResponseContent(response))

suspend fun HttpClient.requestApplicationRequest(request: ApplicationRequest) =
    request<HttpResponse>(request.uri) {
        body = ApplicationRequestContent(request)
        method = request.httpMethod
    }

fun ApplicationRequest.contentLength() =
    header(HttpHeaders.ContentLength)?.toLong() ?: 0

fun Headers.sanitize() = let {
    Headers.build { appendFiltered(it) { k, _ -> !HttpHeaders.isUnsafe(k) && k != "Host" } }
}

private const val BEGIN_KEY = "-----BEGIN PRIVATE KEY-----"
private const val END_KEY = "-----END PRIVATE KEY-----"

fun File.readPemKey(): ByteArray = readText()
    .replace(BEGIN_KEY, "")
    .replace(END_KEY, "")
    .replace("\n", "")
    .let { Base64.getDecoder().decode(it) }

fun PrivateKey.writePem(file: File) {
    file.bufferedWriter().use {
        it.appendLine(BEGIN_KEY)
        it.appendLine(Base64.getEncoder().encodeToString(this.encoded))
        it.appendLine(END_KEY)
    }
}

fun X509Certificate.writePem(file: File) {
    file.bufferedWriter().use {
        it.appendLine(X509Factory.BEGIN_CERT)
        it.appendLine(Base64.getEncoder().encodeToString(this.encoded))
        it.appendLine(X509Factory.END_CERT)
    }
}
