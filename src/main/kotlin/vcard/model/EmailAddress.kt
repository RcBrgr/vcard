package com.github.rcbrgr.vcard.model

import com.github.rcbrgr.vcard.model.param.EmailType

/**
 * Represents a single email address, separating standard enum types from custom string types.
 */
data class EmailAddress(
    val address: String,
    val standardTypes: Set<EmailType> = emptySet(),
    val customTypes: Set<String> = emptySet(),
) {
    fun isType(type: EmailType): Boolean = standardTypes.contains(type)

    fun isType(type: String): Boolean = customTypes.any { it.equals(type, ignoreCase = true) }
}
