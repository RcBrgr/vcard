package com.github.rcbrgr.vcard

import com.github.rcbrgr.vcard.builder.VCardParserBuilder
import com.github.rcbrgr.vcard.config.ParserConfig
import com.github.rcbrgr.vcard.internal.VCardStreamProcessor
import com.github.rcbrgr.vcard.model.VCard
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

/**
 * A robust, user-friendly parser for vCard files with configurable behavior.
 * Use the companion object's `builder()` method to create an instance.
 *
 * @param config The configuration to use for parsing.
 */
class VCardParser internal constructor(
    private val config: ParserConfig,
) {
    companion object {
        /**
         * Creates a new fluent builder for configuring a VCardParser.
         * @return A [VCardParserBuilder] instance.
         */
        @JvmStatic
        fun builder(): VCardParserBuilder = VCardParserBuilder()
    }

    fun parse(inputStream: InputStream): List<VCard> = VCardStreamProcessor(inputStream.bufferedReader(), config).process()

    fun parse(file: File): List<VCard> = file.inputStream().use { parse(it) }

    fun parse(vcardString: String): List<VCard> = vcardString.byteInputStream().use { parse(it) }

    fun parseFlow(inputStream: InputStream): Flow<VCard> = VCardStreamProcessor(inputStream.bufferedReader(), config).processFlow()

    fun parseFlow(file: File): Flow<VCard> = VCardStreamProcessor(file.bufferedReader(), config).processFlow()

    fun parseFlow(vcardString: String): Flow<VCard> = VCardStreamProcessor(vcardString.reader().buffered(), config).processFlow()
}
