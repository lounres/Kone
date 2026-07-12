/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.PlanarVectorArgumentComputer
import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.planarVectorArgument
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.contexts.KoneContextHolder
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.unwrapLocallyAsExtensionReceivers
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.*
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import kotlinx.serialization.Serializable


@Serializable
public data class ComplexNumber<out Number>(
    val realPart: Number,
    val imaginaryPart: Number,
) {
    public companion object;
}

private class ComplexNumberEquality<in Number>(
    private val numberEquality: Equality<Number>
) : Equality<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.equalsTo(other: ComplexNumber<Number>): Boolean =
        numberEquality { this.realPart eq other.realPart && this.imaginaryPart eq other.imaginaryPart }
}

public fun <Number> ComplexNumber.Companion.equality(numberEquality: Equality<Number>): Equality<ComplexNumber<Number>> =
    ComplexNumberEquality(numberEquality)

private class ComplexNumberHashing<in Number>(
    private val numberHashing: Hashing<Number>
) : Hashing<ComplexNumber<Number>> {
    override fun ComplexNumber<Number>.hash(): Int =
        numberHashing { this.realPart.hash() * 31 + this.imaginaryPart.hash() }
}

public fun <Number> ComplexNumber.Companion.hashing(numberHashing: Hashing<Number>): Hashing<ComplexNumber<Number>> =
    ComplexNumberHashing(numberHashing)

@Suppress("UNCHECKED_CAST")
private class ComplexNumberReification<out Number>(
    private val numberReification: Reification<Number>
) : Reification<ComplexNumber<Number>> {
    override fun contains(element: Any?): Boolean =
        element is ComplexNumber<*>
                && element.realPart in numberReification
                && element.imaginaryPart in numberReification
    override fun reifyMaybe(element: Any?): Maybe<ComplexNumber<Number>> =
        if (element in this) Some(element as ComplexNumber<Number>) else None
    override fun reifyOrNull(element: Any?): ComplexNumber<Number>? =
        if (element in this) element as ComplexNumber<Number> else null
    override fun reify(element: Any?): ComplexNumber<Number> =
        if (element in this) element as ComplexNumber<Number> else reificationException()
}

public fun <Number> ComplexNumber.Companion.reification(numberReification: Reification<Number>): Reification<ComplexNumber<Number>> =
    ComplexNumberReification(numberReification)

@Suppress("UNCHECKED_CAST")
private class ComplexNumberFieldExtension<Number>(
    private val numberField: Field<Number>
) : FieldExtension<Number, ComplexNumber<Number>> {
    // region Constants
    override val zero: ComplexNumber<Number> = ComplexNumber(numberField.zero, numberField.zero)
    override val one: ComplexNumber<Number> = ComplexNumber(numberField.one, numberField.zero)
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<ComplexNumber<Number>> = IsZero {
        context(numberField.numberIsZero) { this.realPart.isZero() && this.imaginaryPart.isZero() }
    }
    override val numberIsOne: IsOne<ComplexNumber<Number>> = IsOne {
        context(numberField.numberIsOne, numberField.numberIsZero) { this.realPart.isOne() && this.imaginaryPart.isZero() }
    }
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): ComplexNumber<Number> = ComplexNumber(numberField.valueOf(arg), numberField.zero)
    override fun valueOf(arg: Int): ComplexNumber<Number> = ComplexNumber(numberField.valueOf(arg), numberField.zero)
    override fun valueOf(arg: ULong): ComplexNumber<Number> = ComplexNumber(numberField.valueOf(arg), numberField.zero)
    override fun valueOf(arg: Long): ComplexNumber<Number> = ComplexNumber(numberField.valueOf(arg), numberField.zero)
    // endregion
    
    // region Field conversions
    override fun valueOf(arg: Number): ComplexNumber<Number> = ComplexNumber(arg, numberField.zero)
    // endregion
    
    // region ComplexNumber-Int operations
    override val numberPlusInt: Plus<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusInt { realPart + other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberMinusInt: Minus<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusInt { realPart - other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberTimesInt: Times<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesInt {
            ComplexNumber(
                realPart = realPart * other,
                imaginaryPart = imaginaryPart * other
            )
        }
    }
    override val numberDivideInt: Divide<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Divide { other ->
        numberField.numberDivideInt {
            ComplexNumber(
                realPart = realPart / other,
                imaginaryPart = imaginaryPart / other
            )
        }
    }
    // endregion
    
    // region ComplexNumber-UInt operations
    override val numberPlusUInt: Plus<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusUInt { realPart + other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberMinusUInt: Minus<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusUInt { realPart - other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberTimesUInt: Times<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesUInt {
            ComplexNumber(
                realPart = realPart * other,
                imaginaryPart = imaginaryPart * other
            )
        }
    }
    override val numberDivideUInt: Divide<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Divide { other ->
        numberField.numberDivideUInt {
            ComplexNumber(
                realPart = realPart / other,
                imaginaryPart = imaginaryPart / other
            )
        }
    }
    // endregion
    
    // region ComplexNumber-Long operations
    override val numberPlusLong: Plus<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusLong { realPart + other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberMinusLong: Minus<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusLong { realPart - other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberTimesLong: Times<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesLong {
            ComplexNumber(
                realPart = realPart * other,
                imaginaryPart = imaginaryPart * other
            )
        }
    }
    override val numberDivideLong: Divide<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Divide { other ->
        numberField.numberDivideLong {
            ComplexNumber(
                realPart = realPart / other,
                imaginaryPart = imaginaryPart / other
            )
        }
    }
    // endregion
    
    // region ComplexNumber-ULong operations
    override val numberPlusULong: Plus<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusULong { realPart + other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberMinusULong: Minus<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusULong { realPart - other },
            imaginaryPart = imaginaryPart
        )
    }
    override val numberTimesULong: Times<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesULong {
            ComplexNumber(
                realPart = realPart * other,
                imaginaryPart = imaginaryPart * other
            )
        }
    }
    override val numberDivideULong: Divide<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Divide { other ->
        numberField.numberDivideULong {
            ComplexNumber(
                realPart = realPart / other,
                imaginaryPart = imaginaryPart / other
            )
        }
    }
    // endregion
    
    // region ComplexNumber-Number operations
    override val vectorPlusNumber: Plus<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusNumber { realPart + other },
            imaginaryPart = imaginaryPart
        )
    }
    override val vectorMinusNumber: Minus<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusNumber { realPart - other },
            imaginaryPart = imaginaryPart
        )
    }
    override val vectorTimesNumber: Times<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesNumber {
            ComplexNumber(
                realPart = realPart * other,
                imaginaryPart = imaginaryPart * other
            )
        }
    }
    override val vectorDivideNumber: Divide<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Divide { other ->
        numberField.numberDivideNumber {
            ComplexNumber(
                realPart = realPart / other,
                imaginaryPart = imaginaryPart / other
            )
        }
    }
    // endregion
    
    // region Int-ComplexNumber operations
    override val intPlusNumber: Plus<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.intPlusNumber { this + other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val intMinusNumber: Minus<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.intMinusNumber { this - other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val intTimesNumber: Times<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        numberField.intTimesNumber {
            ComplexNumber(
                realPart = this * other.realPart,
                imaginaryPart = this * other.imaginaryPart,
            )
        }
    }
    override val intDivideNumber: Divide<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = this@Divide / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region UInt-ComplexNumber operations
    override val uIntPlusNumber: Plus<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.uIntPlusNumber { this + other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val uIntMinusNumber: Minus<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.uIntMinusNumber { this - other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val uIntTimesNumber: Times<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        numberField.uIntTimesNumber {
            ComplexNumber(
                realPart = this * other.realPart,
                imaginaryPart = this * other.imaginaryPart,
            )
        }
    }
    override val uIntDivideNumber: Divide<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = this@Divide / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Long-ComplexNumber operations
    override val longPlusNumber: Plus<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.longPlusNumber { this + other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val longMinusNumber: Minus<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.longMinusNumber { this - other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val longTimesNumber: Times<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        numberField.longTimesNumber {
            ComplexNumber(
                realPart = this * other.realPart,
                imaginaryPart = this * other.imaginaryPart,
            )
        }
    }
    override val longDivideNumber: Divide<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = this@Divide / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ULong-ComplexNumber operations
    override val uLongPlusNumber: Plus<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.uLongPlusNumber { this + other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val uLongMinusNumber: Minus<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.uLongMinusNumber { this - other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val uLongTimesNumber: Times<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        numberField.uLongTimesNumber {
            ComplexNumber(
                realPart = this * other.realPart,
                imaginaryPart = this * other.imaginaryPart,
            )
        }
    }
    override val uLongDivideNumber: Divide<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = this@Divide / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Number-ComplexNumber operations
    override val numberPlusVector: Plus<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        ComplexNumber(
            realPart = numberField.numberPlusNumber { this + other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val numberMinusVector: Minus<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        ComplexNumber(
            realPart = numberField.numberMinusNumber { this - other.realPart },
            imaginaryPart = other.imaginaryPart,
        )
    }
    override val numberTimesVector: Times<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        numberField.numberTimesNumber {
            ComplexNumber(
                realPart = this * other.realPart,
                imaginaryPart = this * other.imaginaryPart,
            )
        }
    }
    override val numberDivideVector: Divide<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = this@Divide / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ComplexNumber-ComplexNumber operations
    override val numberUnaryMinus: UnaryMinus<ComplexNumber<Number>, ComplexNumber<Number>> = UnaryMinus {
        numberField.numberUnaryMinus {
            ComplexNumber(
                realPart = -realPart,
                imaginaryPart = -imaginaryPart,
            )
        }
    }
    override val numberPlusNumber: Plus<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { other ->
        numberField.numberPlusNumber {
            ComplexNumber(
                realPart = realPart + other.realPart,
                imaginaryPart = imaginaryPart + other.imaginaryPart,
            )
        }
    }
    override val numberMinusNumber: Minus<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { other ->
        numberField.numberMinusNumber {
            ComplexNumber(
                realPart = realPart - other.realPart,
                imaginaryPart = imaginaryPart - other.imaginaryPart,
            )
        }
    }
    override val numberTimesNumber: Times<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Times { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        ComplexNumber(
            realPart = realPart * other.realPart - imaginaryPart * other.imaginaryPart,
            imaginaryPart = realPart * other.imaginaryPart + imaginaryPart * other.realPart,
        )
    }
    override val numberReciprocal: Reciprocal<ComplexNumber<Number>, ComplexNumber<Number>> = Reciprocal {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val norm = realPart.let { it * it } + imaginaryPart.let { it * it }
        ComplexNumber(
            realPart = realPart / norm,
            imaginaryPart = -imaginaryPart / norm,
        )
    }
    override val numberDivideNumber: Divide<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { other ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val norm = (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = (realPart * other.realPart + imaginaryPart * other.imaginaryPart) / norm,
            imaginaryPart = (imaginaryPart * other.realPart - realPart * other.imaginaryPart) / norm,
        )
    }
    // endregion
}

public fun <Number> ComplexNumber.Companion.fieldExtensionOver(numberField: Field<Number>): FieldExtension<Number, ComplexNumber<Number>> =
    ComplexNumberFieldExtension(numberField)

@Suppliable
context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ComplexNumber.Companion.fieldExtensionOver(): FieldExtension<Number, ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return fieldExtensionOver(
        numberField = koneContextRegistry[Field.Key<Number>()],
    )
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> ComplexNumber.Companion.setFieldExtensionOver() {
    FieldExtension.Key<Number, ComplexNumber<Number>>().withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        fieldExtensionOver()
    }
}

context(ring: CommutativeRing<Number>)
public fun <Number> ComplexNumber<Number>.conjugate(): ComplexNumber<Number> =
    ComplexNumber(
        realPart = realPart,
        imaginaryPart = ring.numberUnaryMinus { -imaginaryPart },
    )
context(ring: CommutativeRing<Number>)
public fun <Number> ComplexNumber<Number>.norm(): Number =
    context(ring.numberTimesNumber, ring.numberPlusNumber) { realPart * realPart + imaginaryPart * imaginaryPart }
context(_: CommutativeRing<Number>, _: PositiveSquareRootComputer<Number>)
public fun <Number> ComplexNumber<Number>.absoluteValue(): Number = norm().positiveSquareRoot()
context(_: PlanarVectorArgumentComputer<Number>)
public fun <Number> ComplexNumber<Number>.argument(): Number = planarVectorArgument(realPart, imaginaryPart)