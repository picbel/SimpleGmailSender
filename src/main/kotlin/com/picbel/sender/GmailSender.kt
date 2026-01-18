package com.picbel.sender

/**
 * An interface for sending emails.
 */
interface GmailSender {
    /**
     * Sends a single email.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param body The body of the email.
     */
    fun send(message: EmailMessage)

    /**
     * Sends multiple emails efficiently using a single connection.
     *
     * @param messages A list of [EmailMessage] objects to send.
     */
    fun sendBulk(messages: List<EmailMessage>)

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
        fun createGmailSender(username: String, password: String): GmailSender {
            return GmailSenderImpl(username = username, password = password)
        }
    }
}
