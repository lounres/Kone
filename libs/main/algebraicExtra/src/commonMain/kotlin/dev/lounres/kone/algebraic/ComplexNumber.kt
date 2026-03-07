package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.algebraic.algorithms.positiveSquareRoot
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.serialization.Serializable
import kotlin.reflect.KVariance.OUT


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
    override fun ComplexNumber<Number>.isZero(): Boolean = numberField { this.realPart.isZero() && this.imaginaryPart.isZero() }
    override fun ComplexNumber<Number>.isOne(): Boolean = numberField { this.realPart.isOne() && this.imaginaryPart.isZero() }
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
    
    // region ComplexNumber-UInt operations
    override fun ComplexNumber<Number>.plus(other: UInt): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.minus(other: UInt): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.times(other: UInt): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other },
            imaginaryPart = numberField { imaginaryPart * other }
        )
    override fun ComplexNumber<Number>.div(other: UInt): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart / other },
            imaginaryPart = numberField { imaginaryPart / other }
        )
    // endregion
    
    // region ComplexNumber-Int operations
    override fun ComplexNumber<Number>.plus(other: Int): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.minus(other: Int): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.times(other: Int): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other },
            imaginaryPart = numberField { imaginaryPart * other }
        )
    override fun ComplexNumber<Number>.div(other: Int): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart / other },
            imaginaryPart = numberField { imaginaryPart / other }
        )
    // endregion
    
    // region ComplexNumber-ULong operations
    override fun ComplexNumber<Number>.plus(other: ULong): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.minus(other: ULong): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.times(other: ULong): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other },
            imaginaryPart = numberField { imaginaryPart * other }
        )
    override fun ComplexNumber<Number>.div(other: ULong): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart / other },
            imaginaryPart = numberField { imaginaryPart / other }
        )
    // endregion
    
    // region ComplexNumber-Long operations
    override fun ComplexNumber<Number>.plus(other: Long): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.minus(other: Long): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.times(other: Long): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other },
            imaginaryPart = numberField { imaginaryPart * other }
        )
    override fun ComplexNumber<Number>.div(other: Long): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart / other },
            imaginaryPart = numberField { imaginaryPart / other }
        )
    // endregion
    
    // region ComplexNumber-Number operations
    override fun ComplexNumber<Number>.plus(other: Number): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.minus(other: Number): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other },
            imaginaryPart = imaginaryPart
        )
    override fun ComplexNumber<Number>.times(other: Number): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other },
            imaginaryPart = numberField { imaginaryPart * other }
        )
    override fun ComplexNumber<Number>.div(other: Number): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart / other },
            imaginaryPart = numberField { imaginaryPart / other }
        )
    // endregion
    
    // region UInt-ComplexNumber operations
    override fun UInt.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this + other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun UInt.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this - other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun UInt.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this * other.realPart },
            imaginaryPart = numberField { this * other.imaginaryPart }
        )
    override fun UInt.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
        val commonMultiplier = this / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Int-ComplexNumber operations
    override fun Int.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this + other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Int.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this - other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Int.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this * other.realPart },
            imaginaryPart = numberField { this * other.imaginaryPart }
        )
    override fun Int.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
        val commonMultiplier = this / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ULong-ComplexNumber operations
    override fun ULong.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this + other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun ULong.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this - other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun ULong.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this * other.realPart },
            imaginaryPart = numberField { this * other.imaginaryPart }
        )
    override fun ULong.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
        val commonMultiplier = this / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Long-ComplexNumber operations
    override fun Long.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this + other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Long.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this - other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Long.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this * other.realPart },
            imaginaryPart = numberField { this * other.imaginaryPart }
        )
    override fun Long.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
        val commonMultiplier = this / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region Long-ComplexNumber operations
    override fun Number.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this + other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Number.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this - other.realPart },
            imaginaryPart = other.imaginaryPart
        )
    override fun Number.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { this * other.realPart },
            imaginaryPart = numberField { this * other.imaginaryPart }
        )
    override fun Number.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
        val commonMultiplier = this / (other.realPart.let { it * it } + other.imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = other.realPart * commonMultiplier,
            imaginaryPart = -other.imaginaryPart * commonMultiplier
        )
    }
    // endregion
    
    // region ComplexNumber-ComplexNumber operations
    override fun ComplexNumber<Number>.unaryMinus(): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { -realPart },
            imaginaryPart = numberField { -imaginaryPart },
        )
    override fun ComplexNumber<Number>.plus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart + other.realPart },
            imaginaryPart = numberField { imaginaryPart + other.imaginaryPart },
        )
    override fun ComplexNumber<Number>.minus(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart - other.realPart },
            imaginaryPart = numberField { imaginaryPart - other.imaginaryPart },
        )
    override fun ComplexNumber<Number>.times(other: ComplexNumber<Number>): ComplexNumber<Number> =
        ComplexNumber(
            realPart = numberField { realPart * other.realPart - imaginaryPart * other.imaginaryPart },
            imaginaryPart = numberField { realPart * other.imaginaryPart + imaginaryPart * other.realPart },
        )
    override fun ComplexNumber<Number>.reciprocal(): ComplexNumber<Number> = numberField {
        val norm = (realPart.let { it * it } + imaginaryPart.let { it * it })
        ComplexNumber(
            realPart = realPart / norm,
            imaginaryPart = imaginaryPart / norm,
        )
    }
    override fun ComplexNumber<Number>.div(other: ComplexNumber<Number>): ComplexNumber<Number> = numberField {
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

context(koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ComplexNumber.Companion.fieldExtensionOver(numberType: SuppliedType): FieldExtension<Number, ComplexNumber<Number>> {
    val koneContextRegistry = koneContextRegistry.get()
    return fieldExtensionOver(
        numberField = koneContextRegistry[Field.Key<Number>(numberType = numberType)],
    )
}

context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number> ComplexNumber.Companion.setFieldExtensionOver(numberType: SuppliedType) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val complexNumberType = SuppliedType.Regular(
        fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
        typeArguments = listOf(
            SuppliedProjection.Regular(
                variance = OUT,
                type = numberType,
            )
        ),
        isNullable = false,
    )
    FieldExtension.Key<Number, ComplexNumber<Number>>(numberType = numberType, vectorType = complexNumberType).withImpliedUsingFirst correspondsTo RegisteredValueProvider.cached {
        fieldExtensionOver(
            numberType = numberType,
        )
    }
}

context(_: CommutativeRing<Number>)
public fun <Number> ComplexNumber<Number>.conjugate(): ComplexNumber<Number> =
    ComplexNumber(
        realPart = realPart,
        imaginaryPart = -imaginaryPart,
    )
context(_: CommutativeRing<Number>)
public fun <Number> ComplexNumber<Number>.norm(): Number = realPart * realPart + imaginaryPart * imaginaryPart
context(_: CommutativeRing<Number>, _: PositiveSquareRootComputer<Number>)
public fun <Number> ComplexNumber<Number>.absoluteValue(): Number = norm().positiveSquareRoot()