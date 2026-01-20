package com.nplus.bookmanager.service

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Service for cleaning MHTML/HTML/PDF documents and converting to Markdown.
 *
 * Handles:
 * - PDF text extraction with Apache PDFBox
 * - MHTML format parsing (multipart MIME)
 * - Quoted-printable decoding
 * - HTML parsing with Jsoup
 * - Slate.js structure to Markdown conversion
 */
class DocumentCleanerService {
    companion object {
        // Slate.js data attributes
        private const val SLATE_TYPE = "data-slate-type"
        private const val SLATE_OBJECT = "data-slate-object"

        // Supported input formats
        val SUPPORTED_EXTENSIONS = listOf(".pdf", ".mhtml", ".html", ".htm")
    }

    /**
     * Clean a single document file and return Markdown content.
     */
    fun cleanDocument(inputFile: File): String? {
        if (!inputFile.exists()) {
            println("Error: File not found: ${inputFile.absolutePath}")
            return null
        }

        return when (inputFile.extension.lowercase()) {
            "pdf" -> cleanPdf(inputFile)
            "mhtml" -> {
                val content = inputFile.readText(StandardCharsets.UTF_8)
                cleanMhtml(content)
            }
            else -> {
                val content = inputFile.readText(StandardCharsets.UTF_8)
                cleanHtml(content)
            }
        }
    }

    /**
     * Extract text from PDF and convert to Markdown format.
     */
    private fun cleanPdf(inputFile: File): String? =
        try {
            Loader.loadPDF(inputFile).use { document ->
                val stripper = PDFTextStripper()
                val rawText = stripper.getText(document)

                // Process the extracted text into clean Markdown
                formatPdfTextAsMarkdown(rawText, document.numberOfPages)
            }
        } catch (e: Exception) {
            println("Error reading PDF: ${e.message}")
            null
        }

    /**
     * Format extracted PDF text as Markdown.
     *
     * PDF text extraction often has issues like:
     * - Inconsistent line breaks
     * - Missing paragraph boundaries
     * - Headers mixed with body text
     *
     * This method attempts to clean up and structure the text.
     */
    private fun formatPdfTextAsMarkdown(
        rawText: String,
        pageCount: Int,
    ): String {
        val result = StringBuilder()

        // Normalize line endings
        val text = rawText.replace("\r\n", "\n").replace("\r", "\n")

        // Split into lines and process
        val lines = text.split("\n")
        var inParagraph = false
        val paragraphBuffer = StringBuilder()

        for (line in lines) {
            val trimmedLine = line.trim()

            // Skip empty lines - they mark paragraph boundaries
            if (trimmedLine.isEmpty()) {
                if (inParagraph && paragraphBuffer.isNotBlank()) {
                    result.append(cleanParagraph(paragraphBuffer.toString()))
                    result.append("\n\n")
                    paragraphBuffer.clear()
                }
                inParagraph = false
                continue
            }

            // Detect potential headings (short lines, possibly numbered)
            if (isLikelyHeading(trimmedLine, lines)) {
                // Flush any pending paragraph
                if (paragraphBuffer.isNotBlank()) {
                    result.append(cleanParagraph(paragraphBuffer.toString()))
                    result.append("\n\n")
                    paragraphBuffer.clear()
                }
                inParagraph = false

                // Format as heading
                val headingLevel = detectHeadingLevel(trimmedLine)
                val headingPrefix = "#".repeat(headingLevel)
                val cleanedHeading = cleanHeadingText(trimmedLine)
                result.append("$headingPrefix $cleanedHeading\n\n")
                continue
            }

            // Detect list items
            if (isListItem(trimmedLine)) {
                // Flush any pending paragraph
                if (paragraphBuffer.isNotBlank()) {
                    result.append(cleanParagraph(paragraphBuffer.toString()))
                    result.append("\n\n")
                    paragraphBuffer.clear()
                }
                inParagraph = false

                result.append(formatListItem(trimmedLine))
                result.append("\n")
                continue
            }

            // Regular text - accumulate into paragraph
            if (paragraphBuffer.isNotBlank()) {
                // Check if this continues the previous line or is a new sentence
                val lastChar = paragraphBuffer.last()
                if (lastChar in listOf('.', '!', '?', '。', '！', '？', '：', ':')) {
                    paragraphBuffer.append(" ")
                } else if (lastChar.isLetterOrDigit() || lastChar in listOf(',', '，', '、')) {
                    // Continue the same sentence
                    paragraphBuffer.append(" ")
                }
            }
            paragraphBuffer.append(trimmedLine)
            inParagraph = true
        }

        // Flush remaining paragraph
        if (paragraphBuffer.isNotBlank()) {
            result.append(cleanParagraph(paragraphBuffer.toString()))
            result.append("\n")
        }

        return result
            .toString()
            .replace(Regex("\n{3,}"), "\n\n") // Max 2 newlines
            .trim()
    }

    /**
     * Check if a line is likely a heading.
     */
    private fun isLikelyHeading(
        line: String,
        allLines: List<String>,
    ): Boolean {
        // Too long to be a heading
        if (line.length > 100) return false

        // Numbered chapter/section patterns (Chinese and English)
        val chapterPatterns =
            listOf(
                Regex("^第[一二三四五六七八九十百千0-9]+[章节部篇]"),
                Regex("^[0-9]+[.、][0-9]*\\s*[^0-9]"),
                Regex("^Chapter\\s+[0-9]+", RegexOption.IGNORE_CASE),
                Regex("^Section\\s+[0-9]+", RegexOption.IGNORE_CASE),
                Regex("^Part\\s+[0-9]+", RegexOption.IGNORE_CASE),
                Regex("^[IVX]+[.、]\\s*"),
                Regex("^[一二三四五六七八九十]+[、.]\\s*"),
            )

        for (pattern in chapterPatterns) {
            if (pattern.containsMatchIn(line)) return true
        }

        // Short lines that end without punctuation might be headings
        if (line.length < 50 &&
            !line.endsWith(".") &&
            !line.endsWith("。") &&
            !line.endsWith(",") &&
            !line.endsWith("，") &&
            !line.endsWith("、")
        ) {
            // Check if next non-empty line is longer (suggesting this is a heading)
            // This is a heuristic and won't always be correct
            return true
        }

        return false
    }

    /**
     * Detect the heading level based on content patterns.
     */
    private fun detectHeadingLevel(line: String): Int {
        // Main chapter headings
        if (Regex("^第[一二三四五六七八九十百千0-9]+[章部]").containsMatchIn(line)) return 2
        if (Regex("^Chapter\\s+[0-9]+", RegexOption.IGNORE_CASE).containsMatchIn(line)) return 2
        if (Regex("^Part\\s+[0-9]+", RegexOption.IGNORE_CASE).containsMatchIn(line)) return 2

        // Section headings
        if (Regex("^第[一二三四五六七八九十0-9]+[节篇]").containsMatchIn(line)) return 3
        if (Regex("^Section\\s+[0-9]+", RegexOption.IGNORE_CASE).containsMatchIn(line)) return 3
        if (Regex("^[0-9]+[.、][0-9]+").containsMatchIn(line)) return 3

        // Sub-section
        if (Regex("^[0-9]+[.、]").containsMatchIn(line)) return 3
        if (Regex("^[一二三四五六七八九十]+[、.]").containsMatchIn(line)) return 3

        // Default to H3 for other short lines that look like headings
        return 3
    }

    /**
     * Clean up heading text (remove numbering if already using Markdown heading).
     */
    private fun cleanHeadingText(line: String): String {
        // Keep the original text for now - numbering can be useful
        return line.trim()
    }

    /**
     * Check if a line is a list item.
     */
    private fun isListItem(line: String): Boolean {
        val listPatterns =
            listOf(
                Regex("^[•·●○◆◇▪▫-]\\s+"),
                Regex("^[0-9]+[.)）]\\s+"),
                Regex("^[a-zA-Z][.)）]\\s+"),
                Regex("^[(（][0-9]+[)）]\\s+"),
            )

        return listPatterns.any { it.containsMatchIn(line) }
    }

    /**
     * Format a list item for Markdown.
     */
    private fun formatListItem(line: String): String {
        // Convert various bullet styles to Markdown
        var formatted =
            line
                .replace(Regex("^[•·●○◆◇▪▫]\\s+"), "- ")
                .replace(Regex("^[0-9]+[.)）]\\s+"), "- ")
                .replace(Regex("^[a-zA-Z][.)）]\\s+"), "- ")
                .replace(Regex("^[(（][0-9]+[)）]\\s+"), "- ")

        // If it still has a leading dash/hyphen, normalize it
        if (formatted.startsWith("-") && !formatted.startsWith("- ")) {
            formatted = "- " + formatted.substring(1).trim()
        }

        return formatted
    }

    /**
     * Clean up a paragraph of text.
     */
    private fun cleanParagraph(text: String): String =
        text
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()

    /**
     * Parse MHTML format and extract HTML content, then clean it.
     */
    private fun cleanMhtml(content: String): String? {
        // Normalize line endings to \n
        val normalizedContent = content.replace("\r\n", "\n").replace("\r", "\n")

        // Find the boundary from Content-Type header
        // Format: boundary="----MultipartBoundary--xxx----"
        val boundaryMatch =
            Regex("boundary=\"([^\"]+)\"").find(normalizedContent)
                ?: Regex("boundary=([^\\s;]+)").find(normalizedContent)

        val boundary =
            boundaryMatch?.groupValues?.get(1)
                ?: return cleanHtml(normalizedContent) // Fallback: try as plain HTML

        // In MHTML, parts are separated by "--" + boundary
        // Split by the actual separator (which has -- prefix)
        val separator = "--$boundary"
        val parts =
            normalizedContent
                .split(separator)
                .filter { it.isNotBlank() && !it.trim().startsWith("--") }

        // Find the main HTML part
        for ((partIndex, part) in parts.withIndex()) {
            // Check for text/html content type in the part headers
            // Skip leading empty lines from the boundary
            val lines = part.split("\n").dropWhile { it.trim().isEmpty() }

            var isHtmlPart = false
            var isQuotedPrintable = false
            var headerEndLine = 0
            var foundHeaderEnd = false

            for ((index, line) in lines.withIndex()) {
                val trimmedLine = line.trim()
                // Empty line marks end of headers (but skip if we haven't seen any headers yet)
                if (trimmedLine.isEmpty() && index > 0) {
                    headerEndLine = index
                    foundHeaderEnd = true
                    break
                }
                if (trimmedLine.contains("Content-Type:", ignoreCase = true) &&
                    trimmedLine.contains("text/html", ignoreCase = true)
                ) {
                    isHtmlPart = true
                }
                if (trimmedLine.contains("Content-Transfer-Encoding:", ignoreCase = true) &&
                    trimmedLine.contains("quoted-printable", ignoreCase = true)
                ) {
                    isQuotedPrintable = true
                }
            }

            if (isHtmlPart && foundHeaderEnd) {
                // Extract body after headers
                val body = lines.drop(headerEndLine + 1).joinToString("\n")

                val htmlContent =
                    if (isQuotedPrintable) {
                        decodeQuotedPrintable(body)
                    } else {
                        body
                    }

                return cleanHtml(htmlContent)
            }
        }

        println("Warning: Could not find HTML content in MHTML file")
        return null
    }

    /**
     * Extract content from a MIME part, handling encoding.
     */
    private fun extractMimePart(part: String): String? {
        // Split headers from body (double newline)
        val headerEndIndex = part.indexOf("\n\n")
        if (headerEndIndex == -1) return null

        val headers = part.substring(0, headerEndIndex)
        val body = part.substring(headerEndIndex + 2)

        // Check Content-Transfer-Encoding
        val isQuotedPrintable = headers.contains("quoted-printable", ignoreCase = true)

        return if (isQuotedPrintable) {
            decodeQuotedPrintable(body)
        } else {
            body
        }
    }

    /**
     * Decode quoted-printable encoded content.
     */
    private fun decodeQuotedPrintable(encoded: String): String {
        val result = StringBuilder()
        var i = 0

        // Remove soft line breaks first (=\r\n or =\n)
        val cleaned =
            encoded
                .replace("=\r\n", "")
                .replace("=\n", "")

        while (i < cleaned.length) {
            val char = cleaned[i]
            if (char == '=' && i + 2 < cleaned.length) {
                val hex = cleaned.substring(i + 1, i + 3)
                try {
                    if (hex.all { it.isDigit() || it.uppercaseChar() in 'A'..'F' }) {
                        result.append(hex.toInt(16).toChar())
                        i += 3
                        continue
                    }
                } catch (_: NumberFormatException) {
                    // Not valid hex, keep as is
                }
            }
            result.append(char)
            i++
        }

        // Handle UTF-8 byte sequences
        return try {
            // The decoded result might have raw UTF-8 bytes as chars
            // We need to convert them back properly
            val bytes = result.toString().toByteArray(Charsets.ISO_8859_1)
            String(bytes, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            result.toString()
        }
    }

    /**
     * Clean HTML content and convert to Markdown.
     */
    private fun cleanHtml(html: String): String {
        val doc = Jsoup.parse(html)

        // Find article content - try multiple strategies
        val articleContent = findArticleContent(doc)
        if (articleContent == null) {
            println("Warning: Could not find article content, using body")
            return convertToMarkdown(doc.body())
        }

        return convertToMarkdown(articleContent)
    }

    /**
     * Find the main article content in the document.
     */
    private fun findArticleContent(doc: Document): Element? {
        // Strategy 1: Find Slate.js editor content
        val slateEditor = doc.selectFirst("[data-slate-editor]")
        if (slateEditor != null) return slateEditor

        // Strategy 2: Common article content selectors
        val selectors =
            listOf(
                "article",
                "[class*='article-content']",
                "[class*='content']",
                ".post-content",
                ".entry-content",
                "main",
            )

        for (selector in selectors) {
            val element = doc.selectFirst(selector)
            if (element != null && element.text().length > 100) {
                return element
            }
        }

        return null
    }

    /**
     * Convert HTML element to Markdown format.
     */
    private fun convertToMarkdown(element: Element): String {
        val result = StringBuilder()

        // Process children
        for (child in element.childNodes()) {
            val markdown = nodeToMarkdown(child)
            if (markdown.isNotBlank()) {
                result.append(markdown)
            }
        }

        return result
            .toString()
            .replace(Regex("\n{3,}"), "\n\n") // Max 2 newlines
            .trim()
    }

    /**
     * Convert a single node to Markdown.
     */
    private fun nodeToMarkdown(node: Node): String =
        when (node) {
            is TextNode -> {
                val text = node.text().trim()
                if (text.isNotBlank()) text else ""
            }
            is Element -> elementToMarkdown(node)
            else -> ""
        }

    /**
     * Convert an HTML element to Markdown.
     */
    private fun elementToMarkdown(element: Element): String {
        val tagName = element.tagName().lowercase()
        val slateType = element.attr(SLATE_TYPE)

        // Handle Slate.js types
        if (slateType.isNotBlank()) {
            return slateTypeToMarkdown(element, slateType)
        }

        // Handle standard HTML tags
        return when (tagName) {
            "h1" -> "\n# ${getInnerText(element)}\n\n"
            "h2" -> "\n## ${getInnerText(element)}\n\n"
            "h3" -> "\n### ${getInnerText(element)}\n\n"
            "h4" -> "\n#### ${getInnerText(element)}\n\n"
            "h5" -> "\n##### ${getInnerText(element)}\n\n"
            "h6" -> "\n###### ${getInnerText(element)}\n\n"
            "p" -> "\n${getInnerText(element)}\n"
            "div" -> processDiv(element)
            "span" -> processSpan(element)
            "strong", "b" -> "**${getInnerText(element)}**"
            "em", "i" -> "*${getInnerText(element)}*"
            "code" -> "`${getInnerText(element)}`"
            "pre" -> "\n```\n${getInnerText(element)}\n```\n"
            "ul" -> processUnorderedList(element)
            "ol" -> processOrderedList(element)
            "li" -> getInnerText(element)
            "a" -> processLink(element)
            "blockquote" -> processBlockquote(element)
            "br" -> "\n"
            "img" -> processImage(element)
            "script", "style", "nav", "header", "footer", "aside" -> "" // Skip
            else -> processChildren(element)
        }
    }

    /**
     * Handle Slate.js specific types.
     */
    private fun slateTypeToMarkdown(
        element: Element,
        slateType: String,
    ): String =
        when (slateType) {
            "paragraph" -> "\n${processChildren(element)}\n"
            "heading-one" -> "\n# ${getInnerText(element)}\n\n"
            "heading-two" -> "\n## ${getInnerText(element)}\n\n"
            "heading-three" -> "\n### ${getInnerText(element)}\n\n"
            "bold" -> "**${getInnerText(element)}**"
            "italic" -> "*${getInnerText(element)}*"
            "code" -> "`${getInnerText(element)}`"
            "code-block" -> "\n```\n${getInnerText(element)}\n```\n"
            "block-quote" -> processBlockquote(element)
            "bulleted-list" -> processUnorderedList(element)
            "numbered-list" -> processOrderedList(element)
            "list-item" -> getInnerText(element)
            "link" -> processLink(element)
            "image" -> processImage(element)
            else -> processChildren(element)
        }

    /**
     * Process div element - check for special types.
     */
    private fun processDiv(element: Element): String {
        val slateType = element.attr(SLATE_TYPE)
        if (slateType.isNotBlank()) {
            return slateTypeToMarkdown(element, slateType)
        }

        // Check if it's a container or has content
        val children =
            element.childNodes().filter {
                (it is TextNode && it.text().isNotBlank()) || it is Element
            }

        return if (children.isEmpty()) {
            ""
        } else {
            processChildren(element) + "\n"
        }
    }

    /**
     * Process span element - check for marks (bold, italic, etc.).
     */
    private fun processSpan(element: Element): String {
        val slateType = element.attr(SLATE_TYPE)

        return when (slateType) {
            "bold" -> "**${getInnerText(element)}**"
            "italic" -> "*${getInnerText(element)}*"
            "code" -> "`${getInnerText(element)}`"
            else -> processChildren(element)
        }
    }

    /**
     * Process unordered list.
     */
    private fun processUnorderedList(element: Element): String {
        val result = StringBuilder("\n")
        for (li in element.select("> li, > [${SLATE_TYPE}='list-item']")) {
            result.append("- ${getInnerText(li)}\n")
        }
        result.append("\n")
        return result.toString()
    }

    /**
     * Process ordered list.
     */
    private fun processOrderedList(element: Element): String {
        val result = StringBuilder("\n")
        var index = 1
        for (li in element.select("> li, > [${SLATE_TYPE}='list-item']")) {
            result.append("$index. ${getInnerText(li)}\n")
            index++
        }
        result.append("\n")
        return result.toString()
    }

    /**
     * Process link element.
     */
    private fun processLink(element: Element): String {
        val text = getInnerText(element)
        val href = element.attr("href")
        return if (href.isNotBlank()) {
            "[$text]($href)"
        } else {
            text
        }
    }

    /**
     * Process blockquote element.
     */
    private fun processBlockquote(element: Element): String {
        val lines = getInnerText(element).split("\n")
        val quoted = lines.joinToString("\n") { "> $it" }
        return "\n$quoted\n\n"
    }

    /**
     * Process image element.
     */
    private fun processImage(element: Element): String {
        val alt = element.attr("alt").ifBlank { "image" }
        val src = element.attr("src")
        return if (src.isNotBlank()) {
            "\n![$alt]($src)\n"
        } else {
            ""
        }
    }

    /**
     * Process all children of an element.
     */
    private fun processChildren(element: Element): String {
        val result = StringBuilder()
        for (child in element.childNodes()) {
            result.append(nodeToMarkdown(child))
        }
        return result.toString()
    }

    /**
     * Get all inner text from an element, preserving inline formatting.
     */
    private fun getInnerText(element: Element): String {
        val result = StringBuilder()

        for (child in element.childNodes()) {
            when (child) {
                is TextNode -> {
                    result.append(child.text())
                }
                is Element -> {
                    val tagName = child.tagName().lowercase()
                    val slateType = child.attr(SLATE_TYPE)

                    val text =
                        when {
                            slateType == "bold" || tagName in listOf("strong", "b") ->
                                "**${getInnerText(child)}**"
                            slateType == "italic" || tagName in listOf("em", "i") ->
                                "*${getInnerText(child)}*"
                            slateType == "code" || tagName == "code" ->
                                "`${getInnerText(child)}`"
                            tagName == "br" -> "\n"
                            else -> getInnerText(child)
                        }
                    result.append(text)
                }
            }
        }

        return result.toString()
    }

    /**
     * Clean all documents in a directory.
     *
     * @param inputDir Input directory containing HTML/MHTML files
     * @param outputDir Output directory for cleaned Markdown files
     * @param dryRun If true, don't write files, just print what would be done
     * @return Number of files processed
     */
    fun cleanDirectory(
        inputDir: File,
        outputDir: File,
        dryRun: Boolean = false,
    ): Int {
        if (!inputDir.exists() || !inputDir.isDirectory) {
            println("Error: Input directory not found: ${inputDir.absolutePath}")
            return 0
        }

        val files =
            inputDir.listFiles()?.filter { file ->
                SUPPORTED_EXTENSIONS.any { ext ->
                    file.name.lowercase().endsWith(ext)
                }
            } ?: emptyList()

        if (files.isEmpty()) {
            println("No supported files found in: ${inputDir.absolutePath}")
            println("Supported formats: ${SUPPORTED_EXTENSIONS.joinToString(", ")}")
            return 0
        }

        if (!dryRun && !outputDir.exists()) {
            outputDir.mkdirs()
        }

        var processed = 0
        for (file in files.sortedBy { it.name }) {
            val outputName = file.nameWithoutExtension + ".md"
            val outputFile = File(outputDir, outputName)

            println("Processing: ${file.name}")

            if (dryRun) {
                println("  -> Would write to: ${outputFile.absolutePath}")
                processed++
                continue
            }

            try {
                val markdown = cleanDocument(file)
                if (markdown != null) {
                    outputFile.writeText(markdown, StandardCharsets.UTF_8)
                    println("  -> Created: ${outputFile.name}")
                    processed++
                } else {
                    println("  -> Error: Failed to extract content")
                }
            } catch (e: IOException) {
                println("  -> Error: ${e.message}")
            }
        }

        return processed
    }
}
