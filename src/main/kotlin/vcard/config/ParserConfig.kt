package com.github.rcbrgr.vcard.config

import com.github.rcbrgr.vcard.VCardException

/**
 * Configuration for the VCardParser.
 *
 * @property strictMode If true, the parser will throw a [VCardException] for malformed
 * vCards or for missing required properties (like FN or VERSION). Defaults to false.
 */
data class ParserConfig(
    val strictMode: Boolean = false,
)
