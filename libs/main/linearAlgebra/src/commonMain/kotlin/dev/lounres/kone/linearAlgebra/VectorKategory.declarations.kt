/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.reciprocal
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface VectorKategory<N, Content1: MDList1<N>, Content2: MDList2<N>> : KoneContext {
    public fun RowVector.Companion.zero(size: UInt): RowVector<N, Content1>
    public fun ColumnVector.Companion.zero(size: UInt): ColumnVector<N, Content1>
    public fun Matrix.Companion.zero(rowNumber: UInt, columnNumber: UInt): Matrix<N, Content2>
    
    public operator fun RowVector<N, Content1>.unaryMinus(): RowVector<N, Content1>
    public operator fun ColumnVector<N, Content1>.unaryMinus(): ColumnVector<N, Content1>
    public operator fun Matrix<N, Content2>.unaryMinus(): Matrix<N, Content2>

    public operator fun RowVector<N, Content1>.plus(other: RowVector<N, Content1>): RowVector<N, Content1>
    public operator fun ColumnVector<N, Content1>.plus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1>
    public operator fun Matrix<N, Content2>.plus(other: Matrix<N, Content2>): Matrix<N, Content2>

    public operator fun RowVector<N, Content1>.minus(other: RowVector<N, Content1>): RowVector<N, Content1>
    public operator fun ColumnVector<N, Content1>.minus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1>
    public operator fun Matrix<N, Content2>.minus(other: Matrix<N, Content2>): Matrix<N, Content2>

    public operator fun RowVector<N, Content1>.times(other: N): RowVector<N, Content1>
    public operator fun N.times(other: RowVector<N, Content1>): RowVector<N, Content1>
    public operator fun ColumnVector<N, Content1>.times(other: N): ColumnVector<N, Content1>
    public operator fun N.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1>
    public operator fun Matrix<N, Content2>.times(other: N): Matrix<N, Content2>
    public operator fun N.times(other: Matrix<N, Content2>): Matrix<N, Content2>

    public operator fun Matrix<N, Content2>.times(other: Matrix<N, Content2>): Matrix<N, Content2>
    public operator fun Matrix<N, Content2>.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1>
    public operator fun RowVector<N, Content1>.times(other: Matrix<N, Content2>): RowVector<N, Content1>
    public operator fun RowVector<N, Content1>.times(other: ColumnVector<N, Content1>): N
    
    public class Key<Number, Content1: MDList1<Number>, Content2: MDList2<Number>>(
        elementType: SuppliedType,
        content1Type: SuppliedType,
        content2Type: SuppliedType,
    ) : RegistryKey<VectorKategory<Number, Content1, Content2>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.linearAlgebra.VectorKategory",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = content1Type
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = content2Type
                    ),
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(vectorKategory: VectorKategory<N, Content1, *>)
public fun <N, Content1: MDList1<N>> RowVector.Companion.zero(size: UInt): RowVector<N, Content1> = with(vectorKategory) { RowVector.zero(size) }
context(vectorKategory: VectorKategory<N, Content1, *>)
public fun <N, Content1: MDList1<N>> ColumnVector.Companion.zero(size: UInt): ColumnVector<N, Content1> = with(vectorKategory) { ColumnVector.zero(size) }
context(vectorKategory: VectorKategory<N, *, Content2>)
public fun <N, Content2: MDList2<N>> Matrix.Companion.zero(rowNumber: UInt, columnNumber: UInt): Matrix<N, Content2> = with(vectorKategory) { Matrix.zero(rowNumber, columnNumber) }

context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.unaryMinus(): RowVector<N, Content1> = with(vectorKategory) { -this@unaryMinus }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.unaryMinus(): ColumnVector<N, Content1> = with(vectorKategory) { -this@unaryMinus }
context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.unaryMinus(): Matrix<N, Content2> = with(vectorKategory) { -this@unaryMinus }

context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.plus(other: RowVector<N, Content1>): RowVector<N, Content1> = with(vectorKategory) { this@plus + other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.plus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> = with(vectorKategory) { this@plus + other }
context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.plus(other: Matrix<N, Content2>): Matrix<N, Content2> = with(vectorKategory) { this@plus + other }

context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.minus(other: RowVector<N, Content1>): RowVector<N, Content1> = with(vectorKategory) { this@minus - other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.minus(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> = with(vectorKategory) { this@minus - other }
context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.minus(other: Matrix<N, Content2>): Matrix<N, Content2> = with(vectorKategory) { this@minus - other }

context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.times(other: N): RowVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> N.times(other: RowVector<N, Content1>): RowVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.times(other: N): ColumnVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> N.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.times(other: N): Matrix<N, Content2> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> N.times(other: Matrix<N, Content2>): Matrix<N, Content2> = with(vectorKategory) { this@times * other }

context(vectorKategory: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.times(other: Matrix<N, Content2>): Matrix<N, Content2> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, Content2>)
public operator fun <N, Content1: MDList1<N>, Content2: MDList2<N>> Matrix<N, Content2>.times(other: ColumnVector<N, Content1>): ColumnVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, Content2>)
public operator fun <N, Content1: MDList1<N>, Content2: MDList2<N>> RowVector<N, Content1>.times(other: Matrix<N, Content2>): RowVector<N, Content1> = with(vectorKategory) { this@times * other }
context(vectorKategory: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.times(other: ColumnVector<N, Content1>): N = with(vectorKategory) { this@times * other }

// TODO: Think about moving the divisions to a separate interface:
context(_: Field<N>, _: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> RowVector<N, Content1>.div(other: N): RowVector<N, Content1> = this * other.reciprocal
context(_: Field<N>, _: VectorKategory<N, Content1, *>)
public operator fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.div(other: N): ColumnVector<N, Content1> = this * other.reciprocal
context(_: Field<N>, _: VectorKategory<N, *, Content2>)
public operator fun <N, Content2: MDList2<N>> Matrix<N, Content2>.div(other: N): Matrix<N, Content2> = this * other.reciprocal

// TODO: Think about moving the zero checks to a separate interface:
context( _: VectorKategory<N, Content1, *>, _: Equality<RowVector<N, Content1>>)
public fun <N, Content1: MDList1<N>> RowVector<N, Content1>.isZero(): Boolean = this eq RowVector.zero(size)
context( _: VectorKategory<N, Content1, *>, _: Equality<ColumnVector<N, Content1>>)
public fun <N, Content1: MDList1<N>> ColumnVector<N, Content1>.isZero(): Boolean = this eq ColumnVector.zero(size)
context( _: VectorKategory<N, *, Content2>, _: Equality<Matrix<N, Content2>>)
public fun <N, Content2: MDList2<N>> Matrix<N, Content2>.isZero(): Boolean = this eq Matrix.zero(rowNumber, columnNumber)