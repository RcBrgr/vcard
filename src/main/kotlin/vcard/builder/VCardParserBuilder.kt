package com.github.rcbrgr.vcard.builder

import com.github.rcbrgr.vcard.VCardParser
import com.github.rcbrgr.vcard.config.ParserConfig

/**
 * A fluent builder for creating and configuring a [VCardParser].
 */
class VCardParserBuilder {
    private var strictMode: Boolean = false

    /**
     * Configures the parser to be strict. It will throw exceptions on malformed data
     * or missing required fields.
     */
    fun strict() = apply { this.strictMode = true }

    /**
     * Configures the parser to be lenient (default). It will attempt to parse as much
     * data as possible, skipping over errors.
     */
    fun lenient() = apply { this.strictMode = false }

    /**
     * Constructs the final configured [VCardParser] instance.
     */
    fun build(): VCardParser = VCardParser(ParserConfig(strictMode = strictMode))
}
