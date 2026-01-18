package com.picbel.sender

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.*

/**
 * A Gmail-specific implementation of the [GmailSender] interface.
 * This class is internal and should be instantiated via the [GmailSender.of] factory method.
 */
internal class GmailSenderImpl(
    private val host: String = "smtp.gmail.com",
    private val port: String = "587",
    private val userEmail: String,
    private val password: String
) : GmailSender {

    private val session: Session

    init {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", host)
            put("mail.smtp.port", port)
        }
        session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(userEmail, password)
            }
        })
    }

    override fun send(message: GmailSender.EmailMessage) {
        try {
            val mime = createMimeMessage(message)
            Transport.send(mime)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun sendBulk(messages: List<GmailSender.EmailMessage>) {
        try {
            session.getTransport("smtp").use { transport ->
                transport.connect(userEmail, password) // Connect once

                for (msg in messages) {
                    val mime = createMimeMessage(msg)
                    transport.sendMessage(mime, mime.allRecipients)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun createMimeMessage(message: GmailSender.EmailMessage): MimeMessage = MimeMessage(session).apply {
        setFrom(InternetAddress(userEmail))
        setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.to))
        this.subject = subject
        setText(message.body)
    }
}
