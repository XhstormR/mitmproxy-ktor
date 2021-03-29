@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package com.xhstormr.app

import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate
import sun.security.x509.CertificateExtensions
import sun.security.x509.DNSName
import sun.security.x509.GeneralName
import sun.security.x509.GeneralNames
import sun.security.x509.SubjectAlternativeNameExtension
import sun.security.x509.X500Name
import sun.security.x509.X509CertImpl
import sun.security.x509.X509CertInfo
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.concurrent.ConcurrentHashMap

object SslContextFactory {

    private val map = ConcurrentHashMap<String, SslContext>()

    private val caCrt: X509Certificate
    private val caKey: PrivateKey

    init {
        val certificate = File("ca.crt")
        val privateKey = File("ca.key")

        if (!certificate.exists() || !privateKey.exists()) {
            with(SelfSignedCertificate("ca")) {
                certificate().copyTo(certificate, true)
                privateKey().copyTo(privateKey, true)
                caCrt = cert()
                caKey = key()
            }
            caCrt.writePem(certificate)
            caKey.writePem(privateKey)
        } else {
            caCrt = certificate.inputStream().use {
                CertificateFactory.getInstance("X.509").generateCertificate(it) as X509Certificate
            }
            caKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateKey.readPemKey()))
        }
    }

    fun getSslContext(fqdn: String) = map.computeIfAbsent(fqdn) {
        generateSslContext(it)
    }

    private fun generateSslContext(fqdn: String) = with(SelfSignedCertificate(fqdn)) {
        SslContextBuilder.forServer(key(), cert().sign()).build()
    }

    private fun X509Certificate.sign(): X509Certificate {
        val fqdn = (subjectDN as X500Name).commonName

        val generalNames = GeneralNames().apply {
            add(GeneralName(DNSName(fqdn)))
            // add(GeneralName(IPAddressName(ip)))
        }

        val extensions = CertificateExtensions().apply {
            set(SubjectAlternativeNameExtension.NAME, SubjectAlternativeNameExtension(generalNames))
        }

        val certInfo = X509CertInfo(tbsCertificate).apply {
            this[X509CertInfo.ISSUER] = caCrt.subjectDN
            this[X509CertInfo.EXTENSIONS] = extensions
        }

        return X509CertImpl(certInfo)
            .apply { sign(caKey, caCrt.sigAlgName) }
    }
}
