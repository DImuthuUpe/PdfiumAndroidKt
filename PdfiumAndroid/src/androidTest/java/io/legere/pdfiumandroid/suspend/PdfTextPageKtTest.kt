package io.legere.pdfiumandroid.suspend

import android.graphics.RectF
import com.google.common.truth.Truth
import io.legere.pdfiumandroid.base.BasePDFTest
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PdfTextPageKtTest  : BasePDFTest() {

    private lateinit var pdfDocument: PdfDocumentKt
    private var pdfBytes: ByteArray? = null

    @Before
    fun setUp() = runBlocking {
        pdfBytes = getPdfBytes("f01.pdf")

        TestCase.assertNotNull(pdfBytes)

        pdfDocument = PdfiumCoreKt(Dispatchers.Unconfined).newDocument(pdfBytes)
    }

    @After
    fun tearDown() {
        pdfDocument.close()
    }

    @Test
    fun textPageCountChars() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val charCount = textPage.textPageCountChars()

            Truth.assertThat(charCount).isEqualTo(3468)
        }
    }

    @Test
    fun textPageGetText() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val text = textPage.textPageGetText(0, 100)

            Truth.assertThat(text?.length).isEqualTo(100)
        }
    }

    @Test
    fun textPageGetUnicode() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val char = textPage.textPageGetUnicode(0)

            Truth.assertThat(char).isEqualTo('T')
        }
    }

    @Test
    fun textPageGetCharBox() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val rect = textPage.textPageGetCharBox(0)

            Truth.assertThat(rect).isEqualTo( RectF(90.314415f, 103.44171f, 699.1206f, 715.3187f))
        }
    }

    @Test
    fun textPageGetCharIndexAtPos() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val characterToLookup = 1
            val rect = textPage.textPageGetCharBox(characterToLookup)

            val pos = textPage.textPageGetCharIndexAtPos(
                rect?.centerX()?.toDouble() ?: 0.0,
                rect?.centerY()?.toDouble() ?: 0.0,
                1.0, // Shouldn't need much since we're in the middle of the rect
                1.0
            )

            Truth.assertThat(pos).isEqualTo(characterToLookup)
        }
    }

    @Test
    fun textPageCountRects() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val rectCount = textPage.textPageCountRects(0, 100)

            Truth.assertThat(rectCount).isEqualTo( 4)
        }
    }

    @Test
    fun textPageGetRect() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val rect = textPage.textPageGetRect(0)

            Truth.assertThat(rect).isEqualTo( RectF(0f, 0f, 0f, 0f))
        }
    }

    @Test
    fun textPageGetBoundedText() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->
            val text = textPage.textPageGetBoundedText(RectF(0f, 97f, 100f, 100f), 100)

            Truth.assertThat(text).isEqualTo( "Do")
        }
    }

    @Test(expected = IllegalStateException::class)
    fun close() = runTest {
        var pageAfterClose: PdfTextPageKt?
        pdfDocument.openTextPage(0).use { textPage ->
            pageAfterClose = textPage
        }
        pageAfterClose!!.textPageCountChars()
    }

    @Test
    fun getPage() = runTest {
        pdfDocument.openTextPage(0).use { textPage ->

            Truth.assertThat(textPage.page).isNotNull()
        }
    }
}