/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.ExpressionPolynomial.Node.*
import space.kscience.kmath.expressions.Symbol

public class ExpressionPolynomial<C>
@PublishedApi internal constructor(@PublishedApi internal val head: Node<C>) : Polynomial<C> {
    @PublishedApi internal sealed interface Node<out C> {
        data class Value<out C>(val value: C): Node<C>
        data class Variable<out C>(val value: Symbol): Node<C>
        data class Plus<out C>(val left: Node<C>, val right: Node<C>): Node<C>
        data class Minus<out C>(val left: Node<C>, val right: Node<C>): Node<C>
        data class Times<out C>(val left: Node<C>, val right: Node<C>): Node<C>
    }

    private val Node<C>.stringRepresentation: String get() =
        when(this) {
            is Value -> "$value"
            is Variable -> value.identity
            is Plus -> "(${left.stringRepresentation} + ${right.stringRepresentation})"
            is Minus -> "(${left.stringRepresentation} - ${right.stringRepresentation})"
            is Times -> "(${left.stringRepresentation} * ${right.stringRepresentation})"
        }

    override fun toString(): String = "ExpressionPolynomial: ${head.stringRepresentation}"
}

public class ExpressionPolynomialSpace<C, out A: Ring<C>>(
    public override val ring: A,
) : MultivariatePolynomialSpace<C, Symbol, ExpressionPolynomial<C>>, PolynomialSpaceOverRing<C, ExpressionPolynomial<C>, A> {
    internal inline val Int.node: ExpressionPolynomial.Node<C> get() = Value(constantValue)
    internal inline val Long.node: ExpressionPolynomial.Node<C> get() = Value(constantValue)
    internal inline val C.node: ExpressionPolynomial.Node<C> get() = Value(this)
    internal inline val Symbol.node: ExpressionPolynomial.Node<C> get() = Variable(this)
    internal inline val ExpressionPolynomial<C>.node: ExpressionPolynomial.Node<C> get() = head

    override val zero: ExpressionPolynomial<C> = ExpressionPolynomial(ring.zero.node)
    override val one: ExpressionPolynomial<C> = ExpressionPolynomial(ring.one.node)

    public override infix fun ExpressionPolynomial<C>.equalsTo(other: ExpressionPolynomial<C>): Boolean = TODO()
    public override fun ExpressionPolynomial<C>.isZero(): Boolean = TODO()
    public override fun ExpressionPolynomial<C>.isOne(): Boolean = TODO()

    public override fun valueOf(value: C): ExpressionPolynomial<C> = ExpressionPolynomial(Value(value))

    public override operator fun Symbol.plus(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Symbol.minus(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Symbol.times(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Symbol.plus(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Symbol.minus(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Symbol.times(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Int.plus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Int.minus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Int.times(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Long.plus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Long.minus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Long.times(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun ExpressionPolynomial<C>.plus(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.minus(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.times(other: Int): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun ExpressionPolynomial<C>.plus(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.minus(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.times(other: Long): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Int.plus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Int.minus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Int.times(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Long.plus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Long.minus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Long.times(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Symbol.plus(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Symbol.minus(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Symbol.times(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun C.plus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun C.minus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun C.times(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    override operator fun ExpressionPolynomial<C>.plus(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    override operator fun ExpressionPolynomial<C>.minus(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    override operator fun ExpressionPolynomial<C>.times(other: C): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    override operator fun C.plus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    override operator fun C.minus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    override operator fun C.times(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Symbol.unaryPlus(): ExpressionPolynomial<C> =
        ExpressionPolynomial(this.node)
    public override operator fun Symbol.unaryMinus(): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(ring.zero.node, this.node)) // TODO: Introduce "UnaryMinus" node?
    public override operator fun Symbol.plus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Symbol.minus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Symbol.times(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun Symbol.plus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun Symbol.minus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun Symbol.times(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    public override operator fun ExpressionPolynomial<C>.plus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.minus(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    public override operator fun ExpressionPolynomial<C>.times(other: Symbol): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    override fun ExpressionPolynomial<C>.unaryMinus(): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(ring.zero.node, this.node)) // TODO: Introduce "UnaryMinus" node?
    override operator fun ExpressionPolynomial<C>.plus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Plus(this.node, other.node))
    override operator fun ExpressionPolynomial<C>.minus(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Minus(this.node, other.node))
    override operator fun ExpressionPolynomial<C>.times(other: ExpressionPolynomial<C>): ExpressionPolynomial<C> =
        ExpressionPolynomial(Times(this.node, other.node))

    override val ExpressionPolynomial<C>.degree: Int
        get() = TODO()
    public override val ExpressionPolynomial<C>.degrees: Map<Symbol, UInt>
        get() = TODO()
    public override fun ExpressionPolynomial<C>.degreeBy(variable: Symbol): UInt = TODO()
    public override fun ExpressionPolynomial<C>.degreeBy(variables: Collection<Symbol>): UInt = TODO()
    public override val ExpressionPolynomial<C>.variables: Set<Symbol>
        get() = TODO()
    public override val ExpressionPolynomial<C>.countOfVariables: Int get() = TODO()
}