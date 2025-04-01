/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.map.relations.koneMapEquality
import dev.lounres.kone.collections.map.relations.koneMapHashing
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.buildKoneReifiedSet
import dev.lounres.kone.collections.set.buildKoneSet
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
import dev.lounres.kone.context
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.defaultComparator
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.eq
import kotlin.jvm.JvmInline
import kotlin.math.max
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
internal val labeledMonomialSignatureEquality: Equality<LabeledMonomialSignature> = koneMapEquality(defaultEquality(), defaultEquality())
@PublishedApi
internal val labeledMonomialSignatureHashing: Hashing<KoneReifiedMap<LabeledVariable, UInt>> = koneMapHashing(keyHashing = defaultHashing(), valueHashing = defaultHashing())

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
                    buildKoneReifiedSet {
                        addAllFrom(left.keys)
                        addAllFrom(right.keys)
                    }.sortedWith(variableComparator)
                
                for (variable in commonVariables) {
                    val leftDeg = left.getOrElse(variable) { 0u }
                    val rightDeg = right.getOrElse(variable) { 0u }
                    val comparisonResult = defaultComparator<UInt>().compare(leftDeg, rightDeg)
                    if (comparisonResult != ComparisonResult.Equal) return@Comparator comparisonResult
                }
                
                return@Comparator ComparisonResult.Equal
            }
        public val lex: Comparator<LabeledMonomialSignature> = lexBy { left: LabeledVariable, right: LabeledVariable -> defaultComparator<String>().compare(left.name, right.name) }

        public fun deglexBy(variableComparator: Comparator<LabeledVariable>): Comparator<LabeledMonomialSignature> =
            Comparator { left: KoneReifiedMap<LabeledVariable, UInt>, right: KoneReifiedMap<LabeledVariable, UInt> ->
                val degComparisonResult = defaultComparator<UInt>().compare(left.valuesView.fold(0u) { acc, i -> acc + i }, right.valuesView.fold(0u) { acc, i -> acc + i })
                if (degComparisonResult != ComparisonResult.Equal) return@Comparator degComparisonResult
                return@Comparator lexBy(variableComparator).compare(left, right)
            }
        public val deglex: Comparator<LabeledMonomialSignature> = deglexBy { left: LabeledVariable, right: LabeledVariable -> defaultComparator<String>().compare(left.name, right.name) }

//        public fun degrevlexBy(variableComparator: Comparator<LabeledVariable>): Comparator<LabeledMonomialSignature> =
//            Comparator { o1: Map<LabeledVariable, UInt>, o2: Map<LabeledVariable, UInt> -> o1.values.sum().compareTo(o2.values.sum()) } then lexBy(variableComparator).reversed()
//        public val degrevlex: Comparator<LabeledMonomialSignature> = degrevlexBy { o1: LabeledVariable, o2: LabeledVariable -> o1.identity.compareTo(o2.identity) }

    }
}

public open class LabeledPolynomialSpace<Number>(
    protected open val numberContext: Ring<Number>,
) : MultivariatePolynomialSpace<Number, LabeledVariable, LabeledPolynomial<Number>> {
    @PublishedApi
    internal fun KoneMutableReifiedMap<LabeledMonomialSignature, Number>.cleanZeroCoefficientsOut() {
        removeAllThat { _, value -> numberContext { value.isZero() } }
    }
    
    final override val numberZero: Number get() = numberContext.zero
    final override val numberOne: Number get() = numberContext.one
    
    final override fun numberValueOf(arg: Int): Number = numberContext.valueOf(arg)
    final override fun numberValueOf(arg: UInt): Number = numberContext.valueOf(arg)
    final override fun numberValueOf(arg: Long): Number = numberContext.valueOf(arg)
    final override fun numberValueOf(arg: ULong): Number = numberContext.valueOf(arg)
    final override val Int.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val UInt.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val Long.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val ULong.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    
    override val zero: LabeledPolynomial<Number> = LabeledPolynomialAsIs()
    override val one: LabeledPolynomial<Number> by lazy { numberOne.asLabeledPolynomial() }

    public override infix fun LabeledPolynomial<Number>.equalsTo(other: LabeledPolynomial<Number>): Boolean =
        numberContext { mergingAll(this.coefficients, other.coefficients, { it.value.isZero() }, { it.value.isZero() }) { _, c1, c2 -> c1 eq c2 } }
    public override fun LabeledPolynomial<Number>.isZero(): Boolean = coefficients.valuesView.all { context(numberContext) { it.isZero() } }
    public override fun LabeledPolynomial<Number>.isOne(): Boolean = coefficients.nodesView.all { it.key.isEmpty() || context(numberContext) { it.value.isZero() } }

    public override fun polynomialValueOf(value: Number): LabeledPolynomial<Number> = value.asLabeledPolynomial()
    
    public override fun polynomialValueOf(variable: LabeledVariable): LabeledPolynomial<Number> = context(numberContext) { variable.asLabeledPolynomial() }

    public override operator fun LabeledVariable.plus(other: Int): LabeledPolynomial<Number> =
        if (other == 0) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo other.numberValue,
        )
    public override operator fun LabeledVariable.minus(other: Int): LabeledPolynomial<Number> =
        if (other == 0) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo (-other).numberValue,
        )
    public override operator fun LabeledVariable.times(other: Int): LabeledPolynomial<Number> =
        if (other == 0) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo other.numberValue,
        )
    
    public override operator fun LabeledVariable.plus(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo other.numberValue,
        )
    public override operator fun LabeledVariable.minus(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { -other.numberValue },
        )
    public override operator fun LabeledVariable.times(other: UInt): LabeledPolynomial<Number> =
        if (other == 0u) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo other.numberValue,
        )

    public override operator fun LabeledVariable.plus(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo other.numberValue,
        )
    public override operator fun LabeledVariable.minus(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo (-other).numberValue,
        )
    public override operator fun LabeledVariable.times(other: Long): LabeledPolynomial<Number> =
        if (other == 0L) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo other.numberValue,
        )
    
    public override operator fun LabeledVariable.plus(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo other.numberValue,
        )
    public override operator fun LabeledVariable.minus(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { -other.numberValue },
        )
    public override operator fun LabeledVariable.times(other: ULong): LabeledPolynomial<Number> =
        if (other == 0uL) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo other.numberValue,
        )

    public override operator fun Int.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@plus.numberValue,
        )
    public override operator fun Int.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { this@minus * numberOne },
        )
    public override operator fun Int.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo this@times.numberValue,
        )
    
    public override operator fun UInt.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@plus.numberValue,
        )
    public override operator fun UInt.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { numberOne * this@minus },
        )
    public override operator fun UInt.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0u) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo this@times.numberValue,
        )

    public override operator fun Long.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@plus.numberValue,
        )
    public override operator fun Long.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { numberOne * this@minus },
        )
    public override operator fun Long.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0L) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo this@times.numberValue,
        )
    
    public override operator fun ULong.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@plus.numberValue,
        )
    public override operator fun ULong.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { numberOne * this@minus },
        )
    public override operator fun ULong.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == 0uL) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo this@times.numberValue,
        )

    public override operator fun LabeledPolynomial<Number>.plus(other: Int): LabeledPolynomial<Number> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { other.numberValue }
                ) { it -> context(numberContext) { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: Int): LabeledPolynomial<Number> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { (-other).numberValue }
                ) { it -> context(numberContext) { it - other } }
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
                ) { entry -> context(numberContext) { entry.value * other } }
            )
        }
    
    public override operator fun LabeledPolynomial<Number>.plus(other: UInt): LabeledPolynomial<Number> =
        when {
            other == 0u -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { other.numberValue }
                ) { it -> context(numberContext) { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: UInt): LabeledPolynomial<Number> =
        when {
            other == 0u -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { context(numberContext) { -other.numberValue } }
                ) { it -> context(numberContext) { it - other } }
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
                ) { entry -> context(numberContext) { entry.value * other } }
            )
        }

    public override operator fun LabeledPolynomial<Number>.plus(other: Long): LabeledPolynomial<Number> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { other.numberValue }
                ) { it -> context(numberContext) { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: Long): LabeledPolynomial<Number> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { (-other).numberValue }
                ) { it -> context(numberContext) { it - other } }
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
                ) { entry -> context(numberContext) { entry.value * other } }
            )
        }
    
    public override operator fun LabeledPolynomial<Number>.plus(other: ULong): LabeledPolynomial<Number> =
        when {
            other == 0uL -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { other.numberValue }
                ) { it -> context(numberContext) { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<Number>.minus(other: ULong): LabeledPolynomial<Number> =
        when {
            other == 0uL -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { context(numberContext) { -other.numberValue } }
                ) { it -> context(numberContext) { it - other } }
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
                ) { entry -> context(numberContext) { entry.value * other } }
            )
        }

    public override operator fun Int.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0 -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { this@plus.numberValue }
                ) { it -> context(numberContext) { this@plus + it } }
            )
        }
    public override operator fun Int.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildKoneReifiedMap(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(emptyKoneReifiedMap(), other.coefficients.computeOnOrElse(emptyKoneReifiedMap(), { this@minus.numberValue }) { it -> context(numberContext) { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }) { _, currentC, _ -> currentC }
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
                ) { entry -> context(numberContext) { this@times * entry.value } }
            )
        }
    
    public override operator fun UInt.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0u -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { this@plus.numberValue }
                ) { it -> context(numberContext) { this@plus + it } }
            )
        }
    public override operator fun UInt.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0u -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildKoneReifiedMap(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(emptyKoneReifiedMap(), other.coefficients.computeOnOrElse(emptyKoneReifiedMap(), { this@minus.numberValue }) { it -> context(numberContext) { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }) { _, currentC, _ -> currentC }
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
                ) { entry -> context(numberContext) { this@times * entry.value } }
            )
        }

    public override operator fun Long.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0L -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { this@plus.numberValue },
                    transformOnChange = { context(numberContext) { this@plus + it } }
                )
            )
        }
    public override operator fun Long.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0L -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildKoneReifiedMap(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(emptyKoneReifiedMap(), other.coefficients.computeOnOrElse(emptyKoneReifiedMap(), { this@minus.numberValue }, { context(numberContext) { this@minus - it } }))
                    other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }) { _, currentC, _ -> currentC }
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
                ) { entry -> context(numberContext) { this@times * entry.value } }
            )
        }
    
    public override operator fun ULong.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0uL -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withSetOrChangedReified(
                    key = emptyKoneReifiedMap(),
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                    valueOnSet = { this@plus.numberValue },
                    transformOnChange = { context(numberContext) { this@plus + it } }
                )
            )
        }
    public override operator fun ULong.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        when {
            this == 0uL -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildKoneReifiedMap(
                    other.coefficients.size + 1u,
                    keyEquality = labeledMonomialSignatureEquality,
                    keyHashing = labeledMonomialSignatureHashing,
                ) {
                    set(emptyKoneReifiedMap(), other.coefficients.computeOnOrElse(emptyKoneReifiedMap(), { this@minus.numberValue }, { context(numberContext) { this@minus - it } }) )
                    other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }) { _, currentC, _ -> currentC }
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
                ) { entry -> context(numberContext) { this@times * entry.value } }
            )
        }

    public override operator fun LabeledVariable.plus(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo other,
        )
    public override operator fun LabeledVariable.minus(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo context(numberContext) { -other },
        )
    public override operator fun LabeledVariable.times(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this@times mapsTo 1U, keyHashing = defaultHashing()) mapsTo other,
        )

    public override operator fun Number.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@plus,
        )
    public override operator fun Number.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
            emptyKoneReifiedMap<LabeledVariable, UInt>() mapsTo this@minus,
        )
    public override operator fun Number.times(other: LabeledVariable): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo this@times,
        )

    override operator fun LabeledPolynomial<Number>.plus(other: Number): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = emptyKoneReifiedMap(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { other },
                transformOnChange = { context(numberContext) { it + other } }
            )
        )
    override operator fun LabeledPolynomial<Number>.minus(other: Number): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = emptyKoneReifiedMap(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { context(numberContext) { -other } },
                transformOnChange = { context(numberContext) { it - other } }
            )
        )
    override operator fun LabeledPolynomial<Number>.times(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { context(numberContext) { it.value * other } }
        )

    override operator fun Number.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@plus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withSetOrChangedReified(
                key = emptyKoneReifiedMap(),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { this@plus },
                transformOnChange = { context(numberContext) { this@plus + it } }
            )
        )
    override operator fun Number.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@minus.polynomialValue
        else LabeledPolynomialAsIs(
            buildKoneReifiedMap(
                other.coefficients.size + 1u,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                set(emptyKoneReifiedMap(), this@minus)
                other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }, { _, currentC, newC -> context(numberContext) { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    override operator fun Number.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            other.coefficients.mapValuesReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { context(numberContext) { this@times * it.value } }
        )

    public override operator fun LabeledVariable.unaryMinus(): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
    public override operator fun LabeledVariable.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { numberOne * 2 }
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )
    public override operator fun LabeledVariable.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) zero
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
            koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { -numberOne },
        )
    public override operator fun LabeledVariable.times(other: LabeledVariable): LabeledPolynomial<Number> =
        if (this == other) LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 2U, keyHashing = defaultHashing()) mapsTo numberOne
        )
        else LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, other mapsTo 1U, keyHashing = defaultHashing()) mapsTo numberOne,
        )

    public override operator fun LabeledVariable.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@plus.polynomialValue
        else LabeledPolynomialAsIs(
            other.coefficients.withSetOrChangedReified(
                key = koneReifiedMapOf(this@plus mapsTo 1U, keyHashing = defaultHashing()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { numberOne },
                transformOnChange = { context(numberContext) { numberOne + it } }
            )
        )
    public override operator fun LabeledVariable.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        if (other.coefficients.isEmpty()) this@minus.polynomialValue
        else LabeledPolynomialAsIs(
            buildKoneReifiedMap(
                other.coefficients.size + 1u,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                set(koneReifiedMapOf(this@minus mapsTo 1U, keyHashing = defaultHashing()), numberOne)
                other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }, { _, currentC, newC -> context(numberContext) { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    public override operator fun LabeledVariable.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            other.coefficients.mapKeysReified(
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { entry -> entry.key.withSetOrChangedReified(key = this, valueOnSet = { 1u }, transformOnChange = { it + 1u }) }
        )

    public override operator fun LabeledPolynomial<Number>.plus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.polynomialValue
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { numberOne },
                transformOnChange = { context(numberContext) { it + numberOne } }
            )
        )
    public override operator fun LabeledPolynomial<Number>.minus(other: LabeledVariable): LabeledPolynomial<Number> =
        if (coefficients.isEmpty()) other.polynomialValue
        else LabeledPolynomialAsIs(
            coefficients.withSetOrChangedReified(
                key = koneReifiedMapOf(other mapsTo 1U, keyHashing = defaultHashing()),
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
                valueOnSet = { context(numberContext) { -numberOne } },
                transformOnChange = { context(numberContext) { it - numberOne } }
            )
        )
    public override operator fun LabeledPolynomial<Number>.times(other: LabeledVariable): LabeledPolynomial<Number> =
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
            ) { context(numberContext) { -it.value } }
        )
    override operator fun LabeledPolynomial<Number>.plus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            mergeByReified(
                coefficients,
                other.coefficients,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) { _, c1, c2 -> context(numberContext) { c1 + c2 } }
        )
    override operator fun LabeledPolynomial<Number>.minus(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            buildKoneReifiedMap(
                coefficients.size + other.coefficients.size,
                keyEquality = labeledMonomialSignatureEquality,
                keyHashing = labeledMonomialSignatureHashing,
            ) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { entry -> context(numberContext) { -entry.value } }, { _, currentC, newC -> context(numberContext) { currentC - newC } })
                cleanZeroCoefficientsOut()
            }
        )
    override operator fun LabeledPolynomial<Number>.times(other: LabeledPolynomial<Number>): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            buildKoneReifiedMap(
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
                        val c = context(numberContext) { c1 * c2 }
                        this.setOrChange(degs, { c }, { context(numberContext) { it + c } })
                    }
                }
                cleanZeroCoefficientsOut()
            }
        )

    override val LabeledPolynomial<Number>.degree: UInt
        get() = context(numberContext) {
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
                maxDegree = max(maxDegree, next.key.valuesView.fold(0u) { acc, deg -> acc + deg })
            }
            maxDegree
        }
    public override val LabeledPolynomial<Number>.degrees: LabeledMonomialSignature
        get() =
            buildKoneReifiedMap {
                var foundNonZeroCoef = false
                coefficients.nodesView.forEach { entry ->
                    val degs = entry.key
                    val coef = entry.value
                    if (context(numberContext) { coef.isNotZero() }) {
                        foundNonZeroCoef = true
                        degs.copyToBy(this) { _, currentDeg, newDeg -> max(currentDeg, newDeg) }
                    }
                }
                if (!foundNonZeroCoef) zeroPolynomialDegreeException()
            }
    public override fun LabeledPolynomial<Number>.degreeBy(variable: LabeledVariable): UInt = context(numberContext) {
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
            maxDegree = max(maxDegree, next.key.getOrElse(variable) { 0u })
        }
        maxDegree
    }
    public override fun LabeledPolynomial<Number>.degreeBy(variables: KoneSet<LabeledVariable>): UInt = context(numberContext) {
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
            maxDegree = max(maxDegree, next.key.nodesView.fold(0u) { acc, entry -> if (entry.key in variables) acc + entry.value else acc })
        }
        maxDegree
    }
    public override val LabeledPolynomial<Number>.variables: KoneSet<LabeledVariable>
        get() =
            buildKoneSet {
                coefficients.nodesView.forEach { entry ->
                    val degs = entry.key
                    val coef = entry.value
                    if (context(numberContext) { coef.isNotZero() }) addAllFrom(degs.keys)
                }
            }
    public override val LabeledPolynomial<Number>.numberOfVariables: UInt get() = variables.size
}

public class LabeledPolynomialSpaceOverField<Number>(
    override val numberContext: Field<Number>,
) : LabeledPolynomialSpace<Number>(numberContext), MultivariatePolynomialSpaceOverField<Number, LabeledVariable, LabeledPolynomial<Number>> {
    // region Number-Int operations
    override operator fun LabeledPolynomial<Number>.div(other: Int): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { context(numberContext) { it.value / other } }
        )
    // endregion
    
    // region Number-UInt operations
    override operator fun LabeledPolynomial<Number>.div(other: UInt): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { context(numberContext) { it.value / other } }
        )
    // endregion
    
    // region Number-Long operations
    override operator fun LabeledPolynomial<Number>.div(other: Long): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { context(numberContext) { it.value / other } }
        )
    // endregion
    
    // region Number-ULong operations
    override operator fun LabeledPolynomial<Number>.div(other: ULong): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { context(numberContext) { it.value / other } }
        )
    // endregion
    
    // region Polynomial-Number operations
    override operator fun LabeledPolynomial<Number>.div(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            coefficients.mapValuesReified { context(numberContext) { it.value / other } }
        )
    // endregion
    
    // region Variable-Int operations
    override operator fun LabeledVariable.div(other: Int): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { numberOne / other },
        )
    // endregion
    
    // region Variable-UInt operations
    override operator fun LabeledVariable.div(other: UInt): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { numberOne / other },
        )
    // endregion
    
    // region Variable-Long operations
    override operator fun LabeledVariable.div(other: Long): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { numberOne / other },
        )
    // endregion
    
    // region Variable-ULong operations
    override operator fun LabeledVariable.div(other: ULong): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { numberOne / other },
        )
    // endregion
    
    // region Variable-Number operations
    override operator fun LabeledVariable.div(other: Number): LabeledPolynomial<Number> =
        LabeledPolynomialAsIs(
            koneReifiedMapOf(this mapsTo 1U, keyHashing = defaultHashing()) mapsTo context(numberContext) { other.reciprocal },
        )
    // endregion
}