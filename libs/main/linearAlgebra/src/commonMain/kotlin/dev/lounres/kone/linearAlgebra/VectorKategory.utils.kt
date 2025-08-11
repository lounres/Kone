/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.linearAlgebra.relations.equality
import dev.lounres.kone.linearAlgebra.relations.hashing
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2Producer
import dev.lounres.kone.multidimensionalCollections.producers.*
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public fun <N> Ring<N>.defaultVectorKategory(
    mdList1Producer: MDList1Producer,
    mdList2Producer: MDList2Producer,
): VectorKategory<N> = DefaultVectorKategory(this, mdList1Producer, mdList2Producer)

public fun <N> Ring<N>.defaultVectorKategory(
    mdListProducer: MDListProducer,
): VectorKategory<N> = DefaultVectorKategory(this, mdListProducer.as1D(), mdListProducer.as2D())

public fun <N> Ring<N>.defaultVectorKategory(): VectorKategory<N> = DefaultVectorKategory(this, ArrayMDList1Producer, ArrayMDList2Producer)

public fun <N> RegistryBuilder<KoneContextRegistry>.setDefaultVectorKategoryFor(
    numberType: SuppliedType,
    mdList1Producer: MDList1Producer,
    mdList2Producer: MDList2Producer,
) {
    val vectorKategory = this[Ring.Key<N>(numberType)].defaultVectorKategory(mdList1Producer, mdList2Producer)
    VectorKategory.Key<N>(numberType) correspondsTo vectorKategory
}

public fun <N> RegistryBuilder<KoneContextRegistry>.setDefaultVectorKategoryFor(
    numberType: SuppliedType,
    mdListProducer: MDListProducer,
) {
    val vectorKategory = this[Ring.Key<N>(numberType)].defaultVectorKategory(mdListProducer)
    this[VectorKategory.Key<N>(numberType)] = vectorKategory
}

public fun <N> RegistryBuilder<KoneContextRegistry>.setDefaultVectorKategoryFor(
    numberType: SuppliedType,
) {
    val vectorKategory = this[Ring.Key<N>(numberType)].defaultVectorKategory()
    VectorKategory.Key<N>(numberType) correspondsTo vectorKategory
}

public fun <N> RegistryBuilder<KoneContextRegistry>.setDefaultVectorAndMatrixEqualitiesFor(
    numberType: SuppliedType,
) {
    val numberEquality = this[Equality.Key<N>(numberType)]
    Equality.Key<RowVector<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.RowVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo RowVector.equality(numberEquality)
    Equality.Key<ColumnVector<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.ColumnVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo ColumnVector.equality(numberEquality)
    Equality.Key<Matrix<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.Matrix",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo Matrix.equality(numberEquality)
}

public fun <N> RegistryBuilder<KoneContextRegistry>.setDefaultVectorAndMatrixHashingsFor(
    numberType: SuppliedType,
) {
    val numberHashing = this[Hashing.Key<N>(numberType)]
    Hashing.Key<RowVector<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.RowVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo RowVector.hashing(numberHashing)
    Hashing.Key<ColumnVector<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.ColumnVector",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo ColumnVector.hashing(numberHashing)
    Hashing.Key<Matrix<N>>(
        @OptIn(DelicateSuppliedTypeConstructor::class)
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.linearAlgebra.Matrix",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType
                )
            ),
            isNullable = false
        )
    ) correspondsTo Matrix.hashing(numberHashing)
}

public fun <N, R> KoneContextRegistry.inVectorKategoryFor(numberType: SuppliedType, block: context(VectorKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[VectorKategory.Key<N>(numberType)])
}

public fun <N, R> KoneContextRegistry.inVectorKategoryScopeFor(numberType: SuppliedType, block: context(Ring<N>, VectorKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[Ring.Key<N>(numberType)], this.contexts[VectorKategory.Key<N>(numberType)])
}