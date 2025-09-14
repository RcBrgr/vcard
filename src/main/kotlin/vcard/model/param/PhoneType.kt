package com.github.rcbrgr.vcard.model.param

/**
 * Represents standard 'TYPE' parameter values for telephone numbers (TEL property).
 */
enum class PhoneType {
    HOME,
    WORK,
    PREF,
    VOICE,
    FAX,
    MSG,
    CELL,
    PAGER,
    BBS,
    MODEM,
    CAR,
    ISDN,
    VIDEO,
    ;

    companion object {
        fun fromString(s: String): PhoneType? = entries.find { it.name == s.uppercase() }
    }
}
