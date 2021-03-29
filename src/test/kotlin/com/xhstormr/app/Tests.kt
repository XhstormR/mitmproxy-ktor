package com.xhstormr.app

import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.network.tls.extensions.HashAlgorithm
import io.ktor.network.tls.extensions.SignatureAlgorithm
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Tests {

    @BeforeAll
    fun beforeAll() {
        println("BeforeAll")
    }

    @Disabled
    @Test
    fun generateJks() {
        val file = File("server.jks")

        val keyStore = buildKeyStore {
            certificate("server") {
                hash = HashAlgorithm.SHA256
                sign = SignatureAlgorithm.RSA
                daysValid = 365
                keySizeInBits = 2048
                password = "changeit"
            }
        }
        keyStore.saveToFile(file, "changeit")
    }
}
