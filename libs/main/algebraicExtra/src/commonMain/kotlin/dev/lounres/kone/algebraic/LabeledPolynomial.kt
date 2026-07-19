/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.*
import dev.lounres.kone.relations.ComparisonResult.Equal
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@OptIn(DelicateCollectionsInheritanceAPI::class)
@JvmInline
//@Serializable // TODO: Add serializer
public value class LabeledPolynomial<out Number> @PublishedApi internal constructor(
    public val coefficients: KoneMap<MonomialSignature, Number>,
) : KoneMap<LabeledPolynomial.MonomialSignature, Number> by coefficients {
    override fun toString(): String = coefficients.nodesView.joinToString(separator = " + ") { [signature, coefficient] -> "$coefficient $signature" }
    
    @JvmInline
//    @Serializable // TODO: Add serializer
    public value class Variable(public val name: String) {
        override fun toString(): String = name
        
        public companion object {
            public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, Variable> = Delegate(Variable(property.name))
        }
        
        private class Delegate(private val variable: Variable): ReadOnlyProperty<Any?, Variable> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): Variable = variable
        }
    }
    
    @JvmInline
//    @Serializable // TODO: Add serializer
    public value class MonomialSignature @PublishedApi internal constructor(
        public val degrees: KoneMap<Variable, UInt>
    ) : KoneMap<Variable, UInt> by degrees {
        override fun toString(): String = degrees.nodesView.joinToString(separator = " ") { [variable, degree] -> "$variable^$degree" }
        
        public companion object;
    }
    
    public companion object;
    
    @RequiresOptIn(level = ERROR)
    public annotation class DelicateApi
}

public fun LabeledPolynomial.Variable.Companion.equality(): Equality<LabeledPolynomial.Variable> = Equality.defaultFor()

public fun LabeledPolynomial.Variable.Companion.hashing(): Hashing<LabeledPolynomial.Variable> = Hashing.defaultFor()

public fun LabeledPolynomial.Variable.Companion.order(
    nameOrder: Order<String> = Order.defaultFor(),
): Order<LabeledPolynomial.Variable> = Order { left, right -> nameOrder { left.name compareWith right.name} }

public fun LabeledPolynomial.Variable.Companion.comparator(
    nameOrder: Comparator<String> = Comparator.defaultFor(),
): Comparator<LabeledPolynomial.Variable> = Comparator { left, right -> nameOrder.compare(left.name, right.name) }

public fun LabeledPolynomial.Variable.Companion.lexicographicOrder(
    charactersOrder: Order<Char?> = Order.defaultFor<Char>().withNullAsLeast,
): Order<LabeledPolynomial.Variable> = Order { left, right ->
    for (i in 0 ..< maxOf(left.name.length, right.name.length)) {
        val comparisonResult = charactersOrder { left.name.getOrNull(i) compareWith right.name.getOrNull(i) }
        if (comparisonResult != Equal) return@Order comparisonResult
    }
    Equal
}

public fun LabeledPolynomial.Variable.Companion.lexicographicComparator(
    charactersOrder: Comparator<Char?> = Comparator.defaultFor<Char>().withNullAsLeast,
): Comparator<LabeledPolynomial.Variable> = Comparator { left, right ->
    for (i in 0 ..< maxOf(left.name.length, right.name.length)) {
        val comparisonResult = charactersOrder.compare(left.name.getOrNull(i), right.name.getOrNull(i))
        if (comparisonResult != Equal) return@Comparator comparisonResult
    }
    Equal
}

private val defaultLabeledPolynomialVariableOrder : Order<LabeledPolynomial.Variable> =
    LabeledPolynomial.Variable.lexicographicOrder(Order.defaultFor<Char>().withNullAsLeast)

private object LabeledPolynomialMonomialSignatureEquality : Equality<LabeledPolynomial.MonomialSignature> {
    override fun LabeledPolynomial.MonomialSignature.equalsTo(other: LabeledPolynomial.MonomialSignature): Boolean {
        if (this.degrees === other.degrees) return true
        if (this.size != other.size) return false
        for ([variable, degree] in this) if (!other.computeOnOrElse(variable, { false }) { it == degree }) return false
        return true
    }
}

public fun LabeledPolynomial.MonomialSignature.Companion.equality(): Equality<LabeledPolynomial.MonomialSignature> = LabeledPolynomialMonomialSignatureEquality

private object LabeledPolynomialMonomialSignatureHashing : Hashing<LabeledPolynomial.MonomialSignature> {
    override fun LabeledPolynomial.MonomialSignature.hash(): Int =
        fold(0) { hash, [variable, degree] -> hash xor (variable.hashCode() * 31 + degree.hashCode()) }
}

public fun LabeledPolynomial.MonomialSignature.Companion.hashing(): Hashing<LabeledPolynomial.MonomialSignature> = LabeledPolynomialMonomialSignatureHashing

public fun LabeledPolynomial.MonomialSignature.Companion.lexicographicOrder(
    variableOrder: Order<LabeledPolynomial.Variable> = LabeledPolynomial.Variable.lexicographicOrder(),
    degreeOrder: Order<UInt> = Order.defaultFor(),
): Order<LabeledPolynomial.MonomialSignature> = Order { left, right ->
    if (left.degrees === right.degrees) return@Order Equal
    
    val commonVariables = variableOrder {
        KoneSet.build(
            elementEquality = LabeledPolynomial.Variable.equality(),
            elementHashing = LabeledPolynomial.Variable.hashing(),
            elementOrder = variableOrder,
        ) {
            addAllFrom(left.keys)
            addAllFrom(right.keys)
        }.sorted() // TODO: Replace with sorted set
    }
    
    for (variable in commonVariables) {
        val leftDeg = left.getOrElse(variable) { 0u }
        val rightDeg = right.getOrElse(variable) { 0u }
        val comparisonResult = degreeOrder { leftDeg compareWith rightDeg }
        if (comparisonResult != Equal) return@Order comparisonResult
    }
    
    Equal
}

public fun LabeledPolynomial.MonomialSignature.Companion.lexicographicComparator(
    variableComparator: Comparator<LabeledPolynomial.Variable> = LabeledPolynomial.Variable.lexicographicComparator(),
    degreeComparator: Comparator<UInt> = Comparator.defaultFor(),
): Comparator<LabeledPolynomial.MonomialSignature> = Comparator { left, right ->
    if (left.degrees === right.degrees) return@Comparator Equal
    
    val commonVariables =
        KoneSet.build(
            elementEquality = LabeledPolynomial.Variable.equality(),
            elementHashing = LabeledPolynomial.Variable.hashing(),
            elementOrder = variableComparator.asOrder(),
        ) {
            addAllFrom(left.keys)
            addAllFrom(right.keys)
        }.sortedWith(variableComparator) // TODO: Replace with sorted set
    
    for (variable in commonVariables) {
        val leftDeg = left.getOrElse(variable) { 0u }
        val rightDeg = right.getOrElse(variable) { 0u }
        val comparisonResult = degreeComparator.compare(leftDeg, rightDeg)
        if (comparisonResult != Equal) return@Comparator comparisonResult
    }
    
    Equal
}

public fun LabeledPolynomial.MonomialSignature.Companion.degreeLexicographicOrder(
    variableOrder: Order<LabeledPolynomial.Variable> = LabeledPolynomial.Variable.lexicographicOrder(),
    degreeOrder: Order<UInt> = Order.defaultFor(),
): Order<LabeledPolynomial.MonomialSignature> = object : Order<LabeledPolynomial.MonomialSignature> {
    private val fallbackOrder = LabeledPolynomial.MonomialSignature.lexicographicOrder(variableOrder, degreeOrder)
    override fun LabeledPolynomial.MonomialSignature.compareWith(other: LabeledPolynomial.MonomialSignature): ComparisonResult {
        val degreeComparisonResult = context(degreeOrder, UInt.monoid()) { this.valuesView.sum() compareWith other.valuesView.sum() }
        if (degreeComparisonResult != Equal) return degreeComparisonResult
        return with(fallbackOrder) { this@compareWith compareWith other }
    }
}

public fun LabeledPolynomial.MonomialSignature.Companion.degreeLexicographicComparator(
    variableOrder: Comparator<LabeledPolynomial.Variable> = LabeledPolynomial.Variable.lexicographicComparator(),
    degreeOrder: Comparator<UInt> = Comparator.defaultFor(),
): Comparator<LabeledPolynomial.MonomialSignature> = object : Comparator<LabeledPolynomial.MonomialSignature> {
    private val fallbackComparator = LabeledPolynomial.MonomialSignature.lexicographicComparator(variableOrder, degreeOrder)
    override fun compare(left: LabeledPolynomial.MonomialSignature, right: LabeledPolynomial.MonomialSignature): ComparisonResult {
        val degreeComparisonResult = context(UInt.monoid()) { degreeOrder.compare(left.valuesView.sum(), right.valuesView.sum()) }
        if (degreeComparisonResult != Equal) return degreeComparisonResult
        return fallbackComparator.compare(left, right)
    }
}

private val defaultLabeledPolynomialMonomialSignatureOrder : Order<LabeledPolynomial.MonomialSignature> =
    LabeledPolynomial.MonomialSignature.lexicographicOrder(defaultLabeledPolynomialVariableOrder)

public fun <Number> LabeledPolynomial.Companion.equality(numberEquality: Equality<Number>): Equality<LabeledPolynomial<Number>> =
    object : Equality<LabeledPolynomial<Number>> {
        override fun LabeledPolynomial<Number>.equalsTo(other: LabeledPolynomial<Number>): Boolean {
            if (this.coefficients === other.coefficients) return true
            if (this.coefficients.size != other.coefficients.size) return false
            for ([signature, coefficient] in this.coefficients)
                if (!other.coefficients.computeOnOrElse(signature, { false }) { numberEquality { it eq coefficient } })
                    return false
            return true
        }
    }

public fun <Number> LabeledPolynomial.Companion.hashing(numberHashing: Hashing<Number>): Hashing<LabeledPolynomial<Number>> =
    object : Hashing<LabeledPolynomial<Number>> {
        override fun LabeledPolynomial<Number>.hash(): Int =
            coefficients.fold(0) { hash, [signature, coefficient] ->
                hash xor (LabeledPolynomialMonomialSignatureHashing { signature.hash() } * 31 + numberHashing { coefficient.hash() })
            }
    }

@LabeledPolynomial.DelicateApi
public fun LabeledPolynomial.MonomialSignature.Companion.fromUnsafe(
    degrees: KoneMap<LabeledPolynomial.Variable, UInt>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(degrees)

@LabeledPolynomial.DelicateApi
public fun LabeledPolynomial.MonomialSignature.Companion.fromUnsafe(
    degrees: KoneIterable<KoneMapEntry<LabeledPolynomial.Variable, UInt>>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(
    degrees.associate(
        keyEquality = LabeledPolynomial.Variable.equality(),
        keyHashing = LabeledPolynomial.Variable.hashing(),
        keyOrder = defaultLabeledPolynomialVariableOrder,
    ) { it }
)

@LabeledPolynomial.DelicateApi
public fun LabeledPolynomial.MonomialSignature.Companion.fromUnsafe(
    vararg degrees: KoneMapEntry<LabeledPolynomial.Variable, UInt>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(
    KoneArray(degrees).associate(
        keyEquality = LabeledPolynomial.Variable.equality(),
        keyHashing = LabeledPolynomial.Variable.hashing(),
        keyOrder = defaultLabeledPolynomialVariableOrder,
    ) { it }
)

public fun LabeledPolynomial.MonomialSignature.Companion.from(
    degrees: KoneMap<LabeledPolynomial.Variable, UInt>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(
    LabeledPolynomial.MonomialSignature(
        degrees.copyToBy(
            KoneMutableMap.of(
                keyEquality = LabeledPolynomial.Variable.equality(),
                keyHashing = LabeledPolynomial.Variable.hashing(),
                keyOrder = defaultLabeledPolynomialVariableOrder,
            )
        ) { _, currentValue, newValue ->
            currentValue + newValue
        }.filterValues(
            keyEquality = LabeledPolynomial.Variable.equality(),
            keyHashing = LabeledPolynomial.Variable.hashing(),
            keyOrder = defaultLabeledPolynomialVariableOrder,
        ) { it > 0U } // TODO: Replace `.copyToBy` + `.filterValues` with `.filterValuesResolving`
    )
)

public fun LabeledPolynomial.MonomialSignature.Companion.from(
    degrees: KoneIterable<KoneMapEntry<LabeledPolynomial.Variable, UInt>>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(
    LabeledPolynomial.MonomialSignature(
        degrees.associate(
            keyEquality = LabeledPolynomial.Variable.equality(),
            keyHashing = LabeledPolynomial.Variable.hashing(),
            keyOrder = defaultLabeledPolynomialVariableOrder,
            { it },
        ) { _, currentValue, newValue ->
            currentValue + newValue
        }.filterValues(
            keyEquality = LabeledPolynomial.Variable.equality(),
            keyHashing = LabeledPolynomial.Variable.hashing(),
            keyOrder = defaultLabeledPolynomialVariableOrder,
        ) { it > 0U } // TODO: Replace `.copyToBy` + `.filterValues` with `.filterValuesResolving`
    )
)

public fun LabeledPolynomial.MonomialSignature.Companion.from(
    vararg degrees: KoneMapEntry<LabeledPolynomial.Variable, UInt>,
): LabeledPolynomial.MonomialSignature = LabeledPolynomial.MonomialSignature(
    LabeledPolynomial.MonomialSignature(
        KoneArray(degrees).associate(
            keyEquality = LabeledPolynomial.Variable.equality(),
            keyHashing = LabeledPolynomial.Variable.hashing(),
            keyOrder = defaultLabeledPolynomialVariableOrder,
            { it },
        ) { _, currentValue, newValue ->
            currentValue + newValue
        }.filterValues(
            keyEquality = LabeledPolynomial.Variable.equality(),
            keyHashing = LabeledPolynomial.Variable.hashing(),
            keyOrder = defaultLabeledPolynomialVariableOrder,
        ) { it > 0U } // TODO: Replace `.copyToBy` + `.filterValues` with `.filterValuesResolving`
    )
)

@LabeledPolynomial.DelicateApi
@JvmName("fromSignaturesUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    coefficients: KoneMap<LabeledPolynomial.MonomialSignature, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(coefficients)

@LabeledPolynomial.DelicateApi
@JvmName("fromSignaturesUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    coefficients: KoneIterable<KoneMapEntry<LabeledPolynomial.MonomialSignature, Number>>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    coefficients.associate(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) { it }
)

@LabeledPolynomial.DelicateApi
@JvmName("fromSignaturesUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    vararg coefficients: KoneMapEntry<LabeledPolynomial.MonomialSignature, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneArray(coefficients).associate(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) { it }
)

@LabeledPolynomial.DelicateApi
@JvmName("fromMapsUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    coefficients: KoneMap<KoneMap<LabeledPolynomial.Variable, UInt>, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    coefficients.mapKeys(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) { LabeledPolynomial.MonomialSignature.from(it.key) }
)

@LabeledPolynomial.DelicateApi
@JvmName("fromMapsUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    coefficients: KoneIterable<KoneMapEntry<KoneMap<LabeledPolynomial.Variable, UInt>, Number>>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    coefficients.associate(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) { LabeledPolynomial.MonomialSignature.from(it.key) mapsTo it.value }
)

@LabeledPolynomial.DelicateApi
@JvmName("fromMapsUnsafe")
public fun <Number> LabeledPolynomial.Companion.fromUnsafe(
    vararg coefficients: KoneMapEntry<KoneMap<LabeledPolynomial.Variable, UInt>, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneArray(coefficients).associate(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) { LabeledPolynomial.MonomialSignature.from(it.key) mapsTo it.value }
)

@JvmName("fromSignatures")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    coefficients: KoneMap<LabeledPolynomial.MonomialSignature, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        coefficients.nodesView.associateTo(
            this,
            transform = { it }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@JvmName("fromSignatures")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    coefficients: KoneIterable<KoneMapEntry<LabeledPolynomial.MonomialSignature, Number>>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        coefficients.associateTo(
            this,
            transform = { it }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@JvmName("fromSignatures")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    vararg coefficients: KoneMapEntry<LabeledPolynomial.MonomialSignature, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        KoneArray(coefficients).associateTo(
            this,
            transform = { it }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@JvmName("fromMaps")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    coefficients: KoneMap<KoneMap<LabeledPolynomial.Variable, UInt>, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        coefficients.nodesView.associateTo(
            this,
            transform = { LabeledPolynomial.MonomialSignature.from(it.key) mapsTo it.value }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@JvmName("fromMaps")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    coefficients: KoneIterable<KoneMapEntry<KoneMap<LabeledPolynomial.Variable, UInt>, Number>>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        coefficients.associateTo(
            this,
            transform = { LabeledPolynomial.MonomialSignature.from(it.key) mapsTo it.value }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@JvmName("fromMaps")
context(group: Monoid<Number>)
public fun <Number> LabeledPolynomial.Companion.from(
    vararg coefficients: KoneMapEntry<KoneMap<LabeledPolynomial.Variable, UInt>, Number>,
): LabeledPolynomial<Number> = LabeledPolynomial(
    KoneMap.build(
        keyEquality = LabeledPolynomial.MonomialSignature.equality(),
        keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
        keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
    ) {
        KoneArray(coefficients).associateTo(
            this,
            transform = { LabeledPolynomial.MonomialSignature.from(it.key) mapsTo it.value }
        ) { _, currentValue, newValue ->
            group.numberPlusNumber { currentValue + newValue }
        }
        removeAllThat { _, value -> group.numberIsZero { value.isZero() } }
    }
)

@OptIn(LabeledPolynomial.DelicateApi::class)
private open class LabeledPolynomialSpace<Number>(
    protected open val ring: CommutativeRing<Number>,
) : PolynomialAlgebra<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> {
    companion object {
        private val unitSignature = LabeledPolynomial.MonomialSignature(KoneMap.empty())
        private val zeroPolynomial = LabeledPolynomial.fromUnsafe(KoneMap.empty<LabeledPolynomial.MonomialSignature, Nothing>())
    }
    
    override val zero: LabeledPolynomial<Number> get() = zeroPolynomial
    override val one: LabeledPolynomial<Number> = LabeledPolynomial.fromUnsafe(unitSignature mapsTo ring.one)
    
    override fun valueOf(arg: Int): LabeledPolynomial<Number> {
        val number = ring.valueOf(arg)
        return if (ring.numberIsZero { number.isZero() }) zero else LabeledPolynomial.fromUnsafe(unitSignature mapsTo ring.valueOf(arg))
    }
    override fun valueOf(arg: UInt): LabeledPolynomial<Number> {
        val number = ring.valueOf(arg)
        return if (ring.numberIsZero { number.isZero() }) zero else LabeledPolynomial.fromUnsafe(unitSignature mapsTo ring.valueOf(arg))
    }
    override fun valueOf(arg: Long): LabeledPolynomial<Number> {
        val number = ring.valueOf(arg)
        return if (ring.numberIsZero { number.isZero() }) zero else LabeledPolynomial.fromUnsafe(unitSignature mapsTo ring.valueOf(arg))
    }
    override fun valueOf(arg: ULong): LabeledPolynomial<Number> {
        val number = ring.valueOf(arg)
        return if (ring.numberIsZero { number.isZero() }) zero else LabeledPolynomial.fromUnsafe(unitSignature mapsTo ring.valueOf(arg))
    }
    override fun valueOf(arg: Number): LabeledPolynomial<Number> =
        if (ring.numberIsZero { arg.isZero() }) zero else LabeledPolynomial.fromUnsafe(unitSignature mapsTo arg)
    
    override val numberIsZero: IsZero<LabeledPolynomial<Number>> = IsZero {
        it.coefficients.valuesView.all { ring.numberIsZero { it.isZero() } }
    }
    override val numberIsOne: IsOne<LabeledPolynomial<Number>> = IsOne {
        it.coefficients.nodesView.all { if (it.key.isEmpty()) ring.numberIsOne { it.value.isOne() } else ring.numberIsZero { it.value.isZero() } }
    }
    
    override val variablePlusInt: Plus<LabeledPolynomial.Variable, Int, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo right,
        )
    }
    override val variableMinusInt: Minus<LabeledPolynomial.Variable, Int, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo ring.numberUnaryMinus { -right },
        )
    }
    override val variableTimesInt: Times<LabeledPolynomial.Variable, Int, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo right,
        )
    }
    
    override val variablePlusUInt: Plus<LabeledPolynomial.Variable, UInt, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo right,
        )
    }
    override val variableMinusUInt: Minus<LabeledPolynomial.Variable, UInt, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo ring.numberUnaryMinus { -right },
        )
    }
    override val variableTimesUInt: Times<LabeledPolynomial.Variable, UInt, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo right,
        )
    }
    
    override val variablePlusLong: Plus<LabeledPolynomial.Variable, Long, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo right,
        )
    }
    override val variableMinusLong: Minus<LabeledPolynomial.Variable, Long, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo ring.numberUnaryMinus { -right },
        )
    }
    override val variableTimesLong: Times<LabeledPolynomial.Variable, Long, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo right,
        )
    }
    
    override val variablePlusULong: Plus<LabeledPolynomial.Variable, ULong, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo right,
        )
    }
    override val variableMinusULong: Minus<LabeledPolynomial.Variable, ULong, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo ring.numberUnaryMinus { -right },
        )
    }
    override val variableTimesULong: Times<LabeledPolynomial.Variable, ULong, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        if (ring.numberIsZero { right.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo right,
        )
    }
    
    override val variablePlusNumber: Plus<LabeledPolynomial.Variable, Number, LabeledPolynomial<Number>> = Plus { left, right ->
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo right,
        )
    }
    override val variableMinusNumber: Minus<LabeledPolynomial.Variable, Number, LabeledPolynomial<Number>> = Minus { left, right ->
        if (ring.numberIsZero { right.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo ring.numberUnaryMinus { -right },
        )
    }
    override val variableTimesNumber: Times<LabeledPolynomial.Variable, Number, LabeledPolynomial<Number>> = Times { left, right ->
        if (ring.numberIsZero { right.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo right,
        )
    }
    
    override val intPlusVariable: Plus<Int, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo left,
        )
    }
    override val intMinusVariable: Minus<Int, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
            unitSignature mapsTo left,
        )
    }
    override val intTimesVariable: Times<Int, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo left,
        )
    }
    
    override val uIntPlusVariable: Plus<UInt, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo left,
        )
    }
    override val uIntMinusVariable: Minus<UInt, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
            unitSignature mapsTo left,
        )
    }
    override val uIntTimesVariable: Times<UInt, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo left,
        )
    }
    
    override val longPlusVariable: Plus<Long, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo left,
        )
    }
    override val longMinusVariable: Minus<Long, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
            unitSignature mapsTo left,
        )
    }
    override val longTimesVariable: Times<Long, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo left,
        )
    }
    
    override val uLongPlusVariable: Plus<ULong, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo left,
        )
    }
    override val uLongMinusVariable: Minus<ULong, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
            unitSignature mapsTo left,
        )
    }
    override val uLongTimesVariable: Times<ULong, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        if (ring.numberIsZero { left.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo left,
        )
    }
    
    override val numberPlusVariable: Plus<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
            unitSignature mapsTo left,
        )
    }
    override val numberMinusVariable: Minus<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        if (ring.numberIsZero { left.isZero() }) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
            unitSignature mapsTo left,
        )
    }
    override val numberTimesVariable: Times<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        if (ring.numberIsZero { left.isZero() }) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo left,
        )
    }
    
    override val numberPlusInt: Plus<LabeledPolynomial<Number>, Int, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = right
                    else {
                        val newValue = ring.numberPlusNumber { this[unitSignature] + right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberMinusInt: Minus<LabeledPolynomial<Number>, Int, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -right }
                    else {
                        val newValue = ring.numberMinusNumber { this[unitSignature] - right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberTimesInt: Times<LabeledPolynomial<Number>, Int, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> zero
            ring.numberIsOne { right.isOne() } -> left
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { value * right } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val numberPlusUInt: Plus<LabeledPolynomial<Number>, UInt, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = right
                    else {
                        val newValue = ring.numberPlusNumber { this[unitSignature] + right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberMinusUInt: Minus<LabeledPolynomial<Number>, UInt, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -right }
                    else {
                        val newValue = ring.numberMinusNumber { this[unitSignature] - right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberTimesUInt: Times<LabeledPolynomial<Number>, UInt, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> zero
            ring.numberIsOne { right.isOne() } -> left
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { value * right } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val numberPlusLong: Plus<LabeledPolynomial<Number>, Long, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = right
                    else {
                        val newValue = ring.numberPlusNumber { this[unitSignature] + right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberMinusLong: Minus<LabeledPolynomial<Number>, Long, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -right }
                    else {
                        val newValue = ring.numberMinusNumber { this[unitSignature] - right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberTimesLong: Times<LabeledPolynomial<Number>, Long, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> zero
            ring.numberIsOne { right.isOne() } -> left
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { value * right } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val numberPlusULong: Plus<LabeledPolynomial<Number>, ULong, LabeledPolynomial<Number>> = Plus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = right
                    else {
                        val newValue = ring.numberPlusNumber { this[unitSignature] + right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberMinusULong: Minus<LabeledPolynomial<Number>, ULong, LabeledPolynomial<Number>> = Minus { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -right }
                    else {
                        val newValue = ring.numberMinusNumber { this[unitSignature] - right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberTimesULong: Times<LabeledPolynomial<Number>, ULong, LabeledPolynomial<Number>> = Times { left, right ->
        val right = ring.valueOf(right)
        when {
            ring.numberIsZero { right.isZero() } -> zero
            ring.numberIsOne { right.isOne() } -> left
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { value * right } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val vectorPlusNumber: Plus<LabeledPolynomial<Number>, Number, LabeledPolynomial<Number>> = Plus { left, right ->
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = right
                    else {
                        val newValue = ring.numberPlusNumber { this[unitSignature] + right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val vectorMinusNumber: Minus<LabeledPolynomial<Number>, Number, LabeledPolynomial<Number>> = Minus { left, right ->
        when {
            ring.numberIsZero { right.isZero() } -> left
            left.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo right)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -right }
                    else {
                        val newValue = ring.numberMinusNumber { this[unitSignature] - right }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val vectorTimesNumber: Times<LabeledPolynomial<Number>, Number, LabeledPolynomial<Number>> = Times { left, right ->
        when {
            ring.numberIsZero { right.isZero() } -> zero
            ring.numberIsOne { right.isOne() } -> left
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    left.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { value * right } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val intPlusNumber: Plus<Int, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = left
                    else {
                        val newValue = ring.numberPlusNumber { left + this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val intMinusNumber: Minus<Int, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -left }
                    else {
                        val newValue = ring.numberMinusNumber { left - this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val intTimesNumber: Times<Int, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> zero
            ring.numberIsOne { left.isOne() } -> right
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { left * value } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val uIntPlusNumber: Plus<UInt, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = left
                    else {
                        val newValue = ring.numberPlusNumber { left + this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val uIntMinusNumber: Minus<UInt, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -left }
                    else {
                        val newValue = ring.numberMinusNumber { left - this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val uIntTimesNumber: Times<UInt, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> zero
            ring.numberIsOne { left.isOne() } -> right
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { left * value } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val longPlusNumber: Plus<Long, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = left
                    else {
                        val newValue = ring.numberPlusNumber { left + this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val longMinusNumber: Minus<Long, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -left }
                    else {
                        val newValue = ring.numberMinusNumber { left - this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val longTimesNumber: Times<Long, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> zero
            ring.numberIsOne { left.isOne() } -> right
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { left * value } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val uLongPlusNumber: Plus<ULong, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = left
                    else {
                        val newValue = ring.numberPlusNumber { left + this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val uLongMinusNumber: Minus<ULong, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -left }
                    else {
                        val newValue = ring.numberMinusNumber { left - this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val uLongTimesNumber: Times<ULong, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        val left = ring.valueOf(left)
        when {
            ring.numberIsZero { left.isZero() } -> zero
            ring.numberIsOne { left.isOne() } -> right
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { left * value } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val numberPlusVector: Plus<Number, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = left
                    else {
                        val newValue = ring.numberPlusNumber { left + this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberMinusVector: Minus<Number, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        when {
            ring.numberIsZero { left.isZero() } -> right
            right.coefficients.isEmpty() -> LabeledPolynomial.fromUnsafe(unitSignature mapsTo left)
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.copyTo(this)
                    if (unitSignature !in this) this[unitSignature] = ring.numberUnaryMinus { -left }
                    else {
                        val newValue = ring.numberMinusNumber { left - this[unitSignature] }
                        if (ring.numberIsZero { newValue.isZero() }) this[unitSignature] = newValue
                        else this.remove(unitSignature)
                    }
                }
            )
        }
    }
    override val numberTimesVector: Times<Number, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        when {
            ring.numberIsZero { left.isZero() } -> zero
            ring.numberIsOne { left.isOne() } -> right
            else -> LabeledPolynomial.fromUnsafe(
                KoneMap.build(
                    keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                    keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                    keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
                ) {
                    right.coefficients.mapValuesTo(this) { (value) -> ring.numberTimesNumber { left * value } }
                    removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
                }
            )
        }
    }
    
    override val variableUnaryPlus: UnaryPlus<LabeledPolynomial.Variable, LabeledPolynomial<Number>> = UnaryPlus {
        LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(it mapsTo 1U) mapsTo ring.one,
        )
    }
    override val variableUnaryMinus: UnaryMinus<LabeledPolynomial.Variable, LabeledPolynomial<Number>> = UnaryMinus {
        LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(it mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
    }
    override val variablePlusVariable: Plus<LabeledPolynomial.Variable, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        if (left == right) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.numberTimesInt { ring.one * 2 }
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.one,
        )
    }
    override val variableMinusVariable: Minus<LabeledPolynomial.Variable, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        if (left == right) zero
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U) mapsTo ring.one,
            LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1U) mapsTo ring.numberUnaryMinus { -ring.one },
        )
    }
    override val variableTimesVariable: Times<LabeledPolynomial.Variable, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        if (left == right) LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 2U) mapsTo ring.one
        )
        else LabeledPolynomial.fromUnsafe(
            LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1U, right mapsTo 1U) mapsTo ring.one,
        )
    }
    
    override val variablePlusPolynomial: Plus<LabeledPolynomial.Variable, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                val leftSignature = LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1u)
                this[leftSignature] = ring.one
                right.coefficients.mapValuesTo(this, { it.value }) { _, currentValue, newValue -> ring.numberPlusNumber { currentValue + newValue } }
                if (ring.numberIsZero { this[leftSignature].isZero() }) remove(leftSignature)
            }
        )
    }
    override val variableMinusPolynomial: Minus<LabeledPolynomial.Variable, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                val leftSignature = LabeledPolynomial.MonomialSignature.fromUnsafe(left mapsTo 1u)
                this[leftSignature] = ring.one
                right.coefficients.mapValuesTo(this, { ring.numberUnaryMinus { -it.value } }) { _, oldValue, newValue -> ring.numberMinusNumber { oldValue - newValue } }
                if (ring.numberIsZero { this[leftSignature].isZero() }) remove(leftSignature)
            }
        )
    }
    override val variableTimesPolynomial: Times<LabeledPolynomial.Variable, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        LabeledPolynomial.fromUnsafe(
            right.coefficients.mapKeys(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) { entry ->
                LabeledPolynomial.MonomialSignature.fromUnsafe(
                    entry.key.withSetOrChanged(
                        key = left,
                        keyEquality = LabeledPolynomial.Variable.equality(),
                        keyHashing = LabeledPolynomial.Variable.hashing(),
                        keyOrder = defaultLabeledPolynomialVariableOrder,
                        valueOnSet = { 1u },
                        transformOnChange = { it + 1u }
                    )
                )
            }
        )
    }
    
    override val polynomialPlusVariable: Plus<LabeledPolynomial<Number>, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Plus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                val rightSignature = LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1u)
                this[rightSignature] = ring.one
                left.coefficients.mapValuesTo(this, { it.value }) { _, currentValue, newValue -> ring.numberPlusNumber { newValue + currentValue } }
                if (ring.numberIsZero { this[rightSignature].isZero() }) remove(rightSignature)
            }
        )
    }
    override val polynomialMinusVariable: Minus<LabeledPolynomial<Number>, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Minus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                val rightSignature = LabeledPolynomial.MonomialSignature.fromUnsafe(right mapsTo 1u)
                this[rightSignature] = ring.numberUnaryMinus { -ring.one }
                left.coefficients.mapValuesTo(this, { it.value }) { _, currentValue, newValue -> ring.numberPlusNumber { newValue + currentValue } }
                if (ring.numberIsZero { this[rightSignature].isZero() }) remove(rightSignature)
            }
        )
    }
    override val polynomialTimesVariable: Times<LabeledPolynomial<Number>, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = Times { left, right ->
        LabeledPolynomial.fromUnsafe(
            left.coefficients.mapKeys(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) { entry ->
                LabeledPolynomial.MonomialSignature.fromUnsafe(
                    entry.key.withSetOrChanged(
                        key = right,
                        keyEquality = LabeledPolynomial.Variable.equality(),
                        keyHashing = LabeledPolynomial.Variable.hashing(),
                        keyOrder = defaultLabeledPolynomialVariableOrder,
                        valueOnSet = { 1u },
                        transformOnChange = { it + 1u }
                    )
                )
            }
        )
    }
    
    override val numberUnaryMinus: UnaryMinus<LabeledPolynomial<Number>, LabeledPolynomial<Number>> = UnaryMinus {
        LabeledPolynomial.fromUnsafe(
            it.coefficients.mapValues(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) { ring.numberUnaryMinus { -it.value } }
        )
    }
    override val numberPlusNumber: Plus<LabeledPolynomial<Number>, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Plus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                left.coefficients.copyTo(this)
                for ((key, value) in right) {
                    val node = this.getNodeOrNull(key)
                    if (node == null) {
                        this[key] = value
                    } else {
                        val oldValue = ring.numberPlusNumber { node.value + value }
                        if (ring.numberIsZero { oldValue.isZero() }) node.remove()
                        else node.value = oldValue
                    }
                }
            }
        )
    }
    override val numberMinusNumber: Minus<LabeledPolynomial<Number>, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Minus { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                left.coefficients.copyTo(this)
                for ((key, value) in right) {
                    val node = this.getNodeOrNull(key)
                    if (node == null) {
                        this[key] = ring.numberUnaryMinus { -value }
                    } else {
                        val oldValue = ring.numberMinusNumber { node.value - value }
                        if (ring.numberIsZero { oldValue.isZero() }) node.remove()
                        else node.value = oldValue
                    }
                }
            }
        )
    }
    override val numberTimesNumber: Times<LabeledPolynomial<Number>, LabeledPolynomial<Number>, LabeledPolynomial<Number>> = Times { left, right ->
        LabeledPolynomial.fromUnsafe(
            KoneMap.build(
                left.coefficients.size * right.coefficients.size,
                keyEquality = LabeledPolynomial.MonomialSignature.equality(),
                keyHashing = LabeledPolynomial.MonomialSignature.hashing(),
                keyOrder = defaultLabeledPolynomialMonomialSignatureOrder,
            ) {
                for ([leftDegrees, leftCoefficient] in left.coefficients.nodesView) {
                    for ([rightDegrees, rightCoefficient] in right.coefficients.nodesView) {
                        val degrees = LabeledPolynomial.MonomialSignature.fromUnsafe(
                            mergeBy(
                                leftDegrees.degrees,
                                rightDegrees.degrees,
                                keyEquality = LabeledPolynomial.Variable.equality(),
                                keyHashing = LabeledPolynomial.Variable.hashing(),
                                keyOrder = defaultLabeledPolynomialVariableOrder,
                            ) { _, deg1, deg2 -> deg1 + deg2 }
                        )
                        val coefficient = ring.numberTimesNumber { leftCoefficient * rightCoefficient }
                        this.setOrChange(degrees, { coefficient }, { ring.numberPlusNumber { it + coefficient } })
                    }
                }
                removeAllThat { _, value -> ring.numberIsZero { value.isZero() } }
            }
        )
    }
    
    override val variablesAndDegree: PolynomialAlgebra.VariablesAndDegree<LabeledPolynomial.Variable, LabeledPolynomial<Number>> =
        object : PolynomialAlgebra.VariablesAndDegree<LabeledPolynomial.Variable, LabeledPolynomial<Number>> {
            override val LabeledPolynomial<Number>.variables: KoneSet<LabeledPolynomial.Variable>
                get() = KoneSet.build(
                    elementEquality = LabeledPolynomial.Variable.equality(),
                    elementHashing = LabeledPolynomial.Variable.hashing(),
                    elementOrder = defaultLabeledPolynomialVariableOrder,
                ) {
                    coefficients.keysView.forEach { addAllFrom(it.keysView) }
                }
            
            override fun LabeledPolynomial<Number>.degreeBy(variables: KoneSet<LabeledPolynomial.Variable>): UInt =
                if (this.coefficients.isEmpty()) zeroPolynomialDegreeException()
                else this.coefficients.keysView.maxOf {
                    it.degrees.fold(0u) { accumulator, [signatureVariable, degree] -> if (signatureVariable in variables) accumulator + degree else accumulator }
                }
            
            override fun LabeledPolynomial<Number>.degreeBy(variable: LabeledPolynomial.Variable): UInt =
                if (this.coefficients.isEmpty()) zeroPolynomialDegreeException()
                else this.coefficients.keysView.maxOf {
                    it.degrees.fold(0u) { accumulator, [signatureVariable, degree] -> if (signatureVariable == variable) accumulator + degree else accumulator }
                }
            
            override val LabeledPolynomial<Number>.degrees: KoneMap<LabeledPolynomial.Variable, UInt>
                get() = KoneMap.build(
                    keyEquality = LabeledPolynomial.Variable.equality(),
                    keyHashing = LabeledPolynomial.Variable.hashing(),
                    keyOrder = defaultLabeledPolynomialVariableOrder,
                ) {
                    coefficients.keysView.forEach {
                        it.degrees.copyToBy(this) { _, currentDeg, newDeg -> maxOf(currentDeg, newDeg) }
                    }
                }
            
            override val LabeledPolynomial<Number>.degree: UInt
                get() =
                    if (this.coefficients.isEmpty()) zeroPolynomialDegreeException()
                    else this.coefficients.keysView.maxOf { (UInt.monoid()) { it.degrees.valuesView.sum() } }
        }
}

public fun <Number> LabeledPolynomial.Companion.polynomialAlgebra(
    ring: CommutativeRing<Number>,
): PolynomialAlgebra<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> = LabeledPolynomialSpace(
    ring = ring
)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> LabeledPolynomial.Companion.polynomialAlgebra(): PolynomialAlgebra<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return polynomialAlgebra(
        ring = koneContextRegistry[CommutativeRing.Key<Number>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Number> LabeledPolynomial.Companion.setPolynomialAlgebra(
    ring: CommutativeRing<Number>,
) {
    PolynomialAlgebra.Key<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        polynomialAlgebra(
            ring = ring,
        )
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> LabeledPolynomial.Companion.setPolynomialAlgebra() {
    PolynomialAlgebra.Key<Number, LabeledPolynomial.Variable, LabeledPolynomial<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        polynomialAlgebra<Number>()
    }
}