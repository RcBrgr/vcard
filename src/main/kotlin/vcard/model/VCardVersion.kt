package com.github.rcbrgr.vcard.model

/**
 * Defines the vCard version.
 */
enum class VCardVersion(val version: String) {
    V2_1("2.1"),
    V3_0("3.0"),
    V4_0("4.0"),
    ;

    companion object {
        fun fromString(s: String): VCardVersion? = VCardVersion.entries.find { it.version == s }
    }
}
