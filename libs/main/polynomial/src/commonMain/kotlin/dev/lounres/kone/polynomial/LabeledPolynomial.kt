/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.empty
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.map.relations.equality
import dev.lounres.kone.collections.map.relations.hashing
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.computeOnOrElse
import dev.lounres.kone.collections.utils.copyMapToBy
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.collections.utils.copyToBy
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.collections.utils.mergeByReified
import dev.lounres.kone.collections.utils.mergingAll
import dev.lounres.kone.collections.utils.setOrChange
import dev.lounres.kone.collections.utils.sortedWith
import dev.lounres.kone.collections.utils.withSetOrChangedReified
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.eq
import kotlin.jvm.JvmInline
import kotlin.reflect.KProperty


@JvmInline
public value class LabeledVariable(public val name: String) {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): LabeledVariable = this
    
    override fun toString(): String = name
    
    public companion object {
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): LabeledVariable = LabeledVariable(property.name)
    }
}

// TODO: Check reifications.
// TODO: Think about `Order`s for creating maps.

public typealias LabeledMonomialSignature = KoneReifiedMap<LabeledVariable, UInt>
public typealias LabeledPolynomialCoefficients<C> = KoneReifiedMap<LabeledMonomialSignature, C>

@PublishedApi
internal val labeledMonomialSignatureEquality: Equality<LabeledMonomialSignature> = KoneMap.equality(Equality.defaultFor(), Equality.defaultFor())
@PublishedApi
internal val labeledMonomialSignatureHashing: Hashing<KoneReifiedMap<LabeledVariable, UInt>> = KoneMap.hashing(keyHashing = Hashing.defaultFor(), valueHashing = Hashing.defaultFor())

public data class LabeledPolynomial<C>
@PublishedApi
internal constructor(
    public val coefficients: LabeledPolynomialCoefficients<C>
) {
    override fun toString(): String = "LabeledPolynomial$coefficients"

    public object signatureComparator {
        public fun lexBy(variableComparator: Comparator<LabeledVariable>): Comparator<LabeledMonomialSignature> =
            Comparator { left: KoneMap<LabeledVariable, UInt>, right: KoneMap<LabeledVariable, UInt> ->
                variableComparator
                if (left === right) return@Comparator ComparisonResult.Equal
                
                val commonVariables =
                    KoneReifiedSet.build<LabeledVariable> {
                        +left.keys
                        +right.keys
                    }.sortedWith(variableComparator)
                
                for (variable in commonVariables) {
                    val leftDeg = left.getOrElse(variable) { 0u }
                    val rightDeg = right.getOrElse(variable) { 0u }
                    val comparisonResult = Comparator.defaultFor<UInt>().compare(leftDeg, rightDeg)
                    if (comparisonResult != ComparisonResult.Equal) return@Comparator comparisonResult
                }
                
                return@Comparator ComparisonResult.Equal
            }
        public val lex: Comparator<LabeledMonomialSignature> = lexBy { left: LabeledVariable, right: LabeledVariable -> Comparator.defaultFor<String>().compare(left.name, right.name) }

        public fun deglexBy(variableComparator: Comparator<LabeledVariable>): Comparator<LabeledMonomialSignature> =
            Comparator { left: KoneReifiedMap<LabeledVariable, UInt>, right: KoneReifiedMap<LabeledVariable, UInt> ->
                val degComparisonResult = Comparator.defaultFor<UInt>().compare(left.valuesView.fold(0u) { acc, i -> acc + i }, right.valuesView.fold(0u) { acc, i -> acc + i })
                if (degComparisonResult != ComparisonResult.Equal) return@Comparator degComparisonResult
                return@Comparator lexBy(variableComparator).compare(left, right)
            }
        public val deglex: Comparator<LabeledMonomialSignature> = deglexBy { left: LabeledVariable, right: LabeledVariable -> Comparator.defaultFor<String>().compare(left.name, right.name) }

//        public fun degrevlexBy(variableComparator: Comparator<LabeledVariable>): Comparator<LabeledMonomialSignature> =
//            Comparator { o1: Map<LabeledVariable, UInt>, o2: Map<LabeledVariable, UInt> -> o1.values.sum().compareTo(o2.values.sum()) } then lexBy(variableComparator).reversed()
//        public val degrevlex: Comparator<LabeledMonomialSignature> = degrevlexBy { o1: LabeledVariable, o2: LabeledVariable -> o1.identity.compareTo(o2.identity) }

    }
}

public open class LabeledPolynomialSpace<Number>(
    protected open val ring: CommutativeRing<Number>,
) : CommutativeAlgebra<Number, LabeledPolynomial<Number>> {
    @PublishedApi
    internal fun KoneMutableReifiedMap<LabeledMonomialSignature, Number>.cleanZeroCoefficientsOut() {
        removeAllThat { _, value -> ring { value.isZero() } }
    }
    
    override val zero: LabeledPolynomial<Number> = LabeledPolynomialAsIs()
    override val one: LabeledPolynomial<Number> = ring.one.asLabeledPolynomial()
    
    override fun valueOf(arg: Int): LabeledPolynomial<Number> = ring.valueOf(arg).asLabeledPolynomial()
    override fun valueOf(arg: Long): LabeledPolynomial<Number> = ring.valueOf(arg).asLabeledPolynomial()
    override fun valueOf(arg: UInt): LabeledPolynomial<Number> = ring.valueOf(arg).asLabeledPolynomial()
    override fun valueOf(arg: ULong): LabeledPolynomial<Number> = ring.valueOf(arg).asLabeledPolynomial()
    override fun valueOf(arg: Number): LabeledPolynomial<Number> = arg.asLabeledPolynomial()

    public override infix fun LabeledPolynomial<Number>.equalsTo(other: LabeledPolynomial<Number>): Boolean =
        ring { mergingAll(this.coefficients, other.coefficients, { it.value.isZero() }, { it.value.isZero() }) { _, c1, c2 -> c1 eq c2 } }
    public override fun LabeledPolynomial<Number>.isZero(): Boolean = coefficients.valuesView.all { ring { it.isZero() } }
    public override fun LabeledPolynomial<Number>.isOne(): Boolean = coefficients.nodesView.all { it.key.isEmpty() || ring { it.value.isZero() } }

    public operator fun LabeledVariable.plus(other: Int): LabeledPolynomial<Number> =
        if (other == 0) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(other),
        )
    public operator fun LabeledVariable.minus(other: Int): LabeledPolynomial<Number> =
        if (other == 0) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(-other),
        )
    public operator fun LabeledVariable.times(other: Int): LabeledPolynomial<Number> =
        if (other == 0) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(other),
        )
    
    public operator fun LabeledVariable.plus(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(other),
        )
    public operator fun LabeledVariable.minus(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { -ring.valueOf(other) },
        )
    public operator fun LabeledVariable.times(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(other),
        )

    public operator fun LabeledVariable.plus(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(other),
        )
    public operator fun LabeledVariable.minus(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { -ring.valueOf(other) },
        )
    public operator fun LabeledVariable.times(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(other),
        )
    
    public operator fun LabeledVariable.plus(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(other),
        )
    public operator fun LabeledVariable.minus(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { -ring.valueOf(other) },
        )
    public operator fun LabeledVariable.times(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(other),
        )

    public operator fun Int.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(this@plus),
        )
    public operator fun Int.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { this@minus * ring.one },
        )
    public operator fun Int.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(this@times),
        )
    
    public operator fun UInt.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(this@plus),
        )
    public operator fun UInt.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { ring.one * this@minus },
        )
    public operator fun UInt.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(this@times),
        )

    public operator fun Long.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(this@plus),
        )
    public operator fun Long.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { ring.one * this@minus },
        )
    public operator fun Long.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(this@times),
        )
    
    public operator fun ULong.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring.valueOf(this@plus),
        )
    public operator fun ULong.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { ring.one * this@minus },
        )
    public operator fun ULong.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.valueOf(this@times),
        )

    public override operator fun LabeledPolynomial<Number>.plus(other: Int): LabeledPolynomial<Number> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(other) }
                ) { it -> ring { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: Int): LabeledPolynomial<Number> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring { -ring.valueOf(other) } }
                ) { it -> ring { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.times(other: Int): LabeledPolynomial<Number> =
        when(other) {
            0 -> zero
            1 -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { entry.value * other } }
            )
        }
    
    public override operator fun LabeledPolynomial<Number>.plus(other: UInt): LabeledPolynomial<Number> =
        when {
            other == 0u -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(other) }
                ) { it -> ring { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: UInt): LabeledPolynomial<Number> =
        when {
            other == 0u -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring { -ring.valueOf(other) } }
                ) { it -> ring { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.times(other: UInt): LabeledPolynomial<Number> =
        when(other) {
            0u -> zero
            1u -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { entry.value * other } }
            )
        }

    public override operator fun LabeledPolynomial<Number>.plus(other: Long): LabeledPolynomial<Number> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(other) }
                ) { it -> ring { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: Long): LabeledPolynomial<Number> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring { -ring.valueOf(other) } }
                ) { it -> ring { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.times(other: Long): LabeledPolynomial<Number> =
        when(other) {
            0L -> zero
            1L -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { entry.value * other } }
            )
        }
    
    public override operator fun LabeledPolynomial<Number>.plus(other: ULong): LabeledPolynomial<Number> =
        when {
            other == 0uL -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(other) }
                ) { it -> ring { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: ULong): LabeledPolynomial<Number> =
        when {
            other == 0uL -> this
            coefficients.isEmpty() -> ring.valueOf(other).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring { -ring.valueOf(other) } }
                ) { it -> ring { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.times(other: ULong): LabeledPolynomial<Number> =
        when(other) {
            0uL -> zero
            1uL -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { entry.value * other } }
            )
        }

    public override operator fun Int.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0 -> other
            other.coefficients.isEmpty() -> ring.valueOf(this@plus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(this@plus) }
                ) { it -> ring { this@plus + it } }
            )
        }
    public override operator fun Int.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> ring.valueOf(this@minus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                KoneReifiedMap.build(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(KoneReifiedMap.empty(), other.coefficients.computeOnOrElse(KoneReifiedMap.empty(), { ring.valueOf(this@minus) }) { it -> ring { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }) { _, currentC, _ -> currentC }
                    cleanZeroCoefficientsOut()
                }
            )
        }
    public override operator fun Int.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when(this) {
            0 -> zero
            1 -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { this@times * entry.value } }
            )
        }
    
    public override operator fun UInt.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0u -> other
            other.coefficients.isEmpty() -> ring.valueOf(this@plus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(this@plus) }
                ) { it -> ring { this@plus + it } }
            )
        }
    public override operator fun UInt.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0u -> -other
            other.coefficients.isEmpty() -> ring.valueOf(this@minus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                KoneReifiedMap.build(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(KoneReifiedMap.empty(), other.coefficients.computeOnOrElse(KoneReifiedMap.empty(), { ring.valueOf(this@minus) }) { it -> ring { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }) { _, currentC, _ -> currentC }
                    cleanZeroCoefficientsOut()
                }
            )
        }
    public override operator fun UInt.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when(this) {
            0u -> zero
            1u -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { this@times * entry.value } }
            )
        }

    public override operator fun Long.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0L -> other
            other.coefficients.isEmpty() -> ring.valueOf(this@plus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(this@plus) },
                    transformOnChange = { ring { this@plus + it } }
                )
            )
        }
    public override operator fun Long.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0L -> -other
            other.coefficients.isEmpty() -> ring.valueOf(this@minus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                KoneReifiedMap.build(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(KoneReifiedMap.empty(), other.coefficients.computeOnOrElse(KoneReifiedMap.empty(), { ring.valueOf(this@minus) }, { ring { this@minus - it } }))
                    other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }) { _, currentC, _ -> currentC }
                    cleanZeroCoefficientsOut()
                }
            )
        }
    public override operator fun Long.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when(this) {
            0L -> zero
            1L -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { this@times * entry.value } }
            )
        }
    
    public override operator fun ULong.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0uL -> other
            other.coefficients.isEmpty() -> ring.valueOf(this@plus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = KoneReifiedMap.empty(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { ring.valueOf(this@plus) },
                    transformOnChange = { ring { this@plus + it } }
                )
            )
        }
    public override operator fun ULong.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0uL -> -other
            other.coefficients.isEmpty() -> ring.valueOf(this@minus).asLabeledPolynomial()
            else -> LabeledPolynomialAsIs(
                KoneReifiedMap.build(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(KoneReifiedMap.empty(), other.coefficients.computeOnOrElse(KoneReifiedMap.empty(), { ring.valueOf(this@minus) }, { ring { this@minus - it } }) )
                    other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }) { _, currentC, _ -> currentC }
                    cleanZeroCoefficientsOut()
                }
            )
        }
    public override operator fun ULong.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when(this) {
            0uL -> zero
            1uL -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValuesReified(
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) { entry -> ring { this@times * entry.value } }
            )
        }

    public operator fun LabeledVariable.plus(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo other,
        )
    public operator fun LabeledVariable.minus(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo ring { -other },
        )
    public operator fun LabeledVariable.times(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this@times mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo other,
        )

    public operator fun Number.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo this@plus,
        )
    public operator fun Number.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
            KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo this@minus,
        )
    public operator fun Number.times(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo this@times,
        )

    override operator fun LabeledPolynomial<Number>.plus(other: Number): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.empty(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { other },
                transformOnChange = { ring { it + other } }
            )
        )
    override operator fun LabeledPolynomial<Number>.minus(other: Number): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.empty(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { ring { -other } },
                transformOnChange = { ring { it - other } }
            )
        )
    override operator fun LabeledPolynomial<Number>.times(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { ring { it.value * other } }
        )

    override operator fun Number.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@plus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.empty(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { this@plus },
                transformOnChange = { ring { this@plus + it } }
            )
        )
    override operator fun Number.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@minus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            KoneReifiedMap.build(
                other.coefficients.size + 1u,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                set(KoneReifiedMap.empty(), this@minus)
                other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }, { _, currentC, newC -> ring { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    override operator fun Number.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            other.coefficients.mapValuesReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { ring { this@times * it.value } }
        )
    
    public operator fun LabeledVariable.unaryPlus(): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
    public operator fun LabeledVariable.unaryMinus(): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
    public operator fun LabeledVariable.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { ring.one * 2 }
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )
    public operator fun LabeledVariable.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) zero
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { -ring.one },
        )
    public operator fun LabeledVariable.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 2U, keyHashing = Hashing.defaultFor()) mapsTo ring.one
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one,
        )

    public operator fun LabeledVariable.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one
        )
        else LabeledPolynomialAsIs(
            other.coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.of(this@plus mapsTo 1U, keyHashing = Hashing.defaultFor()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { ring.one },
                transformOnChange = { ring { ring.one + it } }
            )
        )
    public operator fun LabeledVariable.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one
        )
        else LabeledPolynomialAsIs(
            KoneReifiedMap.build(
                other.coefficients.size + 1u,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                set(KoneReifiedMap.of(this@minus mapsTo 1U, keyHashing = Hashing.defaultFor()), ring.one)
                other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }, { _, currentC, newC -> ring { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    public operator fun LabeledVariable.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            other.coefficients.mapKeysReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { entry -> entry.key.withSetOrChangedReified(key = this, valueOnSet = { 1u }, transformOnChange = { it + 1u }) }
        )

    public operator fun LabeledPolynomial<Number>.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one
        )
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { ring.one },
                transformOnChange = { ring { it + ring.one } }
            )
        )
    public operator fun LabeledPolynomial<Number>.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) LabeledPolynomialAsIs(
            KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring.one
        )
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = KoneReifiedMap.of(other mapsTo 1U, keyHashing = Hashing.defaultFor()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { ring { -ring.one } },
                transformOnChange = { ring { it - ring.one } }
            )
        )
    public operator fun LabeledPolynomial<Number>.times(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapKeysReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { entry -> entry.key.withSetOrChangedReified(key = other, valueOnSet = { 1u }, transformOnChange = { it + 1u }) }
        )

    override fun LabeledPolynomial<Number>.unaryMinus(): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { ring { -it.value } }
        )
    override operator fun LabeledPolynomial<Number>.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            mergeByReified(
                coefficients,
                other.coefficients,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { _, c1, c2 -> ring { c1 + c2 } }
        )
    override operator fun LabeledPolynomial<Number>.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.build(
                coefficients.size + other.coefficients.size,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { entry -> ring { -entry.value } }, { _, currentC, newC -> ring { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    override operator fun LabeledPolynomial<Number>.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.build(
                coefficients.size * other.coefficients.size,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                for (entry1 in coefficients.nodesView) {
                    val degs1 = entry1.key
                    val c1 = entry1.value
                    for (entry2 in other.coefficients.nodesView) {
                        val degs2 = entry2.key
                        val c2 = entry2.value
                        val degs = mergeByReified(degs1, degs2) { _, deg1, deg2 -> deg1 + deg2 }
                        val c = ring { c1 * c2 }
                        this.setOrChange(degs, { c }, { ring { it + c } })
                    }
                }
                cleanZeroCoefficientsOut()
            }
        )

    public val LabeledPolynomial<Number>.degree: UInt
        get() = ring {
            val iterator = coefficients.nodesView.iterator()
            var maxDegree: UInt
            while (true) {
                if (!iterator.hasNext()) zeroPolynomialDegreeException()
                val next = iterator.getNext()
                if (next.value.isNotZero()) {
                    maxDegree = next.key.valuesView.fold(0u) { acc, deg -> acc + deg }
                    break
                }
                iterator.moveNext()
            }
            for (next in iterator) if (next.value.isNotZero()) {
                maxDegree = maxOf(maxDegree, next.key.valuesView.fold(0u) { acc, deg -> acc + deg })
            }
            maxDegree
        }
    public val LabeledPolynomial<Number>.degrees: LabeledMonomialSignature
        get() =
            KoneReifiedMap.build {
                var foundNonZeroCoef = false
                coefficients.nodesView.forEach { entry ->
                    val degs = entry.key
                    val coef = entry.value
                    if (ring { coef.isNotZero() }) {
                        foundNonZeroCoef = true
                        degs.copyToBy(this) { _, currentDeg, newDeg -> maxOf(currentDeg, newDeg) }
                    }
                }
                if (!foundNonZeroCoef) zeroPolynomialDegreeException()
            }
    public fun LabeledPolynomial<Number>.degreeBy(variable: LabeledVariable): UInt = ring {
        val iterator = coefficients.nodesView.iterator()
        var maxDegree: UInt
        while (true) {
            if (!iterator.hasNext()) zeroPolynomialDegreeException()
            val next = iterator.getNext()
            if (next.value.isNotZero()) {
                maxDegree = next.key.getOrElse(variable) { 0u }
                break
            }
        }
        for (next in iterator) if (next.value.isNotZero()) {
            maxDegree = maxOf(maxDegree, next.key.getOrElse(variable) { 0u })
        }
        maxDegree
    }
    public fun LabeledPolynomial<Number>.degreeBy(variables: KoneSet<LabeledVariable>): UInt = ring {
        val iterator = coefficients.nodesView.iterator()
        var maxDegree: UInt
        while (true) {
            if (!iterator.hasNext()) zeroPolynomialDegreeException()
            val next = iterator.getNext()
            if (next.value.isNotZero()) {
                maxDegree = next.key.nodesView.fold(0u) { acc, entry -> if (entry.key in variables) acc + entry.value else acc }
                break
            }
        }
        for (next in iterator) if (next.value.isNotZero()) {
            maxDegree = maxOf(maxDegree, next.key.nodesView.fold(0u) { acc, entry -> if (entry.key in variables) acc + entry.value else acc })
        }
        maxDegree
    }
    public val LabeledPolynomial<Number>.variables: KoneSet<LabeledVariable>
        get() =
            KoneSet.build {
                coefficients.nodesView.forEach { entry ->
                    val degs = entry.key
                    val coef = entry.value
                    if (ring { coef.isNotZero() }) +degs.keys
                }
            }
    public val LabeledPolynomial<Number>.numberOfVariables: UInt get() = variables.size
}

public class LabeledPolynomialSpaceOverField<Number>(
    override val ring: Field<Number>,
) : LabeledPolynomialSpace<Number>(ring)/*, EuclideanRing<LabeledPolynomial<Number>>*/ {
    // region Number-Int operations
    public operator fun LabeledPolynomial<Number>.div(other: Int): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { ring { it.value / other } }
        )
    // endregion
    
    // region Number-UInt operations
    public operator fun LabeledPolynomial<Number>.div(other: UInt): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { ring { it.value / other } }
        )
    // endregion
    
    // region Number-Long operations
    public operator fun LabeledPolynomial<Number>.div(other: Long): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { ring { it.value / other } }
        )
    // endregion
    
    // region Number-ULong operations
    public operator fun LabeledPolynomial<Number>.div(other: ULong): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { ring { it.value / other } }
        )
    // endregion
    
    // region Polynomial-Number operations
    public operator fun LabeledPolynomial<Number>.div(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { ring { it.value / other } }
        )
    // endregion
    
    // region Variable-Int operations
    public operator fun LabeledVariable.div(other: Int): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { ring.valueOf(other).reciprocal() },
        )
    // endregion
    
    // region Variable-UInt operations
    public operator fun LabeledVariable.div(other: UInt): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { ring.valueOf(other).reciprocal() },
        )
    // endregion
    
    // region Variable-Long operations
    public operator fun LabeledVariable.div(other: Long): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { ring.valueOf(other).reciprocal() },
        )
    // endregion
    
    // region Variable-ULong operations
    public operator fun LabeledVariable.div(other: ULong): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { ring.valueOf(other).reciprocal() },
        )
    // endregion
    
    // region Variable-Number operations
    public operator fun LabeledVariable.div(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            KoneReifiedMap.of(this mapsTo 1U, keyHashing = Hashing.defaultFor()) mapsTo ring { other.reciprocal() },
        )
    // endregion
}