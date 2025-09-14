package com.github.rcbrgr.vcard.model

/**
 * Represents the KIND property from vCard 4.0.
 */
enum class Kind {
    INDIVIDUAL,
    GROUP,
    ORG,
    LOCATION,
    ;

    companion object {
        fun fromString(s: String?): Kind? =
            when (s?.uppercase()) {
                "INDIVIDUAL" -> INDIVIDUAL
                "GROUP" -> GROUP
                "ORG" -> ORG
                "LOCATION" -> LOCATION
                else -> null
            }
    }
}
