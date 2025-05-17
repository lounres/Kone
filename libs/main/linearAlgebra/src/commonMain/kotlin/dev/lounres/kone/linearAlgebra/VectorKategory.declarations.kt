/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.linearAlgebra.relations.MatrixEquality
import dev.lounres.kone.linearAlgebra.relations.columnVectorEquality
import dev.lounres.kone.linearAlgebra.relations.rowVectorEquality
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDList1Producer
import dev.lounres.kone.multidimensionalCollections.producers.MDList2Producer
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface VectorKategory<N> : KoneContext {
    public val rowVectorEquality: Equality<RowVector<N>>
    public val columnVectorEquality: Equality<ColumnVector<N>>
    public val matrixEquality: Equality<Matrix<N>>

    public operator fun RowVector<N>.unaryMinus(): RowVector<N>
    public operator fun ColumnVector<N>.unaryMinus(): ColumnVector<N>
    public operator fun Matrix<N>.unaryMinus(): Matrix<N>

    public operator fun RowVector<N>.plus(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N>

    public operator fun RowVector<N>.minus(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N>

    public operator fun RowVector<N>.times(other: N): RowVector<N>
    public operator fun N.times(other: RowVector<N>): RowVector<N>
    public operator fun ColumnVector<N>.times(other: N): ColumnVector<N>
    public operator fun N.times(other: ColumnVector<N>): ColumnVector<N>
    public operator fun Matrix<N>.times(other: N): Matrix<N>
    public operator fun N.times(other: Matrix<N>): Matrix<N>

    public operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N>
    public operator fun Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N>
    public operator fun RowVector<N>.times(other: Matrix<N>): RowVector<N>
    public operator fun RowVector<N>.times(other: ColumnVector<N>): N
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<VectorKategory<Number>> {
        override val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.VectorKategory",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.unaryMinus(): RowVector<N> = with(vectorKategory) { -this@unaryMinus }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> ColumnVector<N>.unaryMinus(): ColumnVector<N> = with(vectorKategory) { -this@unaryMinus }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.unaryMinus(): Matrix<N> = with(vectorKategory) { -this@unaryMinus }

context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.plus(other: RowVector<N>): RowVector<N> = with(vectorKategory) { this@plus + other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N> = with(vectorKategory) { this@plus + other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.plus(other: Matrix<N>): Matrix<N> = with(vectorKategory) { this@plus + other }

context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.minus(other: RowVector<N>): RowVector<N> = with(vectorKategory) { this@minus - other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N> = with(vectorKategory) { this@minus - other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.minus(other: Matrix<N>): Matrix<N> = with(vectorKategory) { this@minus - other }

context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.times(other: N): RowVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> N.times(other: RowVector<N>): RowVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> ColumnVector<N>.times(other: N): ColumnVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> N.times(other: ColumnVector<N>): ColumnVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.times(other: N): Matrix<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> N.times(other: Matrix<N>): Matrix<N> = with(vectorKategory) { this@times * other }

context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.times(other: Matrix<N>): Matrix<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.times(other: Matrix<N>): RowVector<N> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N>)
public operator fun <N> RowVector<N>.times(other: ColumnVector<N>): N = with(vectorKategory) { this@times * other }

internal class VectorKategoryWithNumberRing<N, out A: Ring<N>>(
    val numberRing: A,
    val mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    val mdList2Producer: MDList2Producer = ArrayMDList2Producer,
) : VectorKategory<N> {
    override val rowVectorEquality: Equality<RowVector<N>> get() = rowVectorEquality(numberRing)
    override val columnVectorEquality: Equality<ColumnVector<N>> get() = columnVectorEquality(numberRing)
    override val matrixEquality: Equality<Matrix<N>> get() = MatrixEquality(numberRing)

    override operator fun RowVector<N>.unaryMinus(): RowVector<N> =
        RowVector(mdList1Producer.produceBy(this.size) { context(numberRing) { -this@unaryMinus[it] } })
    override operator fun ColumnVector<N>.unaryMinus(): ColumnVector<N> =
        ColumnVector(mdList1Producer.produceBy(this.size) { context(numberRing) { -this[it] } })
    override operator fun Matrix<N>.unaryMinus(): Matrix<N> =
        Matrix(mdList2Producer.produceBy(this.rowNumber, this.columnNumber) { row, column -> context(numberRing) { -this[row, column] } })

    override operator fun RowVector<N>.plus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return RowVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] + other[it] } })
    }
    override operator fun ColumnVector<N>.plus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return ColumnVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] + other[it] } })
    }
    override operator fun Matrix<N>.plus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return Matrix(mdList2Producer.produceBy(this.rowNumber, this.columnNumber) { row, column -> context(numberRing) { this[row, column] + other[row, column] } })
    }

    override operator fun RowVector<N>.minus(other: RowVector<N>): RowVector<N> {
        requireShapeEquality(this, other)
        return RowVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] - other[it] } })
    }
    override operator fun ColumnVector<N>.minus(other: ColumnVector<N>): ColumnVector<N> {
        requireShapeEquality(this, other)
        return ColumnVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] - other[it] } })
    }
    override operator fun Matrix<N>.minus(other: Matrix<N>): Matrix<N> {
        requireShapeEquality(this, other)
        return Matrix(mdList2Producer.produceBy(this.rowNumber, this.columnNumber) { row, column -> context(numberRing) { this[row, column] - other[row, column] } })
    }

    override operator fun RowVector<N>.times(other: N): RowVector<N> =
        RowVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] * other } })
    override operator fun N.times(other: RowVector<N>): RowVector<N> =
        RowVector(mdList1Producer.produceBy(other.size) { context(numberRing) { this * other[it] } })
    override operator fun ColumnVector<N>.times(other: N): ColumnVector<N> =
        ColumnVector(mdList1Producer.produceBy(this.size) { context(numberRing) { this[it] * other } })
    override operator fun N.times(other: ColumnVector<N>): ColumnVector<N> =
        ColumnVector(mdList1Producer.produceBy(other.size) { context(numberRing) { this * other[it] } })
    override operator fun Matrix<N>.times(other: N): Matrix<N> =
        Matrix(mdList2Producer.produceBy(this.rowNumber, this.columnNumber) { row, column -> context(numberRing) { this[row, column] * other } })
    override operator fun N.times(other: Matrix<N>): Matrix<N> =
        Matrix(mdList2Producer.produceBy(other.rowNumber, other.columnNumber) { row, column -> context(numberRing) { this * other[row, column] } })

    override operator fun Matrix<N>.times(other: Matrix<N>): Matrix<N> {
        require(this.columnNumber == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return Matrix(mdList2Producer.produceBy(this.rowNumber, other.columnNumber) { row, column -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> context(numberRing) { acc + this[row, index] * other[index, column] } } })
    }
    override operator fun Matrix<N>.times(other: ColumnVector<N>): ColumnVector<N> {
        require(this.columnNumber == other.size) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return ColumnVector(mdList1Producer.produceBy(this.rowNumber) { row -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> context(numberRing) { acc + this[row, index] * other[index] } } })
    }
    override operator fun RowVector<N>.times(other: Matrix<N>): RowVector<N> {
        require(this.size == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.size
        return RowVector(mdList1Producer.produceBy(other.columnNumber) { column -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> context(numberRing) { acc + this[index] * other[index, column] } } })
    }
    override operator fun RowVector<N>.times(other: ColumnVector<N>): N {
        require(this.size == other.size) { TODO("Error message is not specified") }
        val indexRange = this.size
        return (0u..<indexRange).fold(numberRing.zero) { acc, index -> context(numberRing) { acc + this[index] * other[index] } }
    }
}