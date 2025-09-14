package com.github.rcbrgr.vcard.internal

import com.github.rcbrgr.vcard.MalformedVCardException
import com.github.rcbrgr.vcard.MissingRequiredFieldException
import com.github.rcbrgr.vcard.config.ParserConfig
import com.github.rcbrgr.vcard.model.EmailAddress
import com.github.rcbrgr.vcard.model.Kind
import com.github.rcbrgr.vcard.model.Name
import com.github.rcbrgr.vcard.model.PhoneNumber
import com.github.rcbrgr.vcard.model.PostalAddress
import com.github.rcbrgr.vcard.model.VCard
import com.github.rcbrgr.vcard.model.VCardVersion
import com.github.rcbrgr.vcard.model.param.AddressType
import com.github.rcbrgr.vcard.model.param.EmailType
import com.github.rcbrgr.vcard.model.param.PhoneType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.util.Base64

/**
 * Internal processor that handles the low-level logic of parsing a vCard stream.
 */
internal class VCardStreamProcessor(
    private val reader: BufferedReader,
    private val config: ParserConfig,
) {
    fun process(): List<VCard> {
        val vcards = mutableListOf<VCard>()
        var builder: VCardBuilder? = null
        getUnfoldedLines().forEach { line ->
            when {
                line.equals("BEGIN:VCARD", true) -> builder = VCardBuilder(config)
                line.equals("END:VCARD", true) -> {
                    builder?.let { vcards.add(it.build()) }
                    builder = null
                }

                else -> builder?.parseProperty(line)
            }
        }
        return vcards
    }

    fun processFlow(): Flow<VCard> =
        flow {
            var builder: VCardBuilder? = null
            reader.use { r ->
                getUnfoldedLines(r).forEach { line ->
                    when {
                        line.equals("BEGIN:VCARD", true) -> builder = VCardBuilder(config)
                        line.equals("END:VCARD", true) -> {
                            builder?.let { emit(it.build()) }
                            builder = null
                        }

                        else -> builder?.parseProperty(line)
                    }
                }
            }
        }

    private fun getUnfoldedLines(bufferedReader: BufferedReader = reader) =
        sequence {
            var line = bufferedReader.readLine()
            while (line != null) {
                val fullLine = StringBuilder(line)
                bufferedReader.mark(1024)
                var nextLine = bufferedReader.readLine()
                while (nextLine != null && (nextLine.startsWith(' ') || nextLine.startsWith('\t'))) {
                    fullLine.append(nextLine.substring(1))
                    bufferedReader.mark(1024)
                    nextLine = bufferedReader.readLine()
                }
                bufferedReader.reset()
                yield(fullLine.toString())
                line = bufferedReader.readLine()
            }
        }

    private class VCardBuilder(
        private val config: ParserConfig,
    ) {
        private var version: VCardVersion? = null
        private var formattedName: String? = null
        private var name: Name? = null
        private var kind: Kind? = null
        private val phoneNumbers = mutableListOf<PhoneNumber>()
        private val emails = mutableListOf<EmailAddress>()
        private val addresses = mutableListOf<PostalAddress>()
        private var organization: String? = null
        private var title: String? = null
        private var photoUri: String? = null
        private var photoData: ByteArray? = null
        private var birthday: String? = null
        private var note: String? = null
        private val members = mutableListOf<String>()
        private val rawProperties = mutableMapOf<String, MutableList<String>>()

        fun parseProperty(line: String) {
            val (propKey, propValue) =
                line.split(":", limit = 2).takeIf { it.size == 2 }
                    ?: run {
                        if (config.strictMode) throw MalformedVCardException("Line is missing ':' separator: $line")
                        return
                    }
            val keyParts = propKey.split(';')
            val propName = keyParts[0].uppercase()
            val params =
                keyParts.drop(1).associate {
                    val (k, v) = it.split('=', limit = 2)
                    k.uppercase() to v
                }
            val typeStrings = params["TYPE"]?.split(',')?.map { it.trim().uppercase() } ?: emptyList()

            when (propName) {
                "VERSION" -> version = VCardVersion.fromString(propValue)
                "FN" -> formattedName = propValue
                "N" -> {
                    val p = propValue.split(';')
                    name =
                        Name(p.getOrNull(0), p.getOrNull(1), p.getOrNull(2), p.getOrNull(3), p.getOrNull(4))
                }

                "KIND" -> kind = Kind.fromString(propValue)
                "TEL" -> {
                    val s = typeStrings.mapNotNull { PhoneType.fromString(it) }.toSet()
                    val c = typeStrings.filter { PhoneType.fromString(it) == null }.toSet()
                    phoneNumbers.add(
                        PhoneNumber(propValue, s, c),
                    )
                }

                "EMAIL" -> {
                    val s = typeStrings.mapNotNull { EmailType.fromString(it) }.toSet()
                    val c = typeStrings.filter { EmailType.fromString(it) == null }.toSet()
                    emails.add(
                        EmailAddress(
                            propValue,
                            s,
                            c,
                        ),
                    )
                }

                "ADR" -> {
                    val s = typeStrings.mapNotNull { AddressType.fromString(it) }.toSet()
                    val c = typeStrings.filter { AddressType.fromString(it) == null }.toSet()
                    val p = propValue.split(';')
                    addresses.add(
                        PostalAddress(
                            s,
                            c,
                            p.getOrNull(0),
                            p.getOrNull(1),
                            p.getOrNull(2),
                            p.getOrNull(3),
                            p.getOrNull(4),
                            p.getOrNull(5),
                            p.getOrNull(6),
                        ),
                    )
                }

                "PHOTO" -> {
                    if (params["ENCODING"]?.equals("B64", true) == true ||
                        params["ENCODING"]?.equals(
                            "BASE64",
                            true,
                        ) == true
                    ) {
                        try {
                            photoData = Base64.getDecoder().decode(propValue)
                        } catch (e: Exception) {
                            if (config.strictMode) throw MalformedVCardException("Invalid Base64", e)
                        }
                    } else {
                        photoUri = propValue
                    }
                }

                "MEMBER" -> members.add(propValue)
                "ORG" -> organization = propValue
                "TITLE" -> title = propValue
                "BDAY" -> birthday = propValue
                "NOTE" -> note = propValue.replace("\\n", "\n").replace("\\,", ",")
                else -> rawProperties.getOrPut(propName) { mutableListOf() }.add(propValue)
            }
        }

        fun build(): VCard {
            if (config.strictMode) {
                if (version == null) throw MissingRequiredFieldException("VERSION is required")
                if (formattedName.isNullOrBlank()) throw MissingRequiredFieldException("FN is required")
            }
            return VCard(
                version,
                formattedName,
                name,
                kind,
                phoneNumbers,
                emails,
                addresses,
                organization,
                title,
                photoUri,
                photoData,
                birthday,
                note,
                members.takeIf { it.isNotEmpty() },
                rawProperties,
            )
        }
    }
}
