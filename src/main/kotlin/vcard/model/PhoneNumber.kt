package com.github.rcbrgr.vcard.model

import com.github.rcbrgr.vcard.model.param.PhoneType

/**
 * Represents a single phone number, separating standard enum types from custom string types.
 */
data class PhoneNumber(
    val number: String,
    val standardTypes: Set<PhoneType> = emptySet(),
    val customTypes: Set<String> = emptySet(),
) {
    /** Checks if the phone number includes a specific standard type. */
    fun isType(type: PhoneType): Boolean = standardTypes.contains(type)

    /** Checks if the phone number includes a specific custom type (case-insensitive). */
    fun isType(type: String): Boolean = customTypes.any { it.equals(type, ignoreCase = true) }
}
