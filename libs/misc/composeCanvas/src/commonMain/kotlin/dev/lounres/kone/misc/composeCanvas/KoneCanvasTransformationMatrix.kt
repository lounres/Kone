/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.computationalGeometry.default2.Point2
import kotlin.jvm.JvmInline


@JvmInline
public value class KoneCanvasTransformationMatrix(
    public val coefficients: KoneDoubleArray,
) {
    init {
        require(coefficients.size == 6u)
    }
}

internal fun Point2<Double>.transform(matrix: KoneCanvasTransformationMatrix): Point2<Double> =
    Point2(
        this.x * matrix.coefficients[0u] + this.y * matrix.coefficients[1u] + matrix.coefficients[2u],
        this.x * matrix.coefficients[3u] + this.y * matrix.coefficients[4u] + matrix.coefficients[5u],
    )

internal fun KoneCanvasTransformationMatrix.transform(matrix: KoneCanvasTransformationMatrix): KoneCanvasTransformationMatrix =
    KoneCanvasTransformationMatrix(
        KoneDoubleArray.of(
            this.coefficients[0u] * matrix.coefficients[0u] + this.coefficients[3u] * matrix.coefficients[1u],
            this.coefficients[1u] * matrix.coefficients[0u] + this.coefficients[4u] * matrix.coefficients[1u],
            this.coefficients[2u] * matrix.coefficients[0u] + this.coefficients[5u] * matrix.coefficients[1u] + matrix.coefficients[2u],
            this.coefficients[0u] * matrix.coefficients[3u] + this.coefficients[3u] * matrix.coefficients[4u],
            this.coefficients[1u] * matrix.coefficients[3u] + this.coefficients[4u] * matrix.coefficients[4u],
            this.coefficients[2u] * matrix.coefficients[3u] + this.coefficients[5u] * matrix.coefficients[4u] + matrix.coefficients[5u],
        )
    )