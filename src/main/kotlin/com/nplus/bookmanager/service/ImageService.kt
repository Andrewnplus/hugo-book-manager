package com.nplus.bookmanager.service

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
            println("  Saved to: ${outputFile.absolutePath}")

            true
        } catch (e: Exception) {
            println("  Error processing image: ${e.message}")
            false
        }
    }

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
