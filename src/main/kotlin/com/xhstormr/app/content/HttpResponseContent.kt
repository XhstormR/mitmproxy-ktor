package com.xhstormr.app.content

import com.xhstormr.app.sanitize
import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyAndClose

class HttpResponseContent(
    private val response: HttpResponse
) : OutgoingContent.WriteChannelContent() {
    override val contentLength = response.contentLength()
    override val contentType = response.contentType()
    override val headers = response.headers.sanitize()
    override val status = response.status

    override suspend fun writeTo(channel: ByteWriteChannel) {
        response.content.copyAndClose(channel)
    }
}
