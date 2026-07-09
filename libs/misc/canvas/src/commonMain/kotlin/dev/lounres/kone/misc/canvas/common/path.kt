/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.common

import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2


public interface KoneCanvasPathContext {
    public fun moveTo(point: Point2<Double>)
    public fun moveToRelative(vector: Vector2<Double>)
    public fun lineTo(point: Point2<Double>)
    public fun lineToRelative(vector: Vector2<Double>)
    public fun quadraticBezierTo(point1: Point2<Double>, point2: Point2<Double>)
    public fun quadraticBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>)
    public fun cubicBezierTo(point1: Point2<Double>, point2: Point2<Double>, point3: Point2<Double>)
    public fun cubicBezierToRelative(vector1: Vector2<Double>, vector2: Vector2<Double>, vector3: Vector2<Double>)
    public fun arcRelative(size: Vector2<Double>, rotation: Angle, startAngle: Angle, sweepAngle: Angle)
    public fun close()
}