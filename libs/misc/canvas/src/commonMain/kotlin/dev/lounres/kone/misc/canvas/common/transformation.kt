/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas.common

import dev.lounres.kone.computationalGeometry.angles.Angle
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2
import dev.lounres.kone.multidimensionalCollections.MDList2


public interface KoneCanvasTransformationContext {
    public fun affine(values: MDList2<Double>)
    public fun translate(vector: Vector2<Double>)
    public fun rotate(pivot: Point2<Double>, angle: Angle)
    public fun scale(pivot: Point2<Double>, scaleX: Double, scaleY: Double)
}