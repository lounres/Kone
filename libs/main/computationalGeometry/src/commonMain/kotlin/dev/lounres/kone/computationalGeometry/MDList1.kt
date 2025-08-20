/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.contentSize
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


private class MDList1EuclideanSpaceOverField<Number>(
    private val field: Field<Number>,
    private val dimension: UInt,
) : EuclideanSpaceOverField<Number, VectorWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>> {
    // region Constants
    override val zero: VectorWrapper<MDList1<Number>> = VectorWrapper(MDList1(dimension) { field.zero })
    // endregion
    
    // region Equality
    override fun VectorWrapper<MDList1<Number>>.equalsTo(other: VectorWrapper<MDList1<Number>>): Boolean {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return (0u ..< dimension).all { field { this.vector[it] eq other.vector[it] } }
    }
    override fun VectorWrapper<MDList1<Number>>.isZero(): Boolean {
        require(this.vector.contentSize == dimension) { TODO() }
        return this.vector.all { field { it.isZero() } }
    }
    // FIXME: KT-5351
    override fun VectorWrapper<MDList1<Number>>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun VectorWrapper<MDList1<Number>>.times(other: UInt): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it * other } })
    }
    // endregion
    
    // region Vector-Int operations
    override operator fun VectorWrapper<MDList1<Number>>.times(other: Int): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it * other } })
    }
    // endregion
    
    // region Vector-Long operations
    override operator fun VectorWrapper<MDList1<Number>>.times(other: Long): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it * other } })
    }
    // endregion
    
    // region Vector-ULong operations
    override operator fun VectorWrapper<MDList1<Number>>.times(other: ULong): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it * other } })
    }
    // endregion
    
    // region Vector-Number operations
    override fun VectorWrapper<MDList1<Number>>.times(other: Number): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it * other } })
    }
    override fun VectorWrapper<MDList1<Number>>.div(other: Number): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { it / other } })
    }
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(other.vector.map { field { this * it } })
    }
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(other.vector.map { field { this * it } })
    }
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(other.vector.map { field { this * it } })
    }
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(other.vector.map { field { this * it } })
    }
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(other.vector.map { field { this * it } })
    }
    // endregion
    
    // region Vector-Vector operations
    override operator fun VectorWrapper<MDList1<Number>>.unaryMinus(): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(this.vector.map { field { -it } })
    }
    override operator fun VectorWrapper<MDList1<Number>>.plus(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(MDList1(dimension) { field { this.vector[it] + other.vector[it] } })
    }
    override operator fun VectorWrapper<MDList1<Number>>.minus(other: VectorWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(MDList1(dimension) { field { this.vector[it] - other.vector[it] } })
    }
    // endregion
    
    override fun PointWrapper<MDList1<Number>>.plus(other: VectorWrapper<MDList1<Number>>): PointWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return PointWrapper(MDList1(dimension) { field { this.vector[it] + other.vector[it] } })
    }
    
    override fun VectorWrapper<MDList1<Number>>.plus(other: PointWrapper<MDList1<Number>>): PointWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return PointWrapper(MDList1(dimension) { field { this.vector[it] + other.vector[it] } })
    }
    
    override fun PointWrapper<MDList1<Number>>.minus(other: VectorWrapper<MDList1<Number>>): PointWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return PointWrapper(MDList1(dimension) { field { this.vector[it] - other.vector[it] } })
    }
    
    override fun PointWrapper<MDList1<Number>>.minus(other: PointWrapper<MDList1<Number>>): VectorWrapper<MDList1<Number>> {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return VectorWrapper(MDList1(dimension) { field { this.vector[it] - other.vector[it] } })
    }
    
    override fun VectorWrapper<MDList1<Number>>.dot(other: VectorWrapper<MDList1<Number>>): Number {
        require(this.vector.contentSize == dimension) { TODO() }
        require(other.vector.contentSize == dimension) { TODO() }
        return field { (0u ..< dimension).toKoneList().sumOf { this.vector[it] * other.vector[it] } }
    }
}

public fun <Number> EuclideanSpaceOverField.Companion.mdList1(field: Field<Number>, dimension: UInt): EuclideanSpaceOverField<Number, VectorWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>> =
    MDList1EuclideanSpaceOverField(field, dimension)

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Number> EuclideanSpaceOverField.Companion.setMDList1For(numberType: SuppliedType, dimension: UInt): Unit = with(koneContextRegistryBuilder) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    EuclideanSpaceOverField.Key<Number, VectorWrapper<MDList1<Number>>, PointWrapper<MDList1<Number>>>(
        numberType,
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.VectorWrapper",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList1",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = numberType
                            )
                        ),
                        isNullable = false,
                    ),
                )
            ),
            isNullable = false,
        ),
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.PointWrapper",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.multidimensionalCollections.MDList1",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = numberType
                            )
                        ),
                        isNullable = false,
                    ),
                )
            ),
            isNullable = false,
        ),
    ) correspondsTo EuclideanSpaceOverField.mdList1(this[Field.Key<Number>(numberType)], dimension)
}