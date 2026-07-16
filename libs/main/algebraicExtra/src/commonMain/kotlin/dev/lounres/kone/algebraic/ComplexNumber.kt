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
        context(numberField.numberIsZero) { it.realPart.isZero() && it.imaginaryPart.isZero() }
    }
    override val numberIsOne: IsOne<ComplexNumber<Number>> = IsOne {
        context(numberField.numberIsOne, numberField.numberIsZero) { it.realPart.isOne() && it.imaginaryPart.isZero() }
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
    override val numberPlusInt: Plus<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusInt { left.realPart + right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberMinusInt: Minus<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusInt { left.realPart - right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberTimesInt: Times<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesInt {
            ComplexNumber(
                realPart = left.realPart * right,
                imaginaryPart = left.imaginaryPart * right
            )
        }
    }
    override val numberDivideInt: Divide<ComplexNumber<Number>, Int, ComplexNumber<Number>> = Divide { left, right ->
        numberField.numberDivideInt {
            ComplexNumber(
                realPart = left.realPart / right,
                imaginaryPart = left.imaginaryPart / right
            )
        }
    }
    // endregion
    
    // region ComplexNumber-UInt operations
    override val numberPlusUInt: Plus<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusUInt { left.realPart + right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberMinusUInt: Minus<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusUInt { left.realPart - right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberTimesUInt: Times<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesUInt {
            ComplexNumber(
                realPart = left.realPart * right,
                imaginaryPart = left.imaginaryPart * right
            )
        }
    }
    override val numberDivideUInt: Divide<ComplexNumber<Number>, UInt, ComplexNumber<Number>> = Divide { left, right ->
        numberField.numberDivideUInt {
            ComplexNumber(
                realPart = left.realPart / right,
                imaginaryPart = left.imaginaryPart / right
            )
        }
    }
    // endregion
    
    // region ComplexNumber-Long operations
    override val numberPlusLong: Plus<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusLong { left.realPart + right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberMinusLong: Minus<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusLong { left.realPart - right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberTimesLong: Times<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesLong {
            ComplexNumber(
                realPart = left.realPart * right,
                imaginaryPart = left.imaginaryPart * right
            )
        }
    }
    override val numberDivideLong: Divide<ComplexNumber<Number>, Long, ComplexNumber<Number>> = Divide { left, right ->
        numberField.numberDivideLong {
            ComplexNumber(
                realPart = left.realPart / right,
                imaginaryPart = left.imaginaryPart / right
            )
        }
    }
    // endregion
    
    // region ComplexNumber-ULong operations
    override val numberPlusULong: Plus<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusULong { left.realPart + right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberMinusULong: Minus<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusULong { left.realPart - right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val numberTimesULong: Times<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesULong {
            ComplexNumber(
                realPart = left.realPart * right,
                imaginaryPart = left.imaginaryPart * right
            )
        }
    }
    override val numberDivideULong: Divide<ComplexNumber<Number>, ULong, ComplexNumber<Number>> = Divide { left, right ->
        numberField.numberDivideULong {
            ComplexNumber(
                realPart = left.realPart / right,
                imaginaryPart = left.imaginaryPart / right
            )
        }
    }
    // endregion
    
    // region ComplexNumber-Number operations
    override val vectorPlusNumber: Plus<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusNumber { left.realPart + right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val vectorMinusNumber: Minus<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusNumber { left.realPart - right },
            imaginaryPart = left.imaginaryPart
        )
    }
    override val vectorTimesNumber: Times<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesNumber {
            ComplexNumber(
                realPart = left.realPart * right,
                imaginaryPart = left.imaginaryPart * right
            )
        }
    }
    override val vectorDivideNumber: Divide<ComplexNumber<Number>, Number, ComplexNumber<Number>> = Divide { left, right ->
        numberField.numberDivideNumber {
            ComplexNumber(
                realPart = left.realPart / right,
                imaginaryPart = left.imaginaryPart / right
            )
        }
    }
    // endregion
    
    // region Int-ComplexNumber operations
    override val intPlusNumber: Plus<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.intPlusNumber { left + right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val intMinusNumber: Minus<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.intMinusNumber { left - right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val intTimesNumber: Times<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        numberField.intTimesNumber {
            ComplexNumber(
                realPart = left * right.realPart,
                imaginaryPart = left * right.imaginaryPart,
            )
        }
    }
    override val intDivideNumber: Divide<Int, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = left / (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = right.realPart * commonMultiplier,
            imaginaryPart = -right.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region UInt-ComplexNumber operations
    override val uIntPlusNumber: Plus<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.uIntPlusNumber { left + right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val uIntMinusNumber: Minus<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.uIntMinusNumber { left - right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val uIntTimesNumber: Times<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        numberField.uIntTimesNumber {
            ComplexNumber(
                realPart = left * right.realPart,
                imaginaryPart = left * right.imaginaryPart,
            )
        }
    }
    override val uIntDivideNumber: Divide<UInt, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = left / (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = right.realPart * commonMultiplier,
            imaginaryPart = -right.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Long-ComplexNumber operations
    override val longPlusNumber: Plus<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.longPlusNumber { left + right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val longMinusNumber: Minus<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.longMinusNumber { left - right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val longTimesNumber: Times<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        numberField.longTimesNumber {
            ComplexNumber(
                realPart = left * right.realPart,
                imaginaryPart = left * right.imaginaryPart,
            )
        }
    }
    override val longDivideNumber: Divide<Long, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = left / (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = right.realPart * commonMultiplier,
            imaginaryPart = -right.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ULong-ComplexNumber operations
    override val uLongPlusNumber: Plus<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.uLongPlusNumber { left + right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val uLongMinusNumber: Minus<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.uLongMinusNumber { left - right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val uLongTimesNumber: Times<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        numberField.uLongTimesNumber {
            ComplexNumber(
                realPart = left * right.realPart,
                imaginaryPart = left * right.imaginaryPart,
            )
        }
    }
    override val uLongDivideNumber: Divide<ULong, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = left / (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = right.realPart * commonMultiplier,
            imaginaryPart = -right.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Number-ComplexNumber operations
    override val numberPlusVector: Plus<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        ComplexNumber(
            realPart = numberField.numberPlusNumber { left + right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val numberMinusVector: Minus<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        ComplexNumber(
            realPart = numberField.numberMinusNumber { left - right.realPart },
            imaginaryPart = right.imaginaryPart,
        )
    }
    override val numberTimesVector: Times<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        numberField.numberTimesNumber {
            ComplexNumber(
                realPart = left * right.realPart,
                imaginaryPart = left * right.imaginaryPart,
            )
        }
    }
    override val numberDivideVector: Divide<Number, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val commonMultiplier = left / (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = right.realPart * commonMultiplier,
            imaginaryPart = -right.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ComplexNumber-ComplexNumber operations
    override val numberUnaryMinus: UnaryMinus<ComplexNumber<Number>, ComplexNumber<Number>> = UnaryMinus {
        numberField.numberUnaryMinus {
            ComplexNumber(
                realPart = -it.realPart,
                imaginaryPart = -it.imaginaryPart,
            )
        }
    }
    override val numberPlusNumber: Plus<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Plus { left, right ->
        numberField.numberPlusNumber {
            ComplexNumber(
                realPart = left.realPart + right.realPart,
                imaginaryPart = left.imaginaryPart + right.imaginaryPart,
            )
        }
    }
    override val numberMinusNumber: Minus<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Minus { left, right ->
        numberField.numberMinusNumber {
            ComplexNumber(
                realPart = left.realPart - right.realPart,
                imaginaryPart = left.imaginaryPart - right.imaginaryPart,
            )
        }
    }
    override val numberTimesNumber: Times<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Times { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        ComplexNumber(
            realPart = left.realPart * right.realPart - left.imaginaryPart * right.imaginaryPart,
            imaginaryPart = left.realPart * right.imaginaryPart + left.imaginaryPart * right.realPart,
        )
    }
    override val numberReciprocal: Reciprocal<ComplexNumber<Number>, ComplexNumber<Number>> = Reciprocal {
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val norm = it.realPart.let { it * it } + it.imaginaryPart.let { it * it }
        ComplexNumber(
            realPart = it.realPart / norm,
            imaginaryPart = -it.imaginaryPart / norm,
        )
    }
    override val numberDivideNumber: Divide<ComplexNumber<Number>, ComplexNumber<Number>, ComplexNumber<Number>> = Divide { left, right ->
        KoneContextHolder.unwrapLocallyAsExtensionReceivers(numberField)
        val norm = (right.realPart.let { it * it } + right.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = (left.realPart * right.realPart + left.imaginaryPart * right.imaginaryPart) / norm,
            imaginaryPart = (left.imaginaryPart * right.realPart - left.realPart * right.imaginaryPart) / norm,
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