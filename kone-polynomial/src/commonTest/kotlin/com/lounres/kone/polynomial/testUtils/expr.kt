/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial.testUtils

import com.lounres.kone.algebraic.Field


@JvmInline
value class StringExpr(val expr: String)

object StringExprRing : Field<StringExpr> {
    override val zero: StringExpr = StringExpr("0")
    override val one: StringExpr = StringExpr("1")
    override fun StringExpr.unaryMinus(): StringExpr = StringExpr("-${expr}")
    override fun StringExpr.plus(other: StringExpr): StringExpr = StringExpr("(${this.expr} + ${other.expr})")
    override fun StringExpr.minus(other: StringExpr): StringExpr = StringExpr("(${this.expr} - ${other.expr})")
    override fun StringExpr.times(other: StringExpr): StringExpr = StringExpr("(${this.expr} * ${other.expr})")
    override fun StringExpr.div(other: StringExpr): StringExpr = StringExpr("(${this.expr} / ${other.expr})")
    operator fun String.not() = StringExpr(this)
    operator fun Int.not() = StringExpr(toString())
}

sealed interface TreeExpr {
    data class Constant(val value: String): TreeExpr
    data class UnaryPlus(val value: TreeExpr): TreeExpr
    data class Negation(val value: TreeExpr): TreeExpr
    data class Sum(val left: TreeExpr, val right: TreeExpr): TreeExpr
    data class Difference(val left: TreeExpr, val right: TreeExpr): TreeExpr
    data class Product(val left: TreeExpr, val right: TreeExpr): TreeExpr
    data class Quoitient(val left: TreeExpr, val right: TreeExpr): TreeExpr
}

object TreeExprRing : Field<TreeExpr> {
    override val zero: TreeExpr = TreeExpr.Constant("0")
    override val one: TreeExpr = TreeExpr.Constant("1")
    override fun TreeExpr.unaryPlus(): TreeExpr = TreeExpr.UnaryPlus(this)
    override fun TreeExpr.unaryMinus(): TreeExpr = TreeExpr.Negation(this)
    override fun TreeExpr.plus(other: TreeExpr): TreeExpr = TreeExpr.Sum(this, other)
    override fun TreeExpr.minus(other: TreeExpr): TreeExpr = TreeExpr.Difference(this, other)
    override fun TreeExpr.times(other: TreeExpr): TreeExpr = TreeExpr.Product(this, other)
    override fun TreeExpr.div(other: TreeExpr): TreeExpr = TreeExpr.Quoitient(this, other)
    operator fun String.not() = TreeExpr.Constant(this)
    operator fun Int.not() = TreeExpr.Constant(toString())
}