package com.github.rcbrgr.vcard.model

/**
 * Represents a structured name (vCard 'N' property).
 */
data class Name(
    val familyName: String?,
    val givenName: String?,
    val additionalNames: String?,
    val honorificPrefixes: String?,
    val honorificSuffixes: String?,
)
