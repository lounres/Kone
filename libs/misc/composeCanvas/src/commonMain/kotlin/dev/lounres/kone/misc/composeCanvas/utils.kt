/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.field
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField
import dev.lounres.kone.computationalGeometry.default2.Point2
import dev.lounres.kone.computationalGeometry.default2.Vector2


@PublishedApi
internal val koneCanvasEuclideanSpace: EuclideanSpace2OverField<Double> = EuclideanSpace2OverField(Double.field())

public inline fun <Result> inKoneCanvasEuclideanSpace(block: context(Field<Double>, EuclideanSpaceOverField<Double, Vector2<Double>, Point2<Double>>) () -> Result): Result =
    block(
        Double.field(),
        koneCanvasEuclideanSpace,
    )