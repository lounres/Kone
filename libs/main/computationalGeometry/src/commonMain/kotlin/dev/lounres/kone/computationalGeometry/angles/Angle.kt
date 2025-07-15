/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.angles

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@Serializable
@JvmInline
public value class Angle internal constructor(internal val value: Double) {
    public companion object {
        public val zero: Angle = Angle(0.0)
        public val right: Angle = Angle(PI / 2)
        public val straight: Angle = Angle(PI)
        public val full: Angle = Angle(PI * 2)
    }
}

public operator fun Angle.plus(other: Angle): Angle = Angle(this.value + other.value)
public operator fun Angle.minus(other: Angle): Angle = Angle(this.value - other.value)

public operator fun Angle.times(other: Double): Angle = Angle(this.value * other)
public operator fun Double.times(other: Angle): Angle = Angle(this * other.value)
public operator fun Angle.div(other: Double): Angle = Angle(this.value / other)

public val Double.radians: Angle get() = Angle(this)
public val Double.degrees: Angle get() = Angle(this / 180.0 * PI)

public fun Angle.inRadians(): Double = value
public fun Angle.inDegrees(): Double = value / PI * 180.0

public fun cos(angle: Angle): Double = cos(angle.inRadians())
public fun sin(angle: Angle): Double = sin(angle.inRadians())