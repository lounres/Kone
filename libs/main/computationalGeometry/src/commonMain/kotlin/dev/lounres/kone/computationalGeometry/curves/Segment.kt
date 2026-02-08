/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.curves

import dev.lounres.kone.computationalGeometry.AffineSpaceOverRing
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import kotlinx.serialization.Serializable


@Serializable
//@JvmInline
public /*value*/ data class Segment<out Vector, out Point>(
    public val start: Point,
    public val direction: Vector,
) {
    override fun toString() : String = "Segment($start, $direction)"
    
    public companion object
}

context(_: AffineSpaceOverRing<*, Vector, Point>)
public val <Vector, Point> Segment<Vector, Point>.end: Point
    get() = this.start + this.direction

context(_: AffineSpaceOverRing<*, Vector, Point>)
public fun <Vector, Point> Segment.Companion.fromEnds(start: Point, end: Point): Segment<Vector, Point> = Segment(start = start, direction = end - start)
