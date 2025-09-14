package com.github.rcbrgr.vcard.model

/**
 * Defines the vCard version.
 */
enum class VCardVersion {
    V2_1,
    V3_0,
    V4_0,
    ;

    companion object {
        fun fromString(s: String): VCardVersion? = VCardVersion.entries.find { it.name == s.uppercase() }
    }
}
