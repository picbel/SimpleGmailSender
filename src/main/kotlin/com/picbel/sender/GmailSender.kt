package com.picbel.sender

/**
 * An interface for sending emails.
 */
interface GmailSender {
    /**
     * Sends a single email.
     *
     * @param message The [EmailMessage] to send.
     */
    fun send(message: EmailMessage)

    /**
     * Sends multiple emails efficiently using a single connection.
     *
     * @param messages A list of [EmailMessage] objects to send.
     */
    fun sendBulk(messages: List<EmailMessage>)

    /**
     * Sends a single email asynchronously.
     *
     * @param message The [EmailMessage] to send.
     */
    fun sendAsync(message: EmailMessage)

    /**
     * Sends multiple emails asynchronously using a single connection.
     *
     * @param messages A list of [EmailMessage] objects to send.
     */
    fun sendBulkAsync(messages: List<EmailMessage>)

    /**
     * Represents a single email message to be sent.
     */
    data class EmailMessage(
        val to: String,
        val subject: String,
        val body: String
    )

    companion object {
        /**
         * Creates a new instance of a Gmail-specific [GmailSender].
         *
         * @param username The Gmail account username.
         * @param password The Gmail app password.
         * @return An instance of [GmailSender].
         */
        fun of(username: String, password: String): GmailSender {
            return GmailSenderImpl(userEmail = username, password = password)
        }

        /**
         * Creates a new instance of a Gmail-specific [GmailSender] with custom SMTP settings.
         *
         * @param host The SMTP host.
         * @param port The SMTP port.
         * @param username The Gmail account username.
         * @param password The Gmail app password.
         * @return An instance of [GmailSender].
         */
        fun of(host: String, port: String, username: String, password: String): GmailSender {
            return GmailSenderImpl(host = host, port = port, userEmail = username, password = password)
        }
    }
}
