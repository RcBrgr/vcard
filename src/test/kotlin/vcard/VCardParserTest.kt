package vcard

import com.github.rcbrgr.vcard.MissingRequiredFieldException
import com.github.rcbrgr.vcard.VCardParser
import com.github.rcbrgr.vcard.model.Kind
import com.github.rcbrgr.vcard.model.param.PhoneType
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VCardParserTest {
    @Test
    fun `parses vCard 4_0 and enum types correctly`() {
        val vcardString =
            """
            BEGIN:VCARD
            VERSION:4.0
            FN:The Smiths
            KIND:group
            TEL;TYPE=HOME,VOICE:123-555-0100
            END:VCARD
            """.trimIndent()
        val parser = VCardParser.builder().build()
        val card = parser.parse(vcardString).first()

        assertEquals(Kind.GROUP, card.kind)
        assertEquals(1, card.phoneNumbers.size)
        assertEquals(setOf(PhoneType.HOME, PhoneType.VOICE), card.phoneNumbers.first().standardTypes)
        assertTrue(card.phoneNumbers.first().isType(PhoneType.HOME))
    }

    @Test
    fun `strict mode throws exception for missing FN`() {
        val vcardString = "BEGIN:VCARD\nVERSION:3.0\nEND:VCARD"
        val parser = VCardParser.builder().strict().build()
        assertThrows<MissingRequiredFieldException> { parser.parse(vcardString) }
    }

    @Test
    fun `lenient mode parses card with missing FN`() {
        val vcardString = "BEGIN:VCARD\nVERSION:3.0\nEND:VCARD"
        val parser = VCardParser.builder().lenient().build()
        val card = parser.parse(vcardString).first()
        assertNull(card.formattedName)
    }

    @Test
    fun `decodes Base64 photo data`() {
        val base64Image = "AQIDBAU="
        val vcardString = "BEGIN:VCARD\nVERSION:3.0\nFN:Photo\nPHOTO;ENCODING=b64;TYPE=GIF:$base64Image\nEND:VCARD"
        val card =
            VCardParser
                .builder()
                .build()
                .parse(vcardString)
                .first()
        assertNotNull(card.photoData)
        assertEquals(5, card.photoData.size)
    }
}
