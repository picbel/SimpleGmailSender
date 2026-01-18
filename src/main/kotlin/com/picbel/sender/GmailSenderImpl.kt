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

    /**
     * Sends a single email synchronously.
     * This method creates and closes a new SMTP connection for each call.
     *
     * @param message The email message object ([EmailMessage]) to send.
     */
    override fun send(message: GmailSender.EmailMessage) {
        val mime = createMimeMessage(message.to, message.subject, message.body)
        Transport.send(mime)
    }

    /**
     * Sends multiple emails efficiently using a single connection.
     * This method internally creates a single SMTP connection, sends all emails, and then closes the connection.
     *
     * @param messages A list of email message objects ([EmailMessage]) to send.
     */
    override fun sendBulk(messages: List<GmailSender.EmailMessage>) {
        session.getTransport("smtp").use { transport ->
            transport.connect(username, password)
            for (msg in messages) {
                val message = createMimeMessage(msg.to, msg.subject, msg.body)
                transport.sendMessage(message, message.allRecipients)
            }
        }
    }

    /**
     * Sends a single email asynchronously.
     * This method uses coroutines to execute blocking I/O operations on a background thread.
     *
     * @param message The email message object ([EmailMessage]) to send.
     */
    override suspend fun sendAsync(message: GmailSender.EmailMessage) = withContext(Dispatchers.IO) {
        send(message)
    }

    /**
     * Sends multiple emails asynchronously using a single connection.
     * This method uses coroutines to execute blocking I/O operations on a background thread,
     * and internally creates a single SMTP connection, sends all emails, and then closes the connection.
     *
     * @param messages A list of email message objects ([EmailMessage]) to send.
     */
    override suspend fun sendBulkAsync(messages: List<GmailSender.EmailMessage>) = withContext(Dispatchers.IO) {
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