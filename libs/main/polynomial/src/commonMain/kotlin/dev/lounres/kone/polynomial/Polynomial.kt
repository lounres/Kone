/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.ExperimentalKoneAPI
import dev.lounres.kone.algebraic.EuclideanRing
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.getOrElse
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KVariance


@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<Number, Polynomial> : Ring<Polynomial> {
    // region Number constants
    public val numberZero: Number
    public val numberOne: Number
    // endregion

    // region Polynomial constants
    public val polynomialZero: Polynomial get() = zero
    public val polynomialOne: Polynomial get() = one
    // endregion

    // region Integer-to-Number conversion
    public fun numberValueOf(arg: Int): Number
    public fun numberValueOf(arg: UInt): Number
    public fun numberValueOf(arg: Long): Number
    public fun numberValueOf(arg: ULong): Number
    public val Int.numberValue: Number
    public val UInt.numberValue: Number
    public val Long.numberValue: Number
    public val ULong.numberValue: Number
    // endregion

    // region Integer-to-Polynomial conversion
    override fun valueOf(arg: Int): Polynomial = polynomialValueOf(numberValueOf(arg))
    override fun valueOf(arg: UInt): Polynomial = polynomialValueOf(numberValueOf(arg))
    override fun valueOf(arg: Long): Polynomial = polynomialValueOf(numberValueOf(arg))
    override fun valueOf(arg: ULong): Polynomial = polynomialValueOf(numberValueOf(arg))
    public fun polynomialValueOf(value: Int): Polynomial = valueOf(value)
    public fun polynomialValueOf(value: UInt): Polynomial = valueOf(value)
    public fun polynomialValueOf(value: Long): Polynomial = valueOf(value)
    public fun polynomialValueOf(value: ULong): Polynomial = valueOf(value)
    public val Int.polynomialValue: Polynomial get() = polynomialValueOf(this)
    public val UInt.polynomialValue: Polynomial get() = polynomialValueOf(this)
    public val Long.polynomialValue: Polynomial get() = polynomialValueOf(this)
    public val ULong.polynomialValue: Polynomial get() = polynomialValueOf(this)
    // endregion

    // region Number-to-Polynomial conversion
    @JvmName("polynomialValueOfNumber")
    public fun polynomialValueOf(value: Number): Polynomial = one * value
    @get:JvmName("polynomialValueNumber")
    public val Number.polynomialValue: Polynomial get() = polynomialValueOf(this)
    // endregion

    // region Number-Polynomial operations
    @JvmName("numberPlusPolynomial")
    public operator fun Number.plus(other: Polynomial): Polynomial
    @JvmName("numberMinusPolynomial")
    public operator fun Number.minus(other: Polynomial): Polynomial
    @JvmName("numberTimesPolynomial")
    public operator fun Number.times(other: Polynomial): Polynomial
    // endregion

    // region Polynomial-Number operations
    @JvmName("polynomialPlusNumber")
    public operator fun Polynomial.plus(other: Number): Polynomial
    @JvmName("polynomialMinusNumber")
    public operator fun Polynomial.minus(other: Number): Polynomial
    @JvmName("polynomialTimesNumber")
    public operator fun Polynomial.times(other: Number): Polynomial
    // endregion

    // region Polynomial properties
    public val Polynomial.degree: UInt
    // endregion
    
    public class Key<Number, Polynomial>(
        numberType: SuppliedType<Number>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<PolynomialSpace<Number, Polynomial>> {
        override val typeKey: SuppliedType.Regular<PolynomialSpace<Number, Polynomial>> =
            SuppliedType.Regular(
                kClass = PolynomialSpace::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

// region Number constants
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> numberZero: Number get() = polynomialSpace.numberZero
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> numberOne: Number get() = polynomialSpace.numberOne
// endregion

// region Polynomial constants
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> polynomialZero: Polynomial get() = polynomialSpace.polynomialZero
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> polynomialOne: Polynomial get() = polynomialSpace.polynomialOne
// endregion

// region Integer-to-Number conversion
context(polynomialSpace: PolynomialSpace<Number, *>)
public fun <Number> numberValueOf(value: Int): Number = polynomialSpace.numberValueOf(value)
context(polynomialSpace: PolynomialSpace<Number, *>)
public fun <Number> numberValueOf(value: UInt): Number = polynomialSpace.numberValueOf(value)
context(polynomialSpace: PolynomialSpace<Number, *>)
public fun <Number> numberValueOf(value: Long): Number = polynomialSpace.numberValueOf(value)
context(polynomialSpace: PolynomialSpace<Number, *>)
public fun <Number> numberValueOf(value: ULong): Number = polynomialSpace.numberValueOf(value)
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> Int.numberValue: Number get() = with(polynomialSpace) { this@numberValue.numberValue }
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> UInt.numberValue: Number get() = with(polynomialSpace) { this@numberValue.numberValue }
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> Long.numberValue: Number get() = with(polynomialSpace) { this@numberValue.numberValue }
context(polynomialSpace: PolynomialSpace<Number, *>)
public val <Number> ULong.numberValue: Number get() = with(polynomialSpace) { this@numberValue.numberValue }
// endregion

// region Integer-to-Polynomial conversion
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> valueOf(value: Int): Polynomial = polynomialSpace.valueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> valueOf(value: UInt): Polynomial = polynomialSpace.valueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> valueOf(value: Long): Polynomial = polynomialSpace.valueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> valueOf(value: ULong): Polynomial = polynomialSpace.valueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> polynomialValueOf(value: Int): Polynomial = polynomialSpace.polynomialValueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> polynomialValueOf(value: UInt): Polynomial = polynomialSpace.polynomialValueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> polynomialValueOf(value: Long): Polynomial = polynomialSpace.polynomialValueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public fun <Polynomial> polynomialValueOf(value: ULong): Polynomial = polynomialSpace.polynomialValueOf(value)
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> Int.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> UInt.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> Long.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> ULong.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
// endregion

// region Number-to-Polynomial conversion
context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
@JvmName("polynomialValueOfNumber")
public fun <Number, Polynomial> polynomialValueOf(value: Number): Polynomial = polynomialSpace.polynomialValueOf(value)
context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
@get:JvmName("polynomialValueNumber")
public val <Number, Polynomial> Number.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
// endregion

// region Number-Polynomial operations
// FIXME: KT-74730
//context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
//public operator fun <Number, Polynomial> Number.plus(other: Polynomial): Polynomial = with(polynomialSpace) { this@plus + other }
// FIXME: KT-74730
//context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
//public operator fun <Number, Polynomial> Number.minus(other: Polynomial): Polynomial = with(polynomialSpace) { this@minus - other }
//// FIXME: KT-74730
//context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
//public operator fun <Number, Polynomial> Number.times(other: Polynomial): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Polynomial-Number operations
context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
public operator fun <Number, Polynomial> Polynomial.plus(other: Number): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
public operator fun <Number, Polynomial> Polynomial.minus(other: Number): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: PolynomialSpace<Number, Polynomial>)
public operator fun <Number, Polynomial> Polynomial.times(other: Number): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Polynomial properties
context(polynomialSpace: PolynomialSpace<*, Polynomial>)
public val <Polynomial> Polynomial.degree: UInt get() = with(polynomialSpace) { this@degree.degree }
// endregion

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface UnivariatePolynomialSpace<Number, Polynomial> : PolynomialSpace<Number, Polynomial> {
    public val variable: Polynomial
    
    public class Key<Number, Polynomial>(
        numberType: SuppliedType<Number>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<UnivariatePolynomialSpace<Number, Polynomial>> {
        override val typeKey: SuppliedType.Regular<UnivariatePolynomialSpace<Number, Polynomial>> =
            SuppliedType.Regular(
                kClass = UnivariatePolynomialSpace::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

context(polynomialSpace: UnivariatePolynomialSpace<*, Polynomial>)
public val <Polynomial> variable: Polynomial get() = polynomialSpace.variable

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<Number, Variable, Polynomial> : PolynomialSpace<Number, Polynomial> {
    // region Variable-to-Polynomial conversion
    @JvmName("valueOfVariable")
    public fun polynomialValueOf(variable: Variable): Polynomial
    @get:JvmName("valueVariable")
    public val Variable.polynomialValue: Polynomial get() = polynomialValueOf(this)
    // endregion

    // region Variable-Int operations
    @JvmName("plusVariableInt")
    public operator fun Variable.plus(other: Int): Polynomial
    @JvmName("minusVariableInt")
    public operator fun Variable.minus(other: Int): Polynomial
    @JvmName("timesVariableInt")
    public operator fun Variable.times(other: Int): Polynomial
    // endregion
    
    // region Variable-UInt operations
    @JvmName("plusVariableUInt")
    public operator fun Variable.plus(other: UInt): Polynomial
    @JvmName("minusVariableUInt")
    public operator fun Variable.minus(other: UInt): Polynomial
    @JvmName("timesVariableUInt")
    public operator fun Variable.times(other: UInt): Polynomial
    // endregion

    // region Variable-Long operations
    @JvmName("plusVariableLong")
    public operator fun Variable.plus(other: Long): Polynomial
    @JvmName("minusVariableLong")
    public operator fun Variable.minus(other: Long): Polynomial
    @JvmName("timesVariableLong")
    public operator fun Variable.times(other: Long): Polynomial
    // endregion
    
    // region Variable-ULong operations
    @JvmName("plusVariableULong")
    public operator fun Variable.plus(other: ULong): Polynomial
    @JvmName("minusVariableULong")
    public operator fun Variable.minus(other: ULong): Polynomial
    @JvmName("timesVariableULong")
    public operator fun Variable.times(other: ULong): Polynomial
    // endregion

    // region Int-Variable operations
    @JvmName("plusIntVariable")
    public operator fun Int.plus(other: Variable): Polynomial
    @JvmName("minusIntVariable")
    public operator fun Int.minus(other: Variable): Polynomial
    @JvmName("timesIntVariable")
    public operator fun Int.times(other: Variable): Polynomial
    // endregion
    
    // region UInt-Variable operations
    @JvmName("plusUIntVariable")
    public operator fun UInt.plus(other: Variable): Polynomial
    @JvmName("minusUIntVariable")
    public operator fun UInt.minus(other: Variable): Polynomial
    @JvmName("timesUIntVariable")
    public operator fun UInt.times(other: Variable): Polynomial
    // endregion

    // region Long-Variable operations
    @JvmName("plusLongVariable")
    public operator fun Long.plus(other: Variable): Polynomial
    @JvmName("minusLongVariable")
    public operator fun Long.minus(other: Variable): Polynomial
    @JvmName("timesLongVariable")
    public operator fun Long.times(other: Variable): Polynomial
    // endregion
    
    // region ULong-Variable operations
    @JvmName("plusULongVariable")
    public operator fun ULong.plus(other: Variable): Polynomial
    @JvmName("minusULongVariable")
    public operator fun ULong.minus(other: Variable): Polynomial
    @JvmName("timesULongVariable")
    public operator fun ULong.times(other: Variable): Polynomial
    // endregion

    // region Variable-Number operations
    @JvmName("plusVariableNumber")
    public operator fun Variable.plus(other: Number): Polynomial
    @JvmName("minusVariableNumber")
    public operator fun Variable.minus(other: Number): Polynomial
    @JvmName("timesVariableNumber")
    public operator fun Variable.times(other: Number): Polynomial
    // endregion

    // region Number-Variable operations
    @JvmName("plusNumberVariable")
    public operator fun Number.plus(other: Variable): Polynomial
    @JvmName("minusNumberVariable")
    public operator fun Number.minus(other: Variable): Polynomial
    @JvmName("timesNumberVariable")
    public operator fun Number.times(other: Variable): Polynomial
    // endregion

    // region Variable-Variable operations
    @JvmName("unaryMinusVariable")
    public operator fun Variable.unaryMinus(): Polynomial
    @JvmName("plusVariableVariable")
    public operator fun Variable.plus(other: Variable): Polynomial
    @JvmName("minusVariableVariable")
    public operator fun Variable.minus(other: Variable): Polynomial
    @JvmName("timesVariableVariable")
    public operator fun Variable.times(other: Variable): Polynomial
    // endregion

    // region Variable-Polynomial operations
    @JvmName("plusVariablePolynomial")
    public operator fun Variable.plus(other: Polynomial): Polynomial
    @JvmName("minusVariablePolynomial")
    public operator fun Variable.minus(other: Polynomial): Polynomial
    @JvmName("timesVariablePolynomial")
    public operator fun Variable.times(other: Polynomial): Polynomial
    // endregion

    // region Polynomial-Variable operations
    @JvmName("plusPolynomialVariable")
    public operator fun Polynomial.plus(other: Variable): Polynomial
    @JvmName("minusPolynomialVariable")
    public operator fun Polynomial.minus(other: Variable): Polynomial
    @JvmName("timesPolynomialVariable")
    public operator fun Polynomial.times(other: Variable): Polynomial
    // endregion

    // region Polynomial properties
    public val Polynomial.degrees: KoneMap<Variable, UInt>
    public fun Polynomial.degreeBy(variable: Variable): UInt = degrees.getOrElse(variable) { 0u }
    public fun Polynomial.degreeBy(variables: KoneSet<Variable>): UInt
    public val Polynomial.variables: KoneSet<Variable> get() = degrees.keys
    public val Polynomial.numberOfVariables: UInt get() = variables.size
    // endregion
    
    public class Key<Number, Variable, Polynomial>(
        numberType: SuppliedType<Number>,
        variableType: SuppliedType<Variable>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<MultivariatePolynomialSpace<Number, Variable, Polynomial>> {
        override val typeKey: SuppliedType.Regular<MultivariatePolynomialSpace<Number, Variable, Polynomial>> =
            SuppliedType.Regular(
                kClass = MultivariatePolynomialSpace::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        variableType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

// region Variable-to-Polynomial conversion
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("polynomialValueOfVariable")
public fun <Variable, Polynomial> polynomialValueOf(variable: Variable): Polynomial = polynomialSpace.polynomialValueOf(variable)
// FIXME: KT-74730
//context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
//@get:JvmName("polynomialValueVariable")
//public val <Variable, Polynomial> Variable.polynomialValue: Polynomial get() = with(polynomialSpace) { this@polynomialValue.polynomialValue }
// endregion

// region Variable-Int operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariableInt")
public operator fun <Variable, Polynomial> Variable.plus(other: Int): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariableInt")
public operator fun <Variable, Polynomial> Variable.minus(other: Int): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariableInt")
public operator fun <Variable, Polynomial> Variable.times(other: Int): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-UInt operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariableUInt")
public operator fun <Variable, Polynomial> Variable.plus(other: UInt): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariableUInt")
public operator fun <Variable, Polynomial> Variable.minus(other: UInt): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariableUInt")
public operator fun <Variable, Polynomial> Variable.times(other: UInt): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-Long operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariableLong")
public operator fun <Variable, Polynomial> Variable.plus(other: Long): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariableLong")
public operator fun <Variable, Polynomial> Variable.minus(other: Long): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariableLong")
public operator fun <Variable, Polynomial> Variable.times(other: Long): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-ULong operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariableULong")
public operator fun <Variable, Polynomial> Variable.plus(other: ULong): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariableULong")
public operator fun <Variable, Polynomial> Variable.minus(other: ULong): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariableULong")
public operator fun <Variable, Polynomial> Variable.times(other: ULong): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Int-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusIntVariable")
public operator fun <Variable, Polynomial> Int.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusIntVariable")
public operator fun <Variable, Polynomial> Int.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesIntVariable")
public operator fun <Variable, Polynomial> Int.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region UInt-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusUIntVariable")
public operator fun <Variable, Polynomial> UInt.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusUIntVariable")
public operator fun <Variable, Polynomial> UInt.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesUIntVariable")
public operator fun <Variable, Polynomial> UInt.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Long-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusLongVariable")
public operator fun <Variable, Polynomial> Long.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusLongVariable")
public operator fun <Variable, Polynomial> Long.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesLongVariable")
public operator fun <Variable, Polynomial> Long.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region ULong-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusULongVariable")
public operator fun <Variable, Polynomial> ULong.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusULongVariable")
public operator fun <Variable, Polynomial> ULong.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesULongVariable")
public operator fun <Variable, Polynomial> ULong.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-Number operations
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("plusVariableNumber")
public operator fun <Number, Variable, Polynomial> Variable.plus(other: Number): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("minusVariableNumber")
public operator fun <Number, Variable, Polynomial> Variable.minus(other: Number): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("timesVariableNumber")
public operator fun <Number, Variable, Polynomial> Variable.times(other: Number): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Number-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("plusNumberVariable")
public operator fun <Number, Variable, Polynomial> Number.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("minusNumberVariable")
public operator fun <Number, Variable, Polynomial> Number.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<Number, Variable, Polynomial>)
@JvmName("timesNumberVariable")
public operator fun <Number, Variable, Polynomial> Number.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("unaryMinusVariable")
public operator fun <Variable, Polynomial> Variable.unaryMinus(): Polynomial = with(polynomialSpace) { -this@unaryMinus }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariableVariable")
public operator fun <Variable, Polynomial> Variable.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariableVariable")
public operator fun <Variable, Polynomial> Variable.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariableVariable")
public operator fun <Variable, Polynomial> Variable.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Variable-Polynomial operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusVariablePolynomial")
public operator fun <Variable, Polynomial> Variable.plus(other: Polynomial): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusVariablePolynomial")
public operator fun <Variable, Polynomial> Variable.minus(other: Polynomial): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesVariablePolynomial")
public operator fun <Variable, Polynomial> Variable.times(other: Polynomial): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Polynomial-Variable operations
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("plusPolynomialVariable")
public operator fun <Variable, Polynomial> Polynomial.plus(other: Variable): Polynomial = with(polynomialSpace) { this@plus + other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("minusPolynomialVariable")
public operator fun <Variable, Polynomial> Polynomial.minus(other: Variable): Polynomial = with(polynomialSpace) { this@minus - other }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
@JvmName("timesPolynomialVariable")
public operator fun <Variable, Polynomial> Polynomial.times(other: Variable): Polynomial = with(polynomialSpace) { this@times * other }
// endregion

// region Polynomial properties
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
public val <Variable, Polynomial> Polynomial.degrees: KoneMap<Variable, UInt> get() = with(polynomialSpace) { this@degrees.degrees }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
public fun <Variable, Polynomial> Polynomial.degreeBy(variable: Variable): UInt = with(polynomialSpace) { this@degreeBy.degreeBy(variable) }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
public fun <Variable, Polynomial> Polynomial.degreeBy(variables: Collection<Variable>): UInt = with(polynomialSpace) { this@degreeBy.degreeBy(variables) }
context(polynomialSpace: MultivariatePolynomialSpace<*, Variable, Polynomial>)
public val <Variable, Polynomial> Polynomial.variables: KoneSet<Variable> get() = with(polynomialSpace) { this@variables.variables }
context(polynomialSpace: MultivariatePolynomialSpace<*, *, Polynomial>)
public val <Polynomial> Polynomial.numberOfVariables: UInt get() = with(polynomialSpace) { this@numberOfVariables.numberOfVariables }
// endregion

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface PolynomialSpaceOverField<Number, Polynomial> : PolynomialSpace<Number, Polynomial> {
    // region Number-Int operations
    @JvmName("divPolynomialInt")
    @JsName("divPolynomialInt")
    public operator fun Polynomial.div(other: Int): Polynomial = this / other.numberValue
    // endregion
    
    // region Number-UInt operations
    @JvmName("divPolynomialUInt")
    @JsName("divPolynomialUInt")
    public operator fun Polynomial.div(other: UInt): Polynomial = this / other.numberValue
    // endregion

    // region Number-Long operations
    @JvmName("divPolynomialLong")
    @JsName("divPolynomialLong")
    public operator fun Polynomial.div(other: Long): Polynomial = this / other.numberValue
    // endregion
    
    // region Number-ULong operations
    @JvmName("divPolynomialULong")
    @JsName("divPolynomialULong")
    public operator fun Polynomial.div(other: ULong): Polynomial = this / other.numberValue
    // endregion

    // region Polynomial-Number operations
    @JvmName("divPolynomialNumber")
    @JsName("divPolynomialNumber")
    public operator fun Polynomial.div(other: Number): Polynomial
    // endregion
    
    public class Key<Number, Polynomial>(
        numberType: SuppliedType<Number>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<PolynomialSpaceOverField<Number, Polynomial>> {
        override val typeKey: SuppliedType.Regular<PolynomialSpaceOverField<Number, Polynomial>> =
            SuppliedType.Regular(
                kClass = PolynomialSpaceOverField::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

// region Number-Int operations
context(polynomialSpace: PolynomialSpaceOverField<*, Polynomial>)
@JvmName("divPolynomialInt")
@JsName("divPolynomialInt")
public operator fun <Polynomial> Polynomial.div(other: Int): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Number-UInt operations
context(polynomialSpace: PolynomialSpaceOverField<*, Polynomial>)
@JvmName("divPolynomialUInt")
@JsName("divPolynomialUInt")
public operator fun <Polynomial> Polynomial.div(other: UInt): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Number-Long operations
context(polynomialSpace: PolynomialSpaceOverField<*, Polynomial>)
@JvmName("divPolynomialLong")
@JsName("divPolynomialLong")
public operator fun <Polynomial> Polynomial.div(other: Long): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Number-ULong operations
context(polynomialSpace: PolynomialSpaceOverField<*, Polynomial>)
@JvmName("divPolynomialULong")
@JsName("divPolynomialULong")
public operator fun <Polynomial> Polynomial.div(other: ULong): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Polynomial-Number operations
context(polynomialSpace: PolynomialSpaceOverField<Number, Polynomial>)
@JvmName("divPolynomialNumber")
@JsName("divPolynomialNumber")
public operator fun <Number, Polynomial> Polynomial.div(other: Number): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

@OptIn(ExperimentalKoneAPI::class)
public interface UnivariatePolynomialSpaceOverField<Number, Polynomial> : PolynomialSpaceOverField<Number, Polynomial>, UnivariatePolynomialSpace<Number, Polynomial>, EuclideanRing<Polynomial> {
    public class Key<Number, Polynomial>(
        numberType: SuppliedType<Number>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<UnivariatePolynomialSpaceOverField<Number, Polynomial>> {
        override val typeKey: SuppliedType.Regular<UnivariatePolynomialSpaceOverField<Number, Polynomial>> =
            SuppliedType.Regular(
                kClass = UnivariatePolynomialSpaceOverField::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpaceOverField<Number, Variable, Polynomial> : PolynomialSpaceOverField<Number, Polynomial>, MultivariatePolynomialSpace<Number, Variable, Polynomial> {
    // region Variable-Int operations
    @JvmName("divVariableInt")
    public operator fun Variable.div(other: Int): Polynomial
    // endregion
    
    // region Variable-UInt operations
    @JvmName("divVariableUInt")
    public operator fun Variable.div(other: UInt): Polynomial
    // endregion

    // region Variable-Long operations
    @JvmName("divVariableLong")
    public operator fun Variable.div(other: Long): Polynomial
    // endregion
    
    // region Variable-ULong operations
    @JvmName("divVariableULong")
    public operator fun Variable.div(other: ULong): Polynomial
    // endregion

    // region Variable-Number operations
    @JvmName("divVariableNumber")
    public operator fun Variable.div(other: Number): Polynomial
    // endregion
    
    public class Key<Number, Variable, Polynomial>(
        numberType: SuppliedType<Number>,
        variableType: SuppliedType<Variable>,
        polynomialType: SuppliedType<Polynomial>
    ) : RegistryKey<MultivariatePolynomialSpaceOverField<Number, Variable, Polynomial>> {
        override val typeKey: SuppliedType.Regular<MultivariatePolynomialSpaceOverField<Number, Variable, Polynomial>> =
            SuppliedType.Regular(
                kClass = MultivariatePolynomialSpaceOverField::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        numberType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        variableType
                    ),
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        polynomialType
                    )
                ),
                isNullable = false
            )
    }
}

// region Variable-Int operations
context(polynomialSpace: MultivariatePolynomialSpaceOverField<*, Variable, Polynomial>)
@JvmName("divVariableInt")
public operator fun <Variable, Polynomial> Variable.div(other: Int): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Variable-UInt operations
context(polynomialSpace: MultivariatePolynomialSpaceOverField<*, Variable, Polynomial>)
@JvmName("divVariableUInt")
public operator fun <Variable, Polynomial> Variable.div(other: UInt): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Variable-Long operations
context(polynomialSpace: MultivariatePolynomialSpaceOverField<*, Variable, Polynomial>)
@JvmName("divVariableLong")
public operator fun <Variable, Polynomial> Variable.div(other: Long): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Variable-ULong operations
context(polynomialSpace: MultivariatePolynomialSpaceOverField<*, Variable, Polynomial>)
@JvmName("divVariableULong")
public operator fun <Variable, Polynomial> Variable.div(other: ULong): Polynomial = with(polynomialSpace) { this@div / other }
// endregion

// region Variable-Number operations
context(polynomialSpace: MultivariatePolynomialSpaceOverField<Number, Variable, Polynomial>)
@JvmName("divVariableNumber")
public operator fun <Number, Variable, Polynomial> Variable.div(other: Number): Polynomial = with(polynomialSpace) { this@div / other }
// endregion