package com.github.rcbrgr.vcard

import com.github.rcbrgr.vcard.config.WriterConfig
import com.github.rcbrgr.vcard.model.EmailAddress
import com.github.rcbrgr.vcard.model.PhoneNumber
import com.github.rcbrgr.vcard.model.PostalAddress
import com.github.rcbrgr.vcard.model.VCard
import com.github.rcbrgr.vcard.model.VCardVersion
import java.util.Base64
import kotlin.collections.map

/**
 * Creates formatted vCard strings from VCard data models with configurable output.
 *
 * @param config The configuration to use for writing.
 */
class VCardWriter(
    private val config: WriterConfig = WriterConfig(),
) {
    private val lineEnding = "\r\n"

    fun write(cards: Collection<VCard>): String = cards.joinToString(lineEnding) { write(it) }

    fun write(card: VCard): String {
        val builder = mutableListOf<String>()

        builder.add("BEGIN:VCARD")

        when (config.targetVersion) {
            VCardVersion.V4_0 -> {
                builder.add("VERSION:4.0")
                card.kind?.let { builder.add(buildProperty("KIND", it.name.lowercase())) }
                card.members?.forEach { builder.add(buildProperty("MEMBER", it)) }
            }

            VCardVersion.V3_0 -> builder.add("VERSION:3.0")
            VCardVersion.V2_1 -> builder.add("VERSION:2.1")
        }

        card.formattedName?.let { builder.add(buildProperty("FN", it)) }
        card.name?.let {
            val value =
                listOfNotNull(
                    it.familyName,
                    it.givenName,
                    it.additionalNames,
                    it.honorificPrefixes,
                    it.honorificSuffixes,
                ).joinToString(";")
            builder.add(buildProperty("N", value))
        }

        card.phoneNumbers.forEach { builder.add(buildPhoneProperty(it)) }
        card.emails.forEach { builder.add(buildEmailProperty(it)) }
        card.addresses.forEach { builder.add(buildAddressProperty(it)) }

        card.organization?.let { builder.add(buildProperty("ORG", it)) }
        card.title?.let { builder.add(buildProperty("TITLE", it)) }
        card.birthday?.let { builder.add(buildProperty("BDAY", it)) }
        card.note?.let { builder.add(buildProperty("NOTE", it)) }

        if (card.photoData != null) {
            val encoded = Base64.getEncoder().encodeToString(card.photoData)
            val photoType = "JPEG" // Default assumption
            val params =
                when (config.targetVersion) {
                    VCardVersion.V4_0 -> ";MEDIATYPE=image/jpeg"
                    else -> ";ENCODING=b64;TYPE=$photoType"
                }
            builder.add(buildPropertyWithParams("PHOTO", params, encoded))
        } else {
            card.photoUri?.let { builder.add(buildProperty("PHOTO", it)) }
        }

        card.rawProperties.forEach { (key, values) ->
            values.forEach { value -> builder.add(buildProperty(key, value)) }
        }

        builder.add("END:VCARD")

        return builder.joinToString(lineEnding)
    }

    private fun buildPhoneProperty(p: PhoneNumber): String {
        val allTypes = p.standardTypes.map { it.name }.toSet() + p.customTypes
        return buildTypedProperty("TEL", p.number, allTypes)
    }

    private fun buildEmailProperty(e: EmailAddress): String {
        val allTypes = e.standardTypes.map { it.name }.toSet() + e.customTypes
        return buildTypedProperty("EMAIL", e.address, allTypes)
    }

    private fun buildAddressProperty(addr: PostalAddress): String {
        val allTypes = addr.standardTypes.map { it.name }.toSet() + addr.customTypes
        val params = if (allTypes.isNotEmpty()) ";TYPE=${allTypes.joinToString(",")}" else ""
        val value =
            listOf(
                addr.postOfficeBox,
                addr.extendedAddress,
                addr.streetAddress,
                addr.locality,
                addr.region,
                addr.postalCode,
                addr.countryName,
            ).joinToString(";") { it ?: "" }
        return buildPropertyWithParams("ADR", params, value)
    }

    private fun buildProperty(
        key: String,
        value: String,
    ): String = foldLine("$key:${escapeValue(value)}")

    private fun buildPropertyWithParams(
        key: String,
        params: String,
        value: String,
    ) = foldLine("$key$params:${escapeValue(value)}")

    private fun buildTypedProperty(
        key: String,
        value: String,
        types: Set<String>,
    ): String {
        if (types.isEmpty()) return buildProperty(key, value)
        return buildPropertyWithParams(key, ";TYPE=${types.joinToString(",")}", value)
    }

    private fun escapeValue(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace(",", "\\,")
            .replace(";", "\\;")
            .replace("\n", "\\n")

    private fun foldLine(line: String): String {
        if (!config.foldLines || line.length <= config.maxLineLength) return line
        val builder = StringBuilder()
        builder.append(line.take(config.maxLineLength))
        var remaining = line.substring(config.maxLineLength)
        while (remaining.isNotEmpty()) {
            builder.append(lineEnding).append(" ")
            val chunk = remaining.take(config.maxLineLength - 1)
            builder.append(chunk)
            remaining = remaining.drop(chunk.length)
        }
        return builder.toString()
    }
}
