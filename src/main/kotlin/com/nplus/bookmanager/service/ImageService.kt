package com.nplus.bookmanager.service

import com.nplus.bookmanager.util.ProcessRunner
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

class ImageService : CoverImageFetcher {
    companion object {
        const val TARGET_WIDTH = 500

        const val PALETTE_COLOURS = 256
    }

    override fun downloadAndResize(
        imageUrl: String,
        outputFile: File,
    ): Boolean {
        return try {
            println("  Downloading image from: $imageUrl")

            val url = URI(imageUrl).toURL()
            val originalImage = ImageIO.read(url)

            if (originalImage == null) {
                println("  Error: Could not read image from URL")
                return false
            }

            println("  Original size: ${originalImage.width}x${originalImage.height}")

            val aspectRatio = originalImage.height.toDouble() / originalImage.width.toDouble()
            val targetHeight = (TARGET_WIDTH * aspectRatio).toInt()

            println("  Resizing to: ${TARGET_WIDTH}x$targetHeight")

            val resizedImage = resizeImage(originalImage, TARGET_WIDTH, targetHeight)

            outputFile.parentFile?.mkdirs()

            ImageIO.write(resizedImage, "png", outputFile)

            quantise(outputFile)

            println("  Saved to: ${outputFile.absolutePath} (${outputFile.length() / 1024}KB)")

            true
        } catch (e: Exception) {
            println("  Error processing image: ${e.message}")
            false
        }
    }

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
