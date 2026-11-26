package com.panomc.plugins.announcement.util

import io.vertx.core.json.JsonObject

/**
 * How a banner looks and moves: marquee speed, how several contents are shown, the background and the text colour.
 * Stored as one JSON text column (`displayOptions`), so a new option needs no table change. The panel sends any JSON;
 * [normalize] keeps only the known keys with valid values and drops the rest, so the site only ever reads clean data.
 */
object AnnouncementDisplayOptions {
    private val MULTI_MODES = setOf("ROTATE", "INLINE")
    private val SEPARATORS = setOf("DOT", "SPARKLE", "STAR", "DIAMOND", "PIPE", "DASH", "ARROW", "CUSTOM")
    private val BACKGROUNDS = setOf("ALERT", "THEME_SOFT", "THEME_SOLID", "THEME_GRADIENT", "GRADIENT", "CUSTOM")
    private val GRADIENTS = setOf("SUNSET", "OCEAN", "BERRY", "FOREST", "CORAL", "NIGHT", "AURORA", "CUSTOM")
    private val TEXT_COLORS = setOf("AUTO", "CUSTOM")
    private val HEX = Regex("^#[0-9a-fA-F]{6}$")

    const val MAX_SEPARATOR_LENGTH = 8

    /** The canonical JSON text for [raw]; an empty object for nothing or invalid JSON (every option has a default). */
    fun normalize(raw: String?): String {
        val input = try {
            if (raw.isNullOrBlank()) JsonObject() else JsonObject(raw)
        } catch (e: Exception) {
            JsonObject()
        }

        val out = JsonObject()

        input.intIn("marqueeSpeed", 10..300)?.let { out.put("marqueeSpeed", it) }
        input.intIn("rotateSeconds", 2..60)?.let { out.put("rotateSeconds", it) }
        input.oneOf("multiMode", MULTI_MODES)?.let { out.put("multiMode", it) }
        input.oneOf("separator", SEPARATORS)?.let { out.put("separator", it) }
        input.getValue("customSeparator")?.let { it as? String }
            ?.let { sanitizeSeparator(it) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { out.put("customSeparator", it) }
        input.oneOf("background", BACKGROUNDS)?.let { out.put("background", it) }
        input.oneOf("gradient", GRADIENTS)?.let { out.put("gradient", it) }
        input.hex("gradientFrom")?.let { out.put("gradientFrom", it) }
        input.hex("gradientTo")?.let { out.put("gradientTo", it) }
        input.intIn("gradientAngle", 0..360)?.let { out.put("gradientAngle", it) }
        input.hex("backgroundColor")?.let { out.put("backgroundColor", it) }
        input.oneOf("textColor", TEXT_COLORS)?.let { out.put("textColor", it) }
        input.hex("textColorValue")?.let { out.put("textColorValue", it) }

        return out.encode()
    }

    /** Plain text only: no markup characters, at most [MAX_SEPARATOR_LENGTH] characters (code points). */
    fun sanitizeSeparator(value: String): String {
        val cleaned = value.filterNot { it in "<>&\"'`\\" || it.isISOControl() }.trim()
        val count = cleaned.codePointCount(0, cleaned.length)

        return if (count <= MAX_SEPARATOR_LENGTH) cleaned
        else cleaned.substring(0, cleaned.offsetByCodePoints(0, MAX_SEPARATOR_LENGTH))
    }

    private fun JsonObject.intIn(key: String, range: IntRange): Int? =
        (getValue(key) as? Number)?.toDouble()
            ?.takeIf { it.isFinite() }
            ?.toInt()
            ?.coerceIn(range)

    private fun JsonObject.oneOf(key: String, allowed: Set<String>): String? =
        (getValue(key) as? String)?.takeIf { it in allowed }

    private fun JsonObject.hex(key: String): String? =
        (getValue(key) as? String)?.takeIf { HEX.matches(it) }?.lowercase()
}
