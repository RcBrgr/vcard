package com.github.rcbrgr.vcard.model.param

/**
 * Represents standard 'TYPE' parameter values for postal addresses (ADR property).
 */
enum class AddressType {
    HOME,
    WORK,
    PREF,
    ;

    companion object {
        fun fromString(s: String): AddressType? = entries.find { it.name == s.uppercase() }
    }
}
