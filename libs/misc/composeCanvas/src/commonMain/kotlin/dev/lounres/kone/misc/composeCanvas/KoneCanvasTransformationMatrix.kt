/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.composeCanvas

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
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

internal fun PointWrapper<MDList1<Double>>.transform(matrix: KoneCanvasTransformationMatrix): PointWrapper<MDList1<Double>> {
    val x = this.vector[0u] * matrix.coefficients[0u] + this.vector[1u] * matrix.coefficients[1u] + matrix.coefficients[2u]
    val y = this.vector[0u] * matrix.coefficients[3u] + this.vector[1u] * matrix.coefficients[4u] + matrix.coefficients[5u]
    val z = this.vector[0u] * matrix.coefficients[6u] + this.vector[1u] * matrix.coefficients[7u] + matrix.coefficients[8u]
    return PointWrapper(MDList1(x / z, y / z))
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