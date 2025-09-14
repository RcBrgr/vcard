package com.github.rcbrgr.vcard.model

/**
 * Represents a single vCard with enhanced, strongly-typed properties.
 */
data class VCard(
    val version: VCardVersion?,
    val formattedName: String?,
    val name: Name? = null,
    val kind: Kind? = null,
    val phoneNumbers: List<PhoneNumber> = emptyList(),
    val emails: List<EmailAddress> = emptyList(),
    val addresses: List<PostalAddress> = emptyList(),
    val organization: String? = null,
    val title: String? = null,
    val photoUri: String? = null,
    val photoData: ByteArray? = null,
    val birthday: String? = null,
    val note: String? = null,
    val members: List<String>? = null,
    val rawProperties: Map<String, List<String>> = emptyMap(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VCard
        if (version != other.version) return false
        if (formattedName != other.formattedName) return false
        if (photoData != null) {
            if (other.photoData == null) return false
            if (!photoData.contentEquals(other.photoData)) return false
        } else if (other.photoData != null) {
            return false
        }
        return name == other.name &&
            kind == other.kind &&
            phoneNumbers == other.phoneNumbers &&
            emails == other.emails &&
            addresses == other.addresses &&
            organization == other.organization &&
            title == other.title &&
            photoUri == other.photoUri &&
            birthday == other.birthday &&
            note == other.note &&
            members == other.members &&
            rawProperties == other.rawProperties
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + (formattedName?.hashCode() ?: 0)
        result = 31 * result + (photoData?.contentHashCode() ?: 0)
        return result
    }
}
