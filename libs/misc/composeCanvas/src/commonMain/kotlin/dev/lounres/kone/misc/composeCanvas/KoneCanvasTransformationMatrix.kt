/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.Point2
import dev.lounres.kone.contexts.invoke
import kotlin.jvm.JvmInline
import kotlin.math.sqrt


@JvmInline
public value class KoneCanvasTransformationMatrix(
    public val coefficients: KoneDoubleArray,
) {
    init {
        require(coefficients.size == 9u)
    }
}

internal val KoneCanvasTransformationMatrix.norm: Double get() = sqrt(Double.context { coefficients.sumOf { it * it } })
internal fun KoneCanvasTransformationMatrix.normalized(): KoneCanvasTransformationMatrix {
    val norm = norm
    return KoneCanvasTransformationMatrix(KoneDoubleArray(9u) { coefficients[it] / norm })
}

internal fun Point2<Double>.transform(matrix: KoneCanvasTransformationMatrix): Point2<Double> {
    val x = this.x * matrix.coefficients[0u] + this.y * matrix.coefficients[1u] + matrix.coefficients[2u]
    val y = this.x * matrix.coefficients[3u] + this.y * matrix.coefficients[4u] + matrix.coefficients[5u]
    val z = this.x * matrix.coefficients[6u] + this.y * matrix.coefficients[7u] + matrix.coefficients[8u]
    return Point2(x / z, y / z)
}

internal fun KoneCanvasPath.Part.transform(matrix: KoneCanvasTransformationMatrix): KoneCanvasPath.Part =
    when (this) {
        is KoneCanvasPath.Part.LineTo -> KoneCanvasPath.Part.LineTo(end.transform(matrix))
    }

internal fun KoneCanvasPath.transform(matrix: KoneCanvasTransformationMatrix): KoneCanvasPath =
    KoneCanvasPath(
        paths = paths.map { subpath ->
            KoneCanvasPath.Subpath(
                start = subpath.start.transform(matrix),
                parts = subpath.parts.map { it.transform(matrix) }
            )
        }
    )