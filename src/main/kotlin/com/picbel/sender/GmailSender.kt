package com.picbel.sender

/**
 * Gmail-specific email sender interface.
 * Operates in a stateless manner; each sending method call independently manages its connection.
 */
interface GmailSender {
    /**
     * Sends a single email synchronously.
     * This method creates and closes a new SMTP connection for each call.
     *
     * @param message The email message object ([EmailMessage]) to send.
     */
    fun send(message: EmailMessage)

    /**
     * Sends multiple emails efficiently using a single connection.
     * This method internally creates a single SMTP connection, sends all emails, and then closes the connection.
     * It continues sending even if some emails fail.
     *
     * @param messages A list of email message objects ([EmailMessage]) to send.
     * @return A [BulkSendResult] object containing the lists of successful and failed messages.
     */
    fun sendBulk(messages: List<EmailMessage>): BulkSendResult

    /**
     * Sends a single email asynchronously.
     * This method uses coroutines to execute blocking I/O operations on a background thread.
     *
     * @param message The email message object ([EmailMessage]) to send.
     */
    suspend fun sendAsync(message: EmailMessage)

    /**
     * Sends multiple emails asynchronously using a single connection.
     * This method uses coroutines to execute blocking I/O operations on a background thread.
     * It continues sending even if some emails fail.
     *
     * @param messages A list of email message objects ([EmailMessage]) to send.
     * @return A [BulkSendResult] object containing the lists of successful and failed messages.
     */
    suspend fun sendBulkAsync(messages: List<EmailMessage>): BulkSendResult

    /**
     * Data class defining the content of an email message.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param body The body of the email.
     */
    data class EmailMessage(val to: String, val subject: String, val body: String)

    /**
     * Represents the result of a bulk send operation.
     *
     * @param successful A list of the messages that were sent successfully.
     * @param failed A map where the key is the message that failed and the value is the Exception that occurred.
     */
    data class BulkSendResult(
        val successful: List<EmailMessage>,
        val failed: Map<EmailMessage, Exception>
    )

    companion object {
        /**
         * Factory method to create an instance of [GmailSender].
         * Uses default SMTP settings (smtp.gmail.com:587).
         *
         * @param username The Gmail account username.
         * @param password The Gmail app password or account password.
         * @return An instance of [GmailSender].
         */
        fun of(username: String, password: String): GmailSender {
            return GmailSenderImpl(username = username, password = password)
        }

        /**
         * Factory method to create an instance of [GmailSender] with custom SMTP settings.
         *
         * @param host The SMTP server host address.
         * @param port The SMTP server port number.
         * @param username The Gmail account username.
         * @param password The Gmail app password or account password.
         * @return An instance of [GmailSender].
         */
        fun of(host: String, port: String, username: String, password: String): GmailSender {
            return GmailSenderImpl(username = username, password = password, host = host, port = port)
        }
    }
}
