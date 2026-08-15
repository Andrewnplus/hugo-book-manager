package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import org.junit.jupiter.api.io.TempDir
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageServiceTest {
    @TempDir
    lateinit var root: File

    private val service = ImageService()

    private val hasImageMagick by lazy {
        ProcessRunner.executeSuccessfully("command -v convert", timeoutSeconds = 10)
    }

    /**
     * A many-coloured source, so quantising to 256 colours is a real reduction
     * rather than a no-op on a handful of flat blocks.
     */
    private fun sourcePng(
        name: String,
        width: Int,
        height: Int,
    ): File {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = (x * 255 / width)
                val g = (y * 255 / height)
                val b = ((x + y) * 255 / (width + height))
                image.setRGB(x, y, (r shl 16) or (g shl 8) or b)
            }
        }
        return File(root, name).also { ImageIO.write(image, "png", it) }
    }

    @Test
    fun `cover is written at the target width with the aspect ratio preserved`() {
        val source = sourcePng("source.png", width = 1650, height = 2560)
        val out = File(root, "repo/site/content/cover.png")

        assertTrue(service.downloadAndResize(source.toURI().toString(), out))

        val written = ImageIO.read(out)
        assertEquals(ImageService.TARGET_WIDTH, written.width)
        assertEquals(775, written.height, "500 * (2560/1650) truncates to 775")
    }

    @Test
    fun `missing parent directories are created`() {
        val source = sourcePng("source.png", width = 800, height = 1000)
        val out = File(root, "brand/new/nested/cover.png")
        assertFalse(out.parentFile.exists())

        assertTrue(service.downloadAndResize(source.toURI().toString(), out))
        assertTrue(out.isFile)
    }

    @Test
    fun `an unreadable source is reported instead of throwing`() {
        val notAnImage = File(root, "broken.png").apply { writeText("this is not a PNG") }
        val out = File(root, "cover.png")

        assertFalse(service.downloadAndResize(notAnImage.toURI().toString(), out))
        assertFalse(out.exists(), "a failed download must not leave a half-written cover behind")
    }

    @Test
    fun `a nonexistent url is reported instead of throwing`() {
        val out = File(root, "cover.png")
        assertFalse(service.downloadAndResize(File(root, "gone.png").toURI().toString(), out))
    }

    @Test
    fun `the saved cover is a palette png when ImageMagick is available`() {
        if (!hasImageMagick) return

        val source = sourcePng("source.png", width = 1650, height = 2560)
        val out = File(root, "cover.png")

        assertTrue(service.downloadAndResize(source.toURI().toString(), out))

        val model = ImageIO.read(out).colorModel
        assertTrue(
            model is IndexColorModel,
            "covers ship to ~1700 repos; the truecolour PNG must be quantised away (got ${model::class.simpleName})",
        )
        assertTrue(
            (model as IndexColorModel).mapSize <= ImageService.PALETTE_COLOURS,
            "palette must stay within ${ImageService.PALETTE_COLOURS} colours",
        )
    }
}
