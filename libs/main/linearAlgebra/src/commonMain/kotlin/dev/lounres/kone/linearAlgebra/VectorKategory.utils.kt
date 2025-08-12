/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.linearAlgebra.relations.equality
import dev.lounres.kone.linearAlgebra.relations.hashing
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private class DefaultVectorKategory<N, Content1: MDList1<N>, Content2: MDList2<N>>(
    private val numberRing: Ring<N>,
    private val content1Producer: (size: UInt, initializer: (index: UInt) -> N) -> Content1,
    private val content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> N) -> Content2,
) : VectorKategory<N, Content1, Content2> {
    override operator fun RowVector<N, Content1>.unaryMinus(): RowVector<N, Content1> =
        RowVector(content1Producer(this.size) { numberRing { -this@unaryMinus[it] } })
    override operator fun ColumnVector<N, Content1>.unaryMinus(): ColumnVector<N, Content1> =
        ColumnVector(content1Producer(this.size) { numberRing { -this[it] } })
    override operator fun Matrix<N, Content2>.unaryMinus(): Matrix<N, Content2> =
        Matrix(content2Producer(this.rowNumber, this.columnNumber) { row, column -> numberRing { -this[row, column] } })
    
    override operator fun RowVector<N, Content1>.plus(other: RowVector<N, Content1>): RowVector<N, Content1> {
        requireMDSizeEquality(this, other)
        return RowVector(content1Producer(this.size) { numberRing { this[it] + other[it] } })
    }
    override operator fun ColumnVector<N, Content1>.plus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> {
        requireMDSizeEquality(this, other)
        return ColumnVector(content1Producer(this.size) { numberRing { this[it] + other[it] } })
    }
    override operator fun Matrix<N, Content2>.plus(other: Matrix<N, Content2>): Matrix<N, Content2> {
        requireMDSizeEquality(this, other)
        return Matrix(content2Producer(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] + other[row, column] } })
    }
    
    override operator fun RowVector<N, Content1>.minus(other: RowVector<N, Content1>): RowVector<N, Content1> {
        requireMDSizeEquality(this, other)
        return RowVector(content1Producer(this.size) { numberRing { this[it] - other[it] } })
    }
    override operator fun ColumnVector<N, Content1>.minus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> {
        requireMDSizeEquality(this, other)
        return ColumnVector(content1Producer(this.size) { numberRing { this[it] - other[it] } })
    }
    override operator fun Matrix<N, Content2>.minus(other: Matrix<N, Content2>): Matrix<N, Content2> {
        requireMDSizeEquality(this, other)
        return Matrix(content2Producer(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] - other[row, column] } })
    }
    
    override operator fun RowVector<N, Content1>.times(other: N): RowVector<N, Content1> =
        RowVector(content1Producer(this.size) { numberRing { this[it] * other } })
    override operator fun N.times(other: RowVector<N, Content1>): RowVector<N, Content1> =
        RowVector(content1Producer(other.size) { numberRing { this * other[it] } })
    override operator fun ColumnVector<N, Content1>.times(other: N): ColumnVector<N, Content1> =
        ColumnVector(content1Producer(this.size) { numberRing { this[it] * other } })
    override operator fun N.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> =
        ColumnVector(content1Producer(other.size) { numberRing { this * other[it] } })
    override operator fun Matrix<N, Content2>.times(other: N): Matrix<N, Content2> =
        Matrix(content2Producer(this.rowNumber, this.columnNumber) { row, column -> numberRing { this[row, column] * other } })
    override operator fun N.times(other: Matrix<N, Content2>): Matrix<N, Content2> =
        Matrix(content2Producer(other.rowNumber, other.columnNumber) { row, column -> numberRing { this * other[row, column] } })
    
    override operator fun Matrix<N, Content2>.times(other: Matrix<N, Content2>): Matrix<N, Content2> {
        require(this.columnNumber == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return Matrix(content2Producer(this.rowNumber, other.columnNumber) { row, column -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index, column] } } })
    }
    override operator fun Matrix<N, Content2>.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> {
        require(this.columnNumber == other.size) { TODO("Error message is not specified") }
        val indexRange = this.columnNumber
        return ColumnVector(content1Producer(this.rowNumber) { row -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[row, index] * other[index] } } })
    }
    override operator fun RowVector<N, Content1>.times(other: Matrix<N, Content2>): RowVector<N, Content1> {
        require(this.size == other.rowNumber) { TODO("Error message is not specified") }
        val indexRange = this.size
        return RowVector(content1Producer(other.columnNumber) { column -> (0u..<indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index, column] } } })
    }
    override operator fun RowVector<N, Content1>.times(other: ColumnVector<N, Content1>): N {
        require(this.size == other.size) { TODO("Error message is not specified") }
        val indexRange = this.size
        return (0u..<indexRange).fold(numberRing.zero) { acc, index -> numberRing { acc + this[index] * other[index] } }
    }
}

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> Ring<N>.defaultVectorKategory(
    content1Producer: (size: UInt, initializer: (index: UInt) -> N) -> Content1,
    content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> N) -> Content2,
): VectorKategory<N, Content1, Content2> = DefaultVectorKategory(this, content1Producer, content2Producer)

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setDefaultVectorKategoryFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
    content1Producer: (size: UInt, initializer: (index: UInt) -> N) -> Content1,
    content2Producer: (rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> N) -> Content2,
) {
    val vectorKategory = this[Ring.Key<N>(numberType)].defaultVectorKategory(content1Producer, content2Producer)
    VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type) correspondsTo vectorKategory
}

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setDefaultVectorAndMatrixEqualitiesFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    val numberEquality = this[Equality.Key<N>(numberType)]
    Equality.Key<RowVector<N, Content1>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.RowVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content1Type
                ),
            ),
            isNullable = false
        )
    ) correspondsTo RowVector.equality(numberEquality)
    Equality.Key<ColumnVector<N, Content1>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.ColumnVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content1Type
                ),
            ),
            isNullable = false
        )
    ) correspondsTo ColumnVector.equality(numberEquality)
    Equality.Key<Matrix<N, Content2>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.Matrix",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content2Type
                ),
            ),
            isNullable = false
        )
    ) correspondsTo Matrix.equality(numberEquality)
}

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RegistryBuilder<KoneContextRegistry>.setDefaultVectorAndMatrixHashingsFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
) {
    val numberHashing = this[Hashing.Key<N>(numberType)]
    Hashing.Key<RowVector<N, Content1>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.RowVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content1Type
                ),
            ),
            isNullable = false
        )
    ) correspondsTo RowVector.hashing(numberHashing)
    Hashing.Key<ColumnVector<N, Content1>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.ColumnVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content1Type,
                ),
            ),
            isNullable = false
        )
    ) correspondsTo ColumnVector.hashing(numberHashing)
    Hashing.Key<Matrix<N, Content2>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.Matrix",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                ),
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = content2Type
                ),
            ),
            isNullable = false
        )
    ) correspondsTo Matrix.hashing(numberHashing)
}

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>, R> KoneContextRegistry.inVectorKategoryFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
    block: context(VectorKategory<N, Content1, Content2>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}

public fun <N, Content1: MDList1<N>, Content2: MDList2<N>, R> KoneContextRegistry.inVectorKategoryScopeFor(
    numberType: SuppliedType,
    content1Type: SuppliedType,
    content2Type: SuppliedType,
    block: context(Ring<N>, VectorKategory<N, Content1, Content2>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[Ring.Key<N>(numberType)], this.contexts[VectorKategory.Key<N, Content1, Content2>(numberType, content1Type, content2Type)])
}