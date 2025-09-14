package com.github.rcbrgr.vcard.config

import com.github.rcbrgr.vcard.model.VCardVersion

/**
 * Configuration for the VCardWriter.
 *
 * @property targetVersion The vCard version to generate. Affects property names and formatting.
 * @property foldLines Whether to fold long lines according to the spec.
 * @property maxLineLength The maximum character length for a line before folding.
 */
data class WriterConfig(
    val targetVersion: VCardVersion = VCardVersion.V3_0,
    val foldLines: Boolean = true,
    val maxLineLength: Int = 75,
)
