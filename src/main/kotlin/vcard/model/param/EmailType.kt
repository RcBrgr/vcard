package com.github.rcbrgr.vcard.model.param

/**
 * Represents standard 'TYPE' parameter values for email addresses (EMAIL property).
 */
enum class EmailType {
    INTERNET,
    PREF,
    ;

    companion object {
        fun fromString(s: String): EmailType? = entries.find { it.name == s.uppercase() }
    }
}
