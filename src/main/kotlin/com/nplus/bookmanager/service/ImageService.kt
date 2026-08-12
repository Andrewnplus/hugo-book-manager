package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

/**
 * Service for downloading and processing cover images
 */
class ImageService : CoverImageFetcher {
    companion object {
        const val TARGET_WIDTH = 500

        /**
         * Book covers are opaque photographs of print artwork, so the truecolour
         * ARGB PNG that ImageIO writes is roughly three times larger than it needs
         * to be (a 500px cover lands around 300KB). Quantising to a 256-colour
         * palette is visually indistinguishable at display size and cuts that by
         * ~70%, which matters because every book repo carries one of these.
         */
        const val PALETTE_COLOURS = 256
    }

    /**
     * Download an image from URL, resize to target width, and save as PNG
     *
     * @param imageUrl URL of the image to download
     * @param outputFile Target file to save (will be PNG format)
     * @return true if successful
     */
    override fun downloadAndResize(
        imageUrl: String,
        outputFile: File,
    ): Boolean {
        return try {
            println("  Downloading image from: $imageUrl")

            // Download image
            val url = URI(imageUrl).toURL()
            val originalImage = ImageIO.read(url)

            if (originalImage == null) {
                println("  Error: Could not read image from URL")
                return false
            }

            println("  Original size: ${originalImage.width}x${originalImage.height}")

            // Calculate new dimensions maintaining aspect ratio
            val aspectRatio = originalImage.height.toDouble() / originalImage.width.toDouble()
            val targetHeight = (TARGET_WIDTH * aspectRatio).toInt()

            println("  Resizing to: ${TARGET_WIDTH}x$targetHeight")

            // Resize image
            val resizedImage = resizeImage(originalImage, TARGET_WIDTH, targetHeight)

            // Ensure parent directory exists
            outputFile.parentFile?.mkdirs()

            // Save as PNG
            ImageIO.write(resizedImage, "png", outputFile)

            quantise(outputFile)

            println("  Saved to: ${outputFile.absolutePath} (${outputFile.length() / 1024}KB)")

            true
        } catch (e: Exception) {
            println("  Error processing image: ${e.message}")
            false
        }
    }

    /**
     * Rewrite the PNG in place as a palette image via ImageMagick.
     *
     * ImageIO cannot write indexed-colour PNG, so this shells out. ImageMagick is
     * optional: when it is missing, or the quantised file would not actually be
     * smaller, the truecolour PNG already written is kept and the caller sees no
     * difference beyond file size.
     */
    private fun quantise(file: File) {
        if (!ProcessRunner.executeSuccessfully("command -v convert", timeoutSeconds = 10)) {
            println("  (ImageMagick not found - keeping the unquantised PNG)")
            return
        }

        val tmp = File(file.parentFile, "${file.name}.palette.png")
        val ok =
            ProcessRunner.executeSuccessfully(
                "convert ${shellQuote(file.path)} -alpha off -strip " +
                    "-colors $PALETTE_COLOURS -depth 8 PNG8:${shellQuote(tmp.path)}",
                timeoutSeconds = 60,
            )

        if (ok && tmp.length() in 1 until file.length()) {
            tmp.copyTo(file, overwrite = true)
        }
        tmp.delete()
    }

    private fun shellQuote(path: String): String = "'" + path.replace("'", "'\\''") + "'"

    private fun resizeImage(
        originalImage: BufferedImage,
        targetWidth: Int,
        targetHeight: Int,
    ): BufferedImage {
        val scaledImage =
            originalImage.getScaledInstance(
                targetWidth,
                targetHeight,
                Image.SCALE_SMOOTH,
            )

        val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = outputImage.createGraphics()
        graphics.drawImage(scaledImage, 0, 0, null)
        graphics.dispose()

        return outputImage
    }
}
