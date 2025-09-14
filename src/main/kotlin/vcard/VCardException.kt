package com.github.rcbrgr.vcard

/**
 * Base exception for any errors occurring during vCard processing.
 */
open class VCardException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * Thrown when the vCard data is structurally invalid.
 */
class MalformedVCardException(
    message: String,
    cause: Throwable? = null,
) : VCardException(message, cause)

/**
 * Thrown in strict mode when a required vCard property is missing.
 */
class MissingRequiredFieldException(
    message: String,
    cause: Throwable? = null,
) : VCardException(message, cause)
