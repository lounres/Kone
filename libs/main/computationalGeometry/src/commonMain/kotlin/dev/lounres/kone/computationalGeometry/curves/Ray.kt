/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.curves

import dev.lounres.kone.computationalGeometry.AffineSpaceOverRing
import dev.lounres.kone.computationalGeometry.minus
import kotlinx.serialization.Serializable


@Serializable
//@JvmInline
public /*value*/ data class Ray<out Vector, out Point>(
    public val start: Point,
    public val direction: Vector,
) {
    override fun toString() : String = "Ray($start, $direction)"
    
    public companion object
}

context(_: AffineSpaceOverRing<*, Vector, Point>)
public fun <Vector, Point> Ray.Companion.byTwoPoints(start: Point, end: Point): Ray<Vector, Point> = Ray(start = start, direction = end - start)