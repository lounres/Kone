/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


/**
 * Describes a context that represents [mathematical field](https://en.wikipedia.org/wiki/Field_(mathematics)).
 * It means that it is an extension of [Ring] interface that also provides division and exponentiation to the negative
 * integer power. See docs of [Ring] for a full description and docs of the [Field] interface's operations.
 */
public interface Field<Number> : CommutativeRing<Number> {
    /**
     * Divides [this] number by [other] number in terms of the [Field].
     */
    public operator fun Number.div(other: Number): Number
    /**
     * Finds reciprocal of [this] number in terms of the [Field].
     *
     * The result is equal to `one / this`.
     */
    public fun Number.reciprocal(): Number = one / this
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / valueOf(other)`.
     */
    public operator fun Number.div(other: Int): Number = this / valueOf(other)
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / valueOf(other)`.
     */
    public operator fun Number.div(other: UInt): Number = this / valueOf(other)
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / valueOf(other)`.
     */
    public operator fun Number.div(other: Long): Number = this / valueOf(other)
    /**
     * Divides [this] number by [other] integer as elements of the [Field].
     *
     * The result is equal to `this / valueOf(other)`.
     */
    public operator fun Number.div(other: ULong): Number = this / valueOf(other)
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `valueOf(this)` / other.
     */
    public operator fun Int.div(other: Number): Number = valueOf(this) / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `valueOf(this)` / other`.
     */
    public operator fun UInt.div(other: Number): Number = valueOf(this) / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `valueOf(this)` / other`.
     */
    public operator fun Long.div(other: Number): Number = valueOf(this) / other
    /**
     * Divides [this] integer by [other] number as elements of the [Field].
     *
     * The result is equal to `valueOf(this)` / other`.
     */
    public operator fun ULong.div(other: Number): Number = valueOf(this) / other
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [base] copies otherwise.
     */
    public fun power(base: Number, exponent: Int): Number =
        if (exponent >= 0) power(base, exponent.toUInt())
        else one / power(base, (-exponent).toUInt())
    /**
     * Raises [base] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [base] copies otherwise.
     */
    public fun power(base: Number, exponent: Long): Number =
        if (exponent >= 0) power(base, exponent.toULong())
        else one / power(base, (-exponent).toULong())
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [this] copies otherwise.
     */
    public infix fun Number.pow(exponent: Int): Number = power(this, exponent)
    /**
     * Raises [this] number in the power of [exponent].
     *
     * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
     * and reciprocal of product of `-exponent` number of [this] copies otherwise.
     */
    public infix fun Number.pow(exponent: Long): Number = power(this, exponent)
    
    public companion object;
    
    /**
     * Registry key for [Field] interface in [KoneContextRegistry].
     */
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<Field<Number>> {
        @OptIn(DelicateSuppliedTypeConstructor::class)
        public val typeKey: SuppliedType.Regular =
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Field",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    )
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<Field<Number>> = ImpliedKeysRegistry {
            CommutativeRing.Key<Number>(numberType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.Field.Key<$numberType>"
    }
}

/**
 * Divides [this] number by [other] number in terms of the [Field].
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Number.div(other: Number): Number = with(field) { this@div / other }
/**
 * Finds reciprocal of [this] number in terms of the [Field].
 *
 * The result is equal to `one / this`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public fun <Number> Number.reciprocal(): Number = with(field) { this@reciprocal.reciprocal() }
/**
 * Divides [this] number by [other] integer as elements of the [Field].
 *
 * The result is equal to `this / valueOf(other)`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Number.div(other: Int): Number = with(field) { this@div / other }
/**
 * Divides [this] number by [other] integer as elements of the [Field].
 *
 * The result is equal to `this / valueOf(other)`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Number.div(other: UInt): Number = with(field) { this@div / other }
/**
 * Divides [this] number by [other] integer as elements of the [Field].
 *
 * The result is equal to `this / valueOf(other)`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Number.div(other: Long): Number = with(field) { this@div / other }
/**
 * Divides [this] number by [other] integer as elements of the [Field].
 *
 * The result is equal to `this / valueOf(other)`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Number.div(other: ULong): Number = with(field) { this@div / other }
/**
 * Divides [this] integer by [other] number as elements of the [Field].
 *
 * The result is equal to `valueOf(this)` / other`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Int.div(other: Number): Number = with(field) { this@div / other }
/**
 * Divides [this] integer by [other] number as elements of the [Field].
 *
 * The result is equal to `valueOf(this)` / other`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> UInt.div(other: Number): Number = with(field) { this@div / other }
/**
 * Divides [this] integer by [other] number as elements of the [Field].
 *
 * The result is equal to `valueOf(this)` / other`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> Long.div(other: Number): Number = with(field) { this@div / other }
/**
 * Divides [this] integer by [other] number as elements of the [Field].
 *
 * The result is equal to `valueOf(this)` / other`.
 *
 * A bridge contextual function for [Field.div].
 */
context(field: Field<Number>)
public operator fun <Number> ULong.div(other: Number): Number = with(field) { this@div / other }
/**
 * Raises [base] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
 * and reciprocal of product of `-exponent` number of [base] copies otherwise.
 *
 * A bridge contextual function for [Field.power].
 */
context(field: Field<Number>)
public fun <Number> power(base: Number, exponent: Int): Number = field.power(base, exponent)
/**
 * Raises [base] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [base] copies if the [exponent] is non-negative
 * and reciprocal of product of `-exponent` number of [base] copies otherwise.
 *
 * A bridge contextual function for [Field.power].
 */
context(field: Field<Number>)
public fun <Number> power(base: Number, exponent: Long): Number = field.power(base, exponent)
/**
 * Raises [this] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
 * and reciprocal of product of `-exponent` number of [this] copies otherwise.
 *
 * A bridge contextual function for [Field.pow].
 */
context(field: Field<Number>)
public infix fun <Number> Number.pow(exponent: Int): Number = with(field) { this@pow pow exponent }
/**
 * Raises [this] number in the power of [exponent].
 *
 * The result is equal to product of [exponent] number of [this] copies if the [exponent] is non-negative
 * and reciprocal of product of `-exponent` number of [this] copies otherwise.
 *
 * A bridge contextual function for [Field.pow].
 */
context(field: Field<Number>)
public infix fun <Number> Number.pow(exponent: Long): Number = with(field) { this@pow pow exponent }