package com.picbel.sender

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

internal class GmailSenderImpl(
    private val username: String,
    private val password: String,
    private val host: String = "smtp.gmail.com",
    private val port: String = "587"
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
                return PasswordAuthentication(username, password)
            }
        })
    }

    override fun send(message: GmailSender.EmailMessage) {
        val mime = createMimeMessage(message.to, message.subject, message.body)
        Transport.send(mime)
    }

    override fun sendBulk(messages: List<GmailSender.EmailMessage>): GmailSender.BulkSendResult {
        val successful = mutableListOf<GmailSender.EmailMessage>()
        val failed = mutableMapOf<GmailSender.EmailMessage, Exception>()

        session.getTransport("smtp").use { transport ->
            try {
                transport.connect(username, password)
                for (msg in messages) {
                    try {
                        val message = createMimeMessage(msg.to, msg.subject, msg.body)
                        transport.sendMessage(message, message.allRecipients)
                        successful.add(msg)
                    } catch (e: Exception) {
                        failed[msg] = e
                    }
                }
            } catch (e: Exception) {
                // If the initial connection fails, all messages fail.
                messages.forEach { msg -> failed[msg] = e }
            }
        }
        return GmailSender.BulkSendResult(successful, failed)
    }

    override suspend fun sendAsync(message: GmailSender.EmailMessage) = withContext(Dispatchers.IO) {
        send(message)
    }

    override suspend fun sendBulkAsync(messages: List<GmailSender.EmailMessage>): GmailSender.BulkSendResult = withContext(Dispatchers.IO) {
        sendBulk(messages)
    }
    
    private fun createMimeMessage(to: String, subject: String, body: String): MimeMessage {
        return MimeMessage(session).apply {
            setFrom(InternetAddress(username))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            this.subject = subject
            setText(body)
        }
    }
}
