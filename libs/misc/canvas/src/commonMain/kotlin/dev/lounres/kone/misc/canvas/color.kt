/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.angles.inRadians
import dev.lounres.kone.computationalGeometry.angles.radians
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.abs
import kotlin.text.toString
import kotlin.toUInt


@Serializable
@JvmInline
public value class KoneColor internal constructor(
    // RGBA
    internal val value: UInt,
) {
    public companion object;
}
public val KoneColor.alpha: Float get() = ((value shr 0) and 0xFFu).toFloat() / 255.0f
public val KoneColor.alphaUByte: UByte get() = ((value shr 0) and 0xFFu).toUByte()

public fun KoneColor.Companion.fromRgbaUInt(value: UInt): KoneColor = KoneColor(value)
public fun KoneColor.Companion.fromRgba(red: Float, green: Float, blue: Float, alpha: Float = 1f): KoneColor =
    KoneColor(((red * 255f).toUInt() shl 24) + ((green * 255f).toUInt() shl 16) + ((blue * 255f).toUInt() shl 8) + ((alpha * 255f).toUInt() shl 0))
public fun KoneColor.toRgbaUInt(): UInt = value

public fun KoneColor.Companion.fromArgbUInt(value: UInt): KoneColor = KoneColor(((value and 0xFFFFFFu) shl 8) + ((value shr 24) and 0xFFu))
public fun KoneColor.Companion.fromArgb(alpha: Float = 1f, red: Float, green: Float, blue: Float): KoneColor =
    KoneColor(((red * 255f).toUInt() shl 24) + ((green * 255f).toUInt() shl 16) + ((blue * 255f).toUInt() shl 8) + ((alpha * 255f).toUInt() shl 0))
public fun KoneColor.toArgbUInt(): UInt = ((value shr 8) and 0xFFFFFFu) + ((value and 0xFFu) shl 24)

public val KoneColor.rgbRed: Float get() = ((value shr 24) and 0xFFu).toFloat() / 255.0f
public val KoneColor.rgbRedUByte: UByte get() = ((value shr 24) and 0xFFu).toUByte()
public val KoneColor.rgbGreen: Float get() = ((value shr 16) and 0xFFu).toFloat() / 255.0f
public val KoneColor.rgbGreenUByte: UByte get() = ((value shr 16) and 0xFFu).toUByte()
public val KoneColor.rgbBlue: Float get() = ((value shr 8) and 0xFFu).toFloat() / 255.0f
public val KoneColor.rgbBlueUByte: UByte get() = ((value shr 8) and 0xFFu).toUByte()

// https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB
public fun KoneColor.Companion.fromHslaUInt(value: UInt): KoneColor {
    val hslaUIntValue = value
    
    val hue = ((hslaUIntValue shr 24) and 0xFFu).toFloat() / 255 * 6
    val saturation = ((hslaUIntValue shr 16) and 0xFFu).toFloat() / 255
    val lightness = ((hslaUIntValue shr 8) and 0xFFu).toFloat() / 255
    val alpha = (hslaUIntValue and 0xFFu)
    
    val chroma = saturation * minOf(lightness, 1 - lightness) * 2
    val value = chroma * (1 - abs(hue.mod(2f) - 1))
    val red: Float
    val green: Float
    val blue: Float
    when {
        hue in 0f ..< 1f -> { red = chroma; green = value; blue = 0f }
        hue in 1f ..< 2f -> { red = value; green = chroma; blue = 0f }
        hue in 2f ..< 3f -> { red = 0f; green = chroma; blue = value }
        hue in 3f ..< 4f -> { red = 0f; green = value; blue = chroma }
        hue in 4f ..< 5f -> { red = value; green = 0f; blue = chroma }
        hue in 5f ..< 6f -> { red = chroma; green = 0f; blue = value }
        else -> error("Unexpected situation when calculating RGBA from HSLA (${hslaUIntValue.toString(16).padStart(8, '0')})")
    }
    val m = lightness - chroma / 2
    val finalRed = ((red + m) * 255).toUInt()
    val finalGreen = ((green + m) * 255).toUInt()
    val finalBlue = ((blue + m) * 255).toUInt()
    return KoneColor((finalRed shl 24) + (finalGreen shl 16) + (finalBlue shl 8) + (alpha shl 0))
}
public fun KoneColor.Companion.fromHsla(hue: Angle, saturation: Float, lightness: Float, alpha: Float = 1f): KoneColor {
    val renormedHue = (hue.inRadians() / 2 / PI * 6).toFloat()
    
    val chroma = saturation * minOf(lightness, 1 - lightness) * 2
    val value = chroma * (1 - abs(renormedHue.mod(2f) - 1))
    val red: Float
    val green: Float
    val blue: Float
    when {
        renormedHue in 0f ..< 1f -> { red = chroma; green = value; blue = 0f }
        renormedHue in 1f ..< 2f -> { red = value; green = chroma; blue = 0f }
        renormedHue in 2f ..< 3f -> { red = 0f; green = chroma; blue = value }
        renormedHue in 3f ..< 4f -> { red = 0f; green = value; blue = chroma }
        renormedHue in 4f ..< 5f -> { red = value; green = 0f; blue = chroma }
        renormedHue in 5f ..< 6f -> { red = chroma; green = 0f; blue = value }
        else -> error("Unexpected situation when calculating RGBA from HSLA (hue = $hue radians, saturation = $saturation, lightness = $lightness, alpha = $alpha)")
    }
    val m = lightness - chroma / 2
    val finalRed = ((red + m) * 255).toUInt()
    val finalGreen = ((green + m) * 255).toUInt()
    val finalBlue = ((blue + m) * 255).toUInt()
    return KoneColor((finalRed shl 24) + (finalGreen shl 16) + (finalBlue shl 8) + ((alpha * 255).toUInt() shl 0))
}
// https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB
public fun KoneColor.toHslaUInt(): UInt {
    val red = rgbRed
    val green = rgbGreen
    val blue = rgbBlue
    
    val value = maxOf(red, green, blue)
    val chroma = value - minOf(red, green, blue)
    val lightness = value - chroma / 2
    val hue = when {
        chroma == 0f -> 0f
        value == red -> ((green - blue) / chroma).mod(6f) / 6 * 255
        value == green -> ((blue - red) / chroma + 2f) / 6 * 255
        value == blue -> ((blue - red) / chroma + 2f) / 6 * 255
        else -> error("Unexpected situation when calculating Hue from RGBA (${this.value.toString(16).padStart(8, '0')})")
    }.toUInt()
    val saturation = if (lightness == 0f || lightness == 1f) 0u else ((value - lightness) / minOf(lightness, 1 - lightness) * 255).toUInt()
    return (hue shl 24) + (saturation shl 16) + ((lightness * 255).toUInt() shl 8) + (this.value and 0xFFu)
}

public val KoneColor.hslHue: Angle
    get() {
        val red = rgbRed
        val green = rgbGreen
        val blue = rgbBlue
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        return when {
            chroma == 0f -> 0f
            value == red -> ((green - blue) / chroma).mod(6f) / 6 * 2 * PI
            value == green -> ((blue - red) / chroma + 2f) / 6 * 2 * PI
            value == blue -> ((blue - red) / chroma + 2f) / 6 * 2 * PI
            else -> error("Unexpected situation when calculating Hue from RGBA (${this.value.toString(16).padStart(8, '0')})")
        }.toDouble().radians
    }
public val KoneColor.hslSaturation: Float
    get() {
        val red = rgbRed
        val green = rgbGreen
        val blue = rgbBlue
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        val lightness = value - chroma / 2
        return if (lightness == 0f || lightness == 1f) 0f else (value - lightness) / minOf(lightness, 1 - lightness)
    }
public val KoneColor.hslSaturationUByte: UByte get() = (hslSaturation * 255).toInt().toUByte()
public val KoneColor.hslLightness: Float
    get() {
        val red = rgbRed
        val green = rgbGreen
        val blue = rgbBlue
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        return value - chroma / 2
    }
public val KoneColor.hslLightnessUByte: UByte
    get() {
        val red = rgbRedUByte
        val green = rgbGreenUByte
        val blue = rgbBlueUByte
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        return (value - chroma / 2u).toUByte()
    }

public fun KoneColor.Companion.fromHsvaUInt(value: UInt): KoneColor {
    val hsvaUIntValue = value
    
    val hue = ((hsvaUIntValue shr 24) and 0xFFu).toFloat() / 255 * 6
    val saturation = ((hsvaUIntValue shr 16) and 0xFFu).toFloat() / 255
    val value = ((hsvaUIntValue shr 8) and 0xFFu).toFloat() / 255
    val alpha = (hsvaUIntValue and 0xFFu)
    
    val chroma = value * saturation
    val valueX = chroma * (1 - abs(hue.mod(2f) - 1))
    val red: Float
    val green: Float
    val blue: Float
    when {
        hue in 0f ..< 1f -> { red = chroma; green = valueX; blue = 0f }
        hue in 1f ..< 2f -> { red = valueX; green = chroma; blue = 0f }
        hue in 2f ..< 3f -> { red = 0f; green = chroma; blue = valueX }
        hue in 3f ..< 4f -> { red = 0f; green = valueX; blue = chroma }
        hue in 4f ..< 5f -> { red = valueX; green = 0f; blue = chroma }
        hue in 5f ..< 6f -> { red = chroma; green = 0f; blue = valueX }
        else -> error("Unexpected situation when calculating RGBA from HSLA (${hsvaUIntValue.toString(16).padStart(8, '0')})")
    }
    val m = value - chroma
    val finalRed = ((red + m) * 255).toUInt()
    val finalGreen = ((green + m) * 255).toUInt()
    val finalBlue = ((blue + m) * 255).toUInt()
    return KoneColor((finalRed shl 24) + (finalGreen shl 16) + (finalBlue shl 8) + (alpha shl 0))
}
public fun KoneColor.Companion.fromHsva(hue: Angle, saturation: Float, value: Float, alpha: Float = 1f): KoneColor {
    val renormedHue = (hue.inRadians() / 2 / PI * 6).toFloat()
    
    val chroma = value * saturation
    val valueX = chroma * (1 - abs(renormedHue.mod(2f) - 1))
    val red: Float
    val green: Float
    val blue: Float
    when {
        renormedHue in 0f ..< 1f -> { red = chroma; green = valueX; blue = 0f }
        renormedHue in 1f ..< 2f -> { red = valueX; green = chroma; blue = 0f }
        renormedHue in 2f ..< 3f -> { red = 0f; green = chroma; blue = valueX }
        renormedHue in 3f ..< 4f -> { red = 0f; green = valueX; blue = chroma }
        renormedHue in 4f ..< 5f -> { red = valueX; green = 0f; blue = chroma }
        renormedHue in 5f ..< 6f -> { red = chroma; green = 0f; blue = valueX }
        else -> error("Unexpected situation when calculating RGBA from HSLA (hue = $hue radians, saturation = $saturation, value = $value, alpha = $alpha)")
    }
    val m = value - chroma
    val finalRed = ((red + m) * 255).toUInt()
    val finalGreen = ((green + m) * 255).toUInt()
    val finalBlue = ((blue + m) * 255).toUInt()
    return KoneColor((finalRed shl 24) + (finalGreen shl 16) + (finalBlue shl 8) + ((alpha * 255).toUInt() shl 0))
}
// https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB
public fun KoneColor.toHsvaUInt(): UInt {
    val red = rgbRed
    val green = rgbGreen
    val blue = rgbBlue
    
    val value = maxOf(red, green, blue)
    val chroma = value - minOf(red, green, blue)
    val hue = when {
        chroma == 0f -> 0f
        value == red -> ((green - blue) / chroma).mod(6f) / 6 * 255
        value == green -> ((blue - red) / chroma + 2f) / 6 * 255
        value == blue -> ((blue - red) / chroma + 2f) / 6 * 255
        else -> error("Unexpected situation when calculating Hue from RGBA (${this.value.toString(16).padStart(8, '0')})")
    }.toUInt()
    val saturation = if (value == 0f) 0u else (chroma / value * 255).toUInt()
    return (hue shl 24) + (saturation shl 16) + ((value * 255).toUInt() shl 8) + (this.value and 0xFFu)
}

public val KoneColor.hsvHue: Angle
    get() {
        val red = rgbRed
        val green = rgbGreen
        val blue = rgbBlue
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        return when {
            chroma == 0f -> 0f
            value == red -> ((green - blue) / chroma).mod(6f) / 6 * 2 * PI
            value == green -> ((blue - red) / chroma + 2f) / 6 * 2 * PI
            value == blue -> ((blue - red) / chroma + 2f) / 6 * 2 * PI
            else -> error("Unexpected situation when calculating Hue from RGBA (${this.value.toString(16).padStart(8, '0')})")
        }.toDouble().radians
    }
public val KoneColor.hsvSaturation: Float
    get() {
        val red = rgbRed
        val green = rgbGreen
        val blue = rgbBlue
        
        val value = maxOf(red, green, blue)
        val chroma = value - minOf(red, green, blue)
        return if (value == 0f) 0f else chroma / value
    }
public val KoneColor.hsvSaturationUByte: UByte get() = (hsvSaturation * 255).toInt().toUByte()
public val KoneColor.hsvValue: Float get() = maxOf(rgbRed, rgbGreen, rgbBlue)
public val KoneColor.hsvValueUByte: UByte get() = maxOf(rgbRedUByte, rgbGreenUByte, rgbBlueUByte)

public val KoneColor.Companion.AliceBlue: KoneColor get() = KoneColor(0xF0F8FFFFu)
public val KoneColor.Companion.AntiqueWhite: KoneColor get() = KoneColor(0xFAEBD7FFu)
public val KoneColor.Companion.Aqua: KoneColor get() = KoneColor(0x00FFFFFFu)
public val KoneColor.Companion.Aquamarine: KoneColor get() = KoneColor(0x7FFFD4FFu)
public val KoneColor.Companion.Azure: KoneColor get() = KoneColor(0xF0FFFFFFu)
public val KoneColor.Companion.Beige: KoneColor get() = KoneColor(0xF5F5DCFFu)
public val KoneColor.Companion.Bisque: KoneColor get() = KoneColor(0xFFE4C4FFu)
public val KoneColor.Companion.Black: KoneColor get() = KoneColor(0x000000FFu)
public val KoneColor.Companion.BlanchedAlmond: KoneColor get() = KoneColor(0xFFEBCDFFu)
public val KoneColor.Companion.Blue: KoneColor get() = KoneColor(0x0000FFFFu)
public val KoneColor.Companion.BlueViolet: KoneColor get() = KoneColor(0x8A2BE2FFu)
public val KoneColor.Companion.Brown: KoneColor get() = KoneColor(0xA52A2AFFu)
public val KoneColor.Companion.Burlywood: KoneColor get() = KoneColor(0xDEB887FFu)
public val KoneColor.Companion.CadetBlue: KoneColor get() = KoneColor(0x5F9EA0FFu)
public val KoneColor.Companion.Chartreuse: KoneColor get() = KoneColor(0x7FFF00FFu)
public val KoneColor.Companion.Chocolate: KoneColor get() = KoneColor(0xD2691EFFu)
public val KoneColor.Companion.Coral: KoneColor get() = KoneColor(0xFF7F50FFu)
public val KoneColor.Companion.CornflowerBlue: KoneColor get() = KoneColor(0x6495EDFFu)
public val KoneColor.Companion.Cornsilk: KoneColor get() = KoneColor(0xFFF8DCFFu)
public val KoneColor.Companion.Crimson: KoneColor get() = KoneColor(0xDC143CFFu)
public val KoneColor.Companion.Cyan: KoneColor get() = KoneColor(0x00FFFFFFu)
public val KoneColor.Companion.DarkBlue: KoneColor get() = KoneColor(0x00008BFFu)
public val KoneColor.Companion.DarkCyan: KoneColor get() = KoneColor(0x008B8BFFu)
public val KoneColor.Companion.DarkGoldenrod: KoneColor get() = KoneColor(0xB8860BFFu)
public val KoneColor.Companion.DarkGray: KoneColor get() = KoneColor(0xA9A9A9FFu)
public val KoneColor.Companion.DarkGreen: KoneColor get() = KoneColor(0x006400FFu)
public val KoneColor.Companion.DarkKhaki: KoneColor get() = KoneColor(0xBDB76BFFu)
public val KoneColor.Companion.DarkMagenta: KoneColor get() = KoneColor(0x8B008BFFu)
public val KoneColor.Companion.DarkOliveGreen: KoneColor get() = KoneColor(0x556B2FFFu)
public val KoneColor.Companion.DarkOrange: KoneColor get() = KoneColor(0xFF8C00FFu)
public val KoneColor.Companion.DarkOrchid: KoneColor get() = KoneColor(0x9932CCFFu)
public val KoneColor.Companion.DarkRed: KoneColor get() = KoneColor(0x8B0000FFu)
public val KoneColor.Companion.DarkSalmon: KoneColor get() = KoneColor(0xE9967AFFu)
public val KoneColor.Companion.DarkSeaGreen: KoneColor get() = KoneColor(0x8FBC8FFFu)
public val KoneColor.Companion.DarkSlateBlue: KoneColor get() = KoneColor(0x483D8BFFu)
public val KoneColor.Companion.DarkSlateGray: KoneColor get() = KoneColor(0x2F4F4FFFu)
public val KoneColor.Companion.DarkTurquoise: KoneColor get() = KoneColor(0x00CED1FFu)
public val KoneColor.Companion.DarkViolet: KoneColor get() = KoneColor(0x9400D3FFu)
public val KoneColor.Companion.DeepPink: KoneColor get() = KoneColor(0xFF1493FFu)
public val KoneColor.Companion.DeepSkyBlue: KoneColor get() = KoneColor(0x00BFFFFFu)
public val KoneColor.Companion.DimGray: KoneColor get() = KoneColor(0x696969FFu)
public val KoneColor.Companion.DodgerBlue: KoneColor get() = KoneColor(0x1E90FFFFu)
public val KoneColor.Companion.Firebrick: KoneColor get() = KoneColor(0xB22222FFu)
public val KoneColor.Companion.FloralWhite: KoneColor get() = KoneColor(0xFFFAF0FFu)
public val KoneColor.Companion.ForestGreen: KoneColor get() = KoneColor(0x228B22FFu)
public val KoneColor.Companion.Fuchsia: KoneColor get() = KoneColor(0xFF00FFFFu)
public val KoneColor.Companion.Gainsboro: KoneColor get() = KoneColor(0xDCDCDCFFu)
public val KoneColor.Companion.GhostWhite: KoneColor get() = KoneColor(0xF8F8FFFFu)
public val KoneColor.Companion.Gold: KoneColor get() = KoneColor(0xFFD700FFu)
public val KoneColor.Companion.Goldenrod: KoneColor get() = KoneColor(0xDAA520FFu)
public val KoneColor.Companion.Gray: KoneColor get() = KoneColor(0x808080FFu)
public val KoneColor.Companion.Green: KoneColor get() = KoneColor(0x008000FFu)
public val KoneColor.Companion.GreenYellow: KoneColor get() = KoneColor(0xADFF2FFFu)
public val KoneColor.Companion.Honeydew: KoneColor get() = KoneColor(0xF0FFF0FFu)
public val KoneColor.Companion.HotPink: KoneColor get() = KoneColor(0xFF69B4FFu)
public val KoneColor.Companion.IndianRed: KoneColor get() = KoneColor(0xCD5C5CFFu)
public val KoneColor.Companion.Indigo: KoneColor get() = KoneColor(0x4B0082FFu)
public val KoneColor.Companion.Ivory: KoneColor get() = KoneColor(0xFFFFF0FFu)
public val KoneColor.Companion.Khaki: KoneColor get() = KoneColor(0xF0E68CFFu)
public val KoneColor.Companion.Lavender: KoneColor get() = KoneColor(0xE6E6FAFFu)
public val KoneColor.Companion.LavenderBlush: KoneColor get() = KoneColor(0xFFF0F5FFu)
public val KoneColor.Companion.LawnGreen: KoneColor get() = KoneColor(0x7CFC00FFu)
public val KoneColor.Companion.LemonChiffon: KoneColor get() = KoneColor(0xFFFACDFFu)
public val KoneColor.Companion.LightBlue: KoneColor get() = KoneColor(0xADD8E6FFu)
public val KoneColor.Companion.LightCoral: KoneColor get() = KoneColor(0xF08080FFu)
public val KoneColor.Companion.LightCyan: KoneColor get() = KoneColor(0xE0FFFFFFu)
public val KoneColor.Companion.LightGoldenrodYellow: KoneColor get() = KoneColor(0xFAFAD2FFu)
public val KoneColor.Companion.LightGray: KoneColor get() = KoneColor(0xD3D3D3FFu)
public val KoneColor.Companion.LightGreen: KoneColor get() = KoneColor(0x90EE90FFu)
public val KoneColor.Companion.LightPink: KoneColor get() = KoneColor(0xFFB6C1FFu)
public val KoneColor.Companion.LightSalmon: KoneColor get() = KoneColor(0xFFA07AFFu)
public val KoneColor.Companion.LightSeaGreen: KoneColor get() = KoneColor(0x20B2AAFFu)
public val KoneColor.Companion.LightSkyBlue: KoneColor get() = KoneColor(0x87CEFAFFu)
public val KoneColor.Companion.LightSlateGray: KoneColor get() = KoneColor(0x778899FFu)
public val KoneColor.Companion.LightSteelBlue: KoneColor get() = KoneColor(0xB0C4DEFFu)
public val KoneColor.Companion.LightYellow: KoneColor get() = KoneColor(0xFFFFE0FFu)
public val KoneColor.Companion.Lime: KoneColor get() = KoneColor(0x00FF00FFu)
public val KoneColor.Companion.LimeGreen: KoneColor get() = KoneColor(0x32CD32FFu)
public val KoneColor.Companion.Linen: KoneColor get() = KoneColor(0xFAF0E6FFu)
public val KoneColor.Companion.Magenta: KoneColor get() = KoneColor(0xFF00FFFFu)
public val KoneColor.Companion.Maroon: KoneColor get() = KoneColor(0x800000FFu)
public val KoneColor.Companion.MediumAquamarine: KoneColor get() = KoneColor(0x66CDAAFFu)
public val KoneColor.Companion.MediumBlue: KoneColor get() = KoneColor(0x0000CDFFu)
public val KoneColor.Companion.MediumOrchid: KoneColor get() = KoneColor(0xBA55D3FFu)
public val KoneColor.Companion.MediumPurple: KoneColor get() = KoneColor(0x9370DBFFu)
public val KoneColor.Companion.MediumSeaGreen: KoneColor get() = KoneColor(0x3CB371FFu)
public val KoneColor.Companion.MediumSlateBlue: KoneColor get() = KoneColor(0x7B68EEFFu)
public val KoneColor.Companion.MediumSpringGreen: KoneColor get() = KoneColor(0x00FA9AFFu)
public val KoneColor.Companion.MediumTurquoise: KoneColor get() = KoneColor(0x48D1CCFFu)
public val KoneColor.Companion.MediumVioletRed: KoneColor get() = KoneColor(0xC71585FFu)
public val KoneColor.Companion.MidnightBlue: KoneColor get() = KoneColor(0x191970FFu)
public val KoneColor.Companion.MintCream: KoneColor get() = KoneColor(0xF5FFFAFFu)
public val KoneColor.Companion.MistyRose: KoneColor get() = KoneColor(0xFFE4E1FFu)
public val KoneColor.Companion.Moccasin: KoneColor get() = KoneColor(0xFFE4B5FFu)
public val KoneColor.Companion.NavajoWhite: KoneColor get() = KoneColor(0xFFDEADFFu)
public val KoneColor.Companion.Navy: KoneColor get() = KoneColor(0x000080FFu)
public val KoneColor.Companion.OldLace: KoneColor get() = KoneColor(0xFDF5E6FFu)
public val KoneColor.Companion.Olive: KoneColor get() = KoneColor(0x808000FFu)
public val KoneColor.Companion.OliveDrab: KoneColor get() = KoneColor(0x6B8E23FFu)
public val KoneColor.Companion.Orange: KoneColor get() = KoneColor(0xFFA500FFu)
public val KoneColor.Companion.OrangeRed: KoneColor get() = KoneColor(0xFF4500FFu)
public val KoneColor.Companion.Orchid: KoneColor get() = KoneColor(0xDA70D6FFu)
public val KoneColor.Companion.PaleGoldenrod: KoneColor get() = KoneColor(0xEEE8AAFFu)
public val KoneColor.Companion.PaleGreen: KoneColor get() = KoneColor(0x98FB98FFu)
public val KoneColor.Companion.PaleTurquoise: KoneColor get() = KoneColor(0xAFEEEEFFu)
public val KoneColor.Companion.PaleVioletRed: KoneColor get() = KoneColor(0xDB7093FFu)
public val KoneColor.Companion.PapayaWhip: KoneColor get() = KoneColor(0xFFEFD5FFu)
public val KoneColor.Companion.PeachPuff: KoneColor get() = KoneColor(0xFFDAB9FFu)
public val KoneColor.Companion.Peru: KoneColor get() = KoneColor(0xCD853FFFu)
public val KoneColor.Companion.Pink: KoneColor get() = KoneColor(0xFFC0CBFFu)
public val KoneColor.Companion.Plum: KoneColor get() = KoneColor(0xDDA0DDFFu)
public val KoneColor.Companion.PowderBlue: KoneColor get() = KoneColor(0xB0E0E6FFu)
public val KoneColor.Companion.Purple: KoneColor get() = KoneColor(0x800080FFu)
public val KoneColor.Companion.Red: KoneColor get() = KoneColor(0xFF0000FFu)
public val KoneColor.Companion.RosyBrown: KoneColor get() = KoneColor(0xBC8F8FFFu)
public val KoneColor.Companion.RoyalBlue: KoneColor get() = KoneColor(0x4169E1FFu)
public val KoneColor.Companion.SaddleBrown: KoneColor get() = KoneColor(0x8B4513FFu)
public val KoneColor.Companion.Salmon: KoneColor get() = KoneColor(0xFA8072FFu)
public val KoneColor.Companion.SandyBrown: KoneColor get() = KoneColor(0xF4A460FFu)
public val KoneColor.Companion.SeaGreen: KoneColor get() = KoneColor(0x2E8B57FFu)
public val KoneColor.Companion.Seashell: KoneColor get() = KoneColor(0xFFF5EEFFu)
public val KoneColor.Companion.Sienna: KoneColor get() = KoneColor(0xA0522DFFu)
public val KoneColor.Companion.Silver: KoneColor get() = KoneColor(0xC0C0C0FFu)
public val KoneColor.Companion.SkyBlue: KoneColor get() = KoneColor(0x87CEEBFFu)
public val KoneColor.Companion.SlateBlue: KoneColor get() = KoneColor(0x6A5ACDFFu)
public val KoneColor.Companion.SlateGray: KoneColor get() = KoneColor(0x708090FFu)
public val KoneColor.Companion.Snow: KoneColor get() = KoneColor(0xFFFAFAFFu)
public val KoneColor.Companion.SpringGreen: KoneColor get() = KoneColor(0x00FF7FFFu)
public val KoneColor.Companion.SteelBlue: KoneColor get() = KoneColor(0x4682B4FFu)
public val KoneColor.Companion.Tan: KoneColor get() = KoneColor(0xD2B48CFFu)
public val KoneColor.Companion.Teal: KoneColor get() = KoneColor(0x008080FFu)
public val KoneColor.Companion.Thistle: KoneColor get() = KoneColor(0xD8BFD8FFu)
public val KoneColor.Companion.Tomato: KoneColor get() = KoneColor(0xFF6347FFu)
public val KoneColor.Companion.Turquoise: KoneColor get() = KoneColor(0x40E0D0FFu)
public val KoneColor.Companion.Violet: KoneColor get() = KoneColor(0xEE82EEFFu)
public val KoneColor.Companion.Wheat: KoneColor get() = KoneColor(0xF5DEB3FFu)
public val KoneColor.Companion.White: KoneColor get() = KoneColor(0xFFFFFFFFu)
public val KoneColor.Companion.WhiteSmoke: KoneColor get() = KoneColor(0xF5F5F5FFu)
public val KoneColor.Companion.Yellow: KoneColor get() = KoneColor(0xFFFF00FFu)
public val KoneColor.Companion.YellowGreen: KoneColor get() = KoneColor(0x9ACD32FFu)