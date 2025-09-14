package com.github.rcbrgr.vcard.builder

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

/**
 * A fluent builder for creating [VCard] objects.
 * This provides a more readable and flexible way to construct vCards compared to a large constructor.
 */
class VCardBuilder(
    private var formattedName: String,
    private var version: VCardVersion = VCardVersion.V4_0,
) {
    private var name: Name? = null
    private var kind: Kind? = null
    private val phoneNumbers: MutableList<PhoneNumber> = mutableListOf()
    private val emails: MutableList<EmailAddress> = mutableListOf()
    private val addresses: MutableList<PostalAddress> = mutableListOf()
    private var organization: String? = null
    private var title: String? = null
    private var photoUri: String? = null
    private var photoData: ByteArray? = null
    private var birthday: String? = null
    private var note: String? = null
    private val members: MutableList<String> = mutableListOf()
    private val rawProperties: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun version(version: VCardVersion) = apply { this.version = version }

    fun formattedName(fn: String) = apply { this.formattedName = fn }

    fun name(
        familyName: String,
        givenName: String,
        additionalNames: String? = null,
        prefix: String? = null,
        suffix: String? = null,
    ) = apply { this.name = Name(familyName, givenName, additionalNames, prefix, suffix) }

    fun kind(kind: Kind) = apply { this.kind = kind }

    fun organization(org: String) = apply { this.organization = org }

    fun title(title: String) = apply { this.title = title }

    fun birthday(bday: String) = apply { this.birthday = bday }

    fun note(note: String) = apply { this.note = note }

    fun photoUri(uri: String) = apply { this.photoUri = uri }

    fun photoData(data: ByteArray) = apply { this.photoData = data }

    fun addPhoneNumber(
        number: String,
        vararg types: PhoneType,
    ) = apply { this.phoneNumbers.add(PhoneNumber(number, standardTypes = types.toSet())) }

    fun addPhoneNumber(
        number: String,
        vararg customTypes: String,
    ) = apply {
        val standard = customTypes.mapNotNull { PhoneType.fromString(it) }.toSet()
        val custom = customTypes.filter { PhoneType.fromString(it) == null }.toSet()
        this.phoneNumbers.add(PhoneNumber(number, standard, custom))
    }

    fun addEmail(
        address: String,
        vararg types: EmailType,
    ) = apply { this.emails.add(EmailAddress(address, standardTypes = types.toSet())) }

    fun addEmail(
        address: String,
        vararg customTypes: String,
    ) = apply {
        val standard = customTypes.mapNotNull { EmailType.fromString(it) }.toSet()
        val custom = customTypes.filter { EmailType.fromString(it) == null }.toSet()
        this.emails.add(EmailAddress(address, standard, custom))
    }

    fun addAddress(
        vararg types: AddressType,
        street: String? = null,
        city: String? = null,
        region: String? = null,
        postalCode: String? = null,
        country: String? = null,
        poBox: String? = null,
        extended: String? = null,
    ) = apply {
        this.addresses.add(
            PostalAddress(
                types.toSet(),
                emptySet(),
                poBox,
                extended,
                street,
                city,
                region,
                postalCode,
                country,
            ),
        )
    }

    fun addAddress(
        vararg customTypes: String,
        street: String? = null,
        city: String? = null,
        region: String? = null,
        postalCode: String? = null,
        country: String? = null,
        poBox: String? = null,
        extended: String? = null,
    ) = apply {
        val standard = customTypes.mapNotNull { AddressType.fromString(it) }.toSet()
        val custom = customTypes.filter { AddressType.fromString(it) == null }.toSet()
        this.addresses.add(PostalAddress(standard, custom, poBox, extended, street, city, region, postalCode, country))
    }

    fun addMember(uri: String) = apply { this.members.add(uri) }

    fun addRawProperty(
        key: String,
        value: String,
    ) = apply { this.rawProperties.getOrPut(key.uppercase()) { mutableListOf() }.add(value) }

    fun build(): VCard =
        VCard(
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
