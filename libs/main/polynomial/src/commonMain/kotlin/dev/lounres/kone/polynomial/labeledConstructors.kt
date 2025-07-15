/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.map.KoneMutableReifiedMap
import dev.lounres.kone.collections.map.KoneReifiedMap
import dev.lounres.kone.collections.map.empty
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.utils.associateBy
import dev.lounres.kone.collections.utils.mapKeys
import dev.lounres.kone.collections.utils.setOrChange
import dev.lounres.kone.relations.defaultHashing


// TODO: Check reifications.
// TODO: Think about `Order`s for creating maps.

@PublishedApi
internal fun LabeledMonomialSignature.cleanUp(): LabeledMonomialSignature = filterValuesReified { it > 0U }

@DelicatePolynomialAPI
public inline fun <Number> LabeledPolynomialAsIs(coefs: LabeledPolynomialCoefficients<Number>) : LabeledPolynomial<Number> =
    LabeledPolynomial<Number>(coefs)

@DelicatePolynomialAPI
public fun <Number> LabeledPolynomialAsIs(entries: KoneIterable<KoneMapEntry<LabeledMonomialSignature, Number>>) : LabeledPolynomial<Number> =
    LabeledPolynomial<Number>(entries.associateReified(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing) { it })

@DelicatePolynomialAPI
public fun <Number> LabeledPolynomialAsIs(vararg entries: KoneMapEntry<LabeledMonomialSignature, Number>) : LabeledPolynomial<Number> =
    LabeledPolynomial<Number>(KoneArray(entries).associateReified(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing) { it })

public inline fun <Number> LabeledPolynomial(coefs: LabeledPolynomialCoefficients<Number>, add: (Number, Number) -> Number, isZero: (Number) -> Boolean) : LabeledPolynomial<Number> =
    LabeledPolynomialAsIs(
        coefs
            .mapKeys(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, transform = { entry -> entry.key.cleanUp() }, resolve = { _, c1, c2 -> add(c1, c2) })
            .filterValuesReified(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, predicate = { !isZero(it) })
    )

public inline fun <Number> LabeledPolynomial(entries: KoneIterable<KoneMapEntry<LabeledMonomialSignature, Number>>, add: (Number, Number) -> Number, isZero: (Number) -> Boolean) : LabeledPolynomial<Number> =
    LabeledPolynomialAsIs(
        entries
            .associateBy(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, keySelector = { it.key.cleanUp() }, valueTransform = { it.value }, resolve = { _, c1, c2 -> add(c1, c2)})
            .filterValuesReified(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, predicate = { !isZero(it) })
    )

public inline fun <Number> LabeledPolynomial(vararg entries: KoneMapEntry<LabeledMonomialSignature, Number>, add: (Number, Number) -> Number, isZero: (Number) -> Boolean) : LabeledPolynomial<Number> =
    LabeledPolynomialAsIs(
        KoneArray(entries)
            .associateBy(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, keySelector = { it.key.cleanUp() }, valueTransform = { it.value }, resolve = { _, c1, c2 -> add(c1, c2)})
            .filterValuesReified(keyEquality = labeledMonomialSignatureEquality, keyHashing = labeledMonomialSignatureHashing, predicate = { !isZero(it) })
    )

context(_: Ring<C>)
public fun <C> LabeledPolynomial(coefs: LabeledPolynomialCoefficients<C>) : LabeledPolynomial<C> =
    LabeledPolynomial(coefs, { left: C, right: C -> left + right }, { it.isZero() })

context(_: Ring<C>)
public fun <C> LabeledPolynomial(entries: KoneIterable<KoneMapEntry<LabeledMonomialSignature, C>>) : LabeledPolynomial<C> = LabeledPolynomial(entries, { left: C, right: C -> left + right }, { it.isZero() })

context(_: Ring<C>)
public fun <C> LabeledPolynomial(vararg entries: KoneMapEntry<LabeledMonomialSignature, C>) : LabeledPolynomial<C> = LabeledPolynomial(entries = entries, add = { left: C, right: C -> left + right }, isZero = { it.isZero() })

public fun <C> C.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomialAsIs(KoneReifiedMap.of(KoneReifiedMap.empty<LabeledVariable, UInt>() mapsTo this))

/**
 * Converts [this] variable to [LabeledPolynomial].
 */
context(_: Ring<C>)
public inline fun <C> LabeledVariable.asLabeledPolynomial() : LabeledPolynomial<C> = LabeledPolynomial<C>(KoneReifiedMap.of(KoneReifiedMap.of(this mapsTo 1u) mapsTo one))

@DslMarker
internal annotation class LabeledPolynomialConstructorDSL1

@LabeledPolynomialConstructorDSL1
public class DSL1LabeledPolynomialTermSignatureBuilder {
    private val signature: KoneMutableReifiedMap<LabeledVariable, UInt> = KoneMutableReifiedMap.of(keyHashing = defaultHashing())

    @PublishedApi
    internal fun build(): LabeledMonomialSignature = signature

    public infix fun LabeledVariable.inPowerOf(deg: UInt) {
        if (deg == 0u) return
        signature.setOrChange(this, { deg }, { it + deg })
    }
    public inline infix fun LabeledVariable.pow(deg: UInt): Unit = this inPowerOf deg
    public inline infix fun LabeledVariable.`in`(deg: UInt): Unit = this inPowerOf deg
    public inline infix fun LabeledVariable.of(deg: UInt): Unit = this inPowerOf deg
}

@LabeledPolynomialConstructorDSL1
public class DSL1LabeledPolynomialBuilder<C>(
    private val add: (C, C) -> C,
    initialCapacity: Int? = null
) {
    private val coefficients: KoneMutableReifiedMap<LabeledMonomialSignature, C> = KoneMutableReifiedMap.of(keyHashing = labeledMonomialSignatureHashing) // TODO: Use `initialCapacity` eventually

    @PublishedApi
    internal fun build(): LabeledPolynomial<C> = LabeledPolynomial<C>(coefficients)

    public infix fun C.with(signature: LabeledMonomialSignature) {
        coefficients.setOrChange(signature, { this@with }, { add(this@with, it) })
    }
    public inline infix fun C.with(noinline block: DSL1LabeledPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
    public inline operator fun C.invoke(block: DSL1LabeledPolynomialTermSignatureBuilder.() -> Unit): Unit =
        this with DSL1LabeledPolynomialTermSignatureBuilder().apply(block).build()
}

///**
// * Creates [LabeledPolynomial] with lambda [block] in context of [this] ring of constants.
// *
// * For example, polynomial \(5 a^2 c^3 - 6 b\) can be described as
// * ```
// * Int.algebra {
// *     val labeledPolynomial : LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
// *         5 { a inPowerOf 2u; c inPowerOf 3u } // 5 a^2 c^3 +
// *         (-6) { b inPowerOf 1u }              // (-6) b^1
// *     }
// * }
// * ```
// * @usesMathJax
// */
context(_: Ring<C>)
public inline fun <C> LabeledPolynomialDSL1(initialCapacity: Int? = null, block: DSL1LabeledPolynomialBuilder<C>.() -> Unit) : LabeledPolynomial<C> =
    DSL1LabeledPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()

//@DslMarker
//@ExperimentalKoneAPI
//internal annotation class LabeledPolynomialBuilderDSL2
//
//@ExperimentalKoneAPI
//@LabeledPolynomialBuilderDSL2
//public class DSL2LabeledPolynomialBuilder<C>(
//    private val ring: Ring<C>,
//    initialCapacity: Int? = null
//) {
//    private val coefficients: KoneMutableReifiedMap<LabeledMonomialSignature, C> = koneMutableReifiedMapOf(labeledMonomialSignatureReifiedHashing) // TODO: Use `initialCapacity` eventually
//
//    @PublishedApi
//    internal fun build(): LabeledPolynomial<C> = LabeledPolynomial<C>(coefficients)
//
//    public inner class Term internal constructor(
//        internal val signature: LabeledMonomialSignature = koneMutableReifiedMapOf(),
//        internal val coefficient: C
//    )
//
//    private inline fun submit(signature: LabeledMonomialSignature, onPut: Ring<C>.() -> C, onChange: Ring<C>.(C) -> C) {
//        coefficients.putOrChange<_, C>(signature, { ring.onPut() }, { ring.onChange(it) })
//    }
//
//    private inline fun submit(signature: LabeledMonomialSignature, lazyCoefficient: Ring<C>.() -> C) {
//        submit(signature, lazyCoefficient, { it + lazyCoefficient() })
//    }
//
//    private fun submit(signature: LabeledMonomialSignature, coefficient: C) {
//        submit(signature) { coefficient }
//    }
//
//    // TODO: `@submit` will be resolved differently. Change it to `@C`.
//    @Suppress("LABEL_RESOLVE_WILL_CHANGE")
//    private fun C.submit() = this.let { submit(emptyKoneReifiedMap(), { it }) }
//
//    private fun LabeledVariable.submit() = submit(koneMutableReifiedMapOf(this mapsTo 1u, keyContext = defaultReifiedHashing<LabeledVariable>()), { one })
//
//    private fun Term.submit(): Submit {
//        submit(signature, coefficient)
//        return Submit
//    }
//
//    public object Submit
//
//    public operator fun C.unaryPlus(): Submit {
//        submit()
//        return Submit
//    }
//
//    public operator fun C.unaryMinus(): Submit {
//        submit(emptyKoneReifiedMap(), { -this@unaryMinus }, { it - this@unaryMinus })
//        return Submit
//    }
//
//    public operator fun C.plus(other: C): Submit {
//        submit(emptyKoneReifiedMap(), { this@plus + other })
//        return Submit
//    }
//
//    public operator fun C.minus(other: C): Submit {
//        submit(emptyKoneReifiedMap(), { this@minus - other })
//        return Submit
//    }
//
//    public operator fun C.times(other: C): C = with(ring) { this@times * other }
//
//    public operator fun C.plus(other: LabeledVariable): Submit {
//        submit(emptyKoneReifiedMap(), this)
//        submit(koneMutableReifiedMapOf(other mapsTo 1u, keyContext = defaultReifiedHashing()), ring.one)
//        return Submit
//    }
//
//    public operator fun C.minus(other: LabeledVariable): Submit {
//        submit(emptyKoneReifiedMap(), this)
//        submit(mapOf(other to 1u), { -one }, { it - one })
//        return Submit
//    }
//
//    public operator fun C.times(other: LabeledVariable): Term = Term(mapOf(other to 1u), this)
//
//    public operator fun C.plus(other: Term): Submit {
//        submit(emptyKoneReifiedMap(), this)
//        other.submit()
//        return Submit
//    }
//
//    public operator fun C.minus(other: Term): Submit {
//        submit(emptyKoneReifiedMap(), this)
//        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
//        return Submit
//    }
//
//    public operator fun C.times(other: Term): Term = Term(other.signature, ring { this@times * other.coefficient })
//
//    public operator fun LabeledVariable.plus(other: C): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun LabeledVariable.minus(other: C): Submit {
//        this.submit()
//        submit(emptyKoneReifiedMap(), { -other }, { it - other })
//        return Submit
//    }
//
//    public operator fun LabeledVariable.times(other: C): Term = Term(mapOf(this to 1u), other)
//
//    public operator fun LabeledVariable.unaryPlus(): Submit {
//        this.submit()
//        return Submit
//    }
//
//    public operator fun LabeledVariable.unaryMinus(): Submit {
//        submit(mapOf(this to 1u), { -one }, { it - one })
//        return Submit
//    }
//
//    public operator fun LabeledVariable.plus(other: LabeledVariable): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun LabeledVariable.minus(other: LabeledVariable): Submit {
//        this.submit()
//        submit(mapOf(other to 1u), { -one }, { it - one })
//        return Submit
//    }
//
//    public operator fun LabeledVariable.times(other: LabeledVariable): Term =
//        if (this == other) Term(mapOf(this to 2u), ring.one)
//        else Term(mapOf(this to 1u, other to 1u), ring.one)
//
//    public operator fun LabeledVariable.plus(other: Term): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun LabeledVariable.minus(other: Term): Submit {
//        this.submit()
//        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
//        return Submit
//    }
//
//    public operator fun LabeledVariable.times(other: Term): Term =
//        Term(
//            other.signature.withPutOrChanged(this, { 1u }, { it + 1u }),
//            other.coefficient
//        )
//
//    public operator fun Term.plus(other: C): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun Term.minus(other: C): Submit {
//        this.submit()
//        submit(emptyKoneReifiedMap(), { -other }, { it - other })
//        return Submit
//    }
//
//    public operator fun Term.times(other: C): Term =
//        Term(
//            signature,
//            ring { coefficient * other }
//        )
//
//    public operator fun Term.plus(other: LabeledVariable): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun Term.minus(other: LabeledVariable): Submit {
//        this.submit()
//        submit(mapOf(other to 1u), { -one }, { it - one })
//        return Submit
//    }
//
//    public operator fun Term.times(other: LabeledVariable): Term =
//        Term(
//            signature.withPutOrChanged(other, { 1u }, { it + 1u }),
//            coefficient
//        )
//
//    public operator fun Term.unaryPlus(): Submit {
//        this.submit()
//        return Submit
//    }
//
//    public operator fun Term.unaryMinus(): Submit {
//        submit(signature, { -coefficient }, { it - coefficient })
//        return Submit
//    }
//
//    public operator fun Term.plus(other: Term): Submit {
//        this.submit()
//        other.submit()
//        return Submit
//    }
//
//    public operator fun Term.minus(other: Term): Submit {
//        this.submit()
//        submit(other.signature, { -other.coefficient }, { it - other.coefficient })
//        return Submit
//    }
//
//    public operator fun Term.times(other: Term): Term =
//        Term(
//            mergeBy(signature, other.signature) { _, deg1, deg2 -> deg1 + deg2 },
//            ring { coefficient * other.coefficient }
//        )
//}
//
//context(Ring<C>)
//@ExperimentalKoneAPI
//public fun <C> LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(this@Ring, initialCapacity).apply(block).build()
//
//context(LabeledPolynomialSpace<C, Ring<C>>)
//@ExperimentalKoneAPI
//public fun <C> LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(constantRing, initialCapacity).apply(block).build()
//
//context(LabeledRationalFunctionSpace<C, *, LabeledPolynomialSpace<C, Ring<C>>>)
//@ExperimentalKoneAPI
//public fun <C> LabeledPolynomialDSL2(initialCapacity: Int? = null, block: DSL2LabeledPolynomialBuilder<C>.() -> Unit): LabeledPolynomial<C> = DSL2LabeledPolynomialBuilder(polynomialSpace.constantRing, initialCapacity).apply(block).build()

public fun <C> LabeledRationalFunction(numeratorCoefficients: LabeledPolynomialCoefficients<C>, denominatorCoefficients: LabeledPolynomialCoefficients<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        LabeledPolynomial(denominatorCoefficients)
    )

context(_: LabeledPolynomialSpace<C>)
public fun <C> LabeledRationalFunction(numerator: LabeledPolynomial<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(numerator, one)

context(_: LabeledPolynomialSpace<C>)
public fun <C> LabeledRationalFunction(numeratorCoefficients: LabeledPolynomialCoefficients<C>): LabeledRationalFunction<C> =
    LabeledRationalFunction<C>(
        LabeledPolynomial(numeratorCoefficients),
        one
    )

/**
 * Converts [this] constant to [LabeledRationalFunction].
 */
context(_: LabeledPolynomialSpace<C>)
public fun <C> C.asLabeledRationalFunction() : LabeledRationalFunction<C> =
    LabeledRationalFunction(
        this.asLabeledPolynomial(),
        one,
    )

/**
 * Converts [this] variable to [LabeledRationalFunction].
 */
context(numberContext: Ring<C>, _: LabeledPolynomialSpace<C>)
public fun <C> LabeledVariable.asLabeledRationalFunction() : LabeledRationalFunction<C> =
    LabeledRationalFunction(
        context(numberContext) { this.asLabeledPolynomial() },
        polynomialOne,
    )