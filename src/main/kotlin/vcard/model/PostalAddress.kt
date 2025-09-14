package com.github.rcbrgr.vcard.model

import com.github.rcbrgr.vcard.model.param.AddressType

/**
 * Represents a structured postal address, separating standard enum types from custom string types.
 */
data class PostalAddress(
    val standardTypes: Set<AddressType> = emptySet(),
    val customTypes: Set<String> = emptySet(),
    val postOfficeBox: String? = null,
    val extendedAddress: String? = null,
    val streetAddress: String? = null,
    val locality: String? = null, // City
    val region: String? = null, // State or Province
    val postalCode: String? = null,
    val countryName: String? = null,
) {
    fun isType(type: AddressType): Boolean = standardTypes.contains(type)

    fun isType(type: String): Boolean = customTypes.any { it.equals(type, ignoreCase = true) }
}
