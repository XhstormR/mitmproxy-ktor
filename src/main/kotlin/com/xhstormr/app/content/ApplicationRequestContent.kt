package com.xhstormr.app.content

import com.xhstormr.app.contentLength
import com.xhstormr.app.sanitize
import io.ktor.http.content.OutgoingContent
import io.ktor.request.ApplicationRequest
import io.ktor.request.contentType

class ApplicationRequestContent(
    private val request: ApplicationRequest
) : OutgoingContent.ReadChannelContent() {
    override val contentLength = request.contentLength()
    override val contentType = request.contentType()
    override val headers = request.headers.sanitize()

    override fun readFrom() = request.receiveChannel()
}
