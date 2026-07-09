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
public value class KoneColor(
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