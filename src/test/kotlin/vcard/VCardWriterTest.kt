package vcard

import com.github.rcbrgr.vcard.VCardWriter
import com.github.rcbrgr.vcard.builder.VCardBuilder
import com.github.rcbrgr.vcard.config.WriterConfig
import com.github.rcbrgr.vcard.model.Kind
import com.github.rcbrgr.vcard.model.VCardVersion
import com.github.rcbrgr.vcard.model.param.PhoneType
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class VCardWriterTest {
    @Test
    fun `generates vCard 4_0 with enum types`() {
        val card =
            VCardBuilder("Test Org")
                .kind(Kind.ORG)
                .addPhoneNumber("123-555-0199", PhoneType.WORK, PhoneType.PREF)
                .build()

        val writer = VCardWriter(config = WriterConfig(targetVersion = VCardVersion.V4_0))
        val result = writer.write(card)

        assertTrue(result.contains("VERSION:4.0"))
        assertTrue(result.contains("KIND:org"))
        assertTrue(result.contains("TEL;TYPE=WORK,PREF:123-555-0199"))
    }

    @Test
    fun `encodes photo data to Base64`() {
        val card = VCardBuilder("Photo Test").photoData(byteArrayOf(1, 2, 3, 4, 5)).build()
        val writer = VCardWriter(config = WriterConfig(targetVersion = VCardVersion.V3_0))
        val result = writer.write(card)
        assertTrue(result.contains("PHOTO;ENCODING=b64;TYPE=JPEG:AQIDBAU="))
    }

    @Test
    fun `disables line folding when configured`() {
        val longNote = "This is a very long note that should absolutely not be folded."
        val card = VCardBuilder("Folding Test").note(longNote).build()
        val writer = VCardWriter(config = WriterConfig(foldLines = false))
        val result = writer.write(card)
        assertTrue(result.contains("NOTE:$longNote"))
        assertTrue(result.lines().none { it.startsWith(" ") })
    }
}
