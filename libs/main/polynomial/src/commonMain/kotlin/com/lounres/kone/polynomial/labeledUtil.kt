/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.*
import com.lounres.kone.annotations.ExperimentalKoneAPI
import com.lounres.kone.computationalContext.invoke
import com.lounres.kone.util.mapOperations.mergeBy
import com.lounres.kone.util.mapOperations.putOrChange
import com.lounres.kone.util.mapOperations.withPutOrChanged
import space.kscience.kmath.expressions.Symbol
import kotlin.jvm.JvmName


public inline val <C, A : Ring<C>> A.labeledPolynomialSpace: LabeledPolynomialSpace<C, A>
    get() = LabeledPolynomialSpace(this)

public inline val <C, A : Field<C>> A.labeledPolynomialSpace: LabeledPolynomialSpaceOverField<C, A>
    get() = LabeledPolynomialSpaceOverField(this)

public inline val <C, A : Ring<C>> A.labeledRationalFunctionSpace: DefaultLabeledRationalFunctionSpace<C, A>
    get() = LabeledRationalFunctionSpace(this.labeledPolynomialSpace)

public inline val <C, A : Field<C>> A.labeledRationalFunctionSpace: DefaultLabeledRationalFunctionSpaceOverField<C, A>
    get() = LabeledRationalFunctionSpaceOverField(this.labeledPolynomialSpace)

public fun LabeledPolynomial<Double>.substitute(args: Map<Symbol, Double>): LabeledPolynomial<Double> = Double.field {
    if (coefficients.isEmpty()) return this@substitute
    LabeledPolynomial<Double>(
        buildMap {
            coefficients.forEach { (degs, c) ->
                val newDegs = degs.filterKeys { it !in args }
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                putOrChange(newDegs, newC) { _, left, right -> left + right }
            }
        }
    )
}

@Suppress("NOTHING_TO_INLINE")
public inline fun LabeledPolynomial<Double>.substitute(vararg inputs: Pair<Symbol, Double>): LabeledPolynomial<Double> =
    this.substitute(mapOf(*inputs))

public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, C>): LabeledPolynomial<C> = ring {
    if (coefficients.isEmpty()) return this@substitute
    LabeledPolynomial<C>(
        buildMap {
            coefficients.forEach { (degs, c) ->
                val newDegs = degs.filterKeys { it !in args }
                val newC = args.entries.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrElse(variable) { 0u }
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                putOrChange(newDegs, newC) { _, left, right -> left + right }
            }
        }
    )
}

@Suppress("NOTHING_TO_INLINE")
public inline fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, C>): LabeledPolynomial<C> =
    this.substitute(ring, mapOf(*inputs))

 // TODO: To optimize boxing
@JvmName("substitutePolynomial")
public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
    ring.labeledPolynomialSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.filterKeys { it !in args }
            acc + args.entries.fold(LabeledPolynomial<C>(mapOf(newDegs to c))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

@Suppress("NOTHING_TO_INLINE")
@JvmName("substitutePolynomial")
public inline fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, LabeledPolynomial<C>>): LabeledPolynomial<C> =
    this.substitute(ring, mapOf(*inputs))

 // TODO: To optimize boxing
@JvmName("substituteRationalFunction")
public fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
    ring.labeledRationalFunctionSpace {
        coefficients.entries.fold(zero) { acc, (degs, c) ->
            val newDegs = degs.filterKeys { it !in args }
            acc + args.entries.fold(LabeledRationalFunction(LabeledPolynomial<C>(mapOf(newDegs to c)))) { product, (variable, substitution) ->
                val deg = degs.getOrElse(variable) { 0u }
                if (deg == 0u) product else product * power(substitution, deg)
            }
        }
    }

@Suppress("NOTHING_TO_INLINE")
@JvmName("substituteRationalFunction")
public inline fun <C> LabeledPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
    this.substitute(ring, mapOf(*inputs))

public fun LabeledRationalFunction<Double>.substitute(args: Map<Symbol, Double>): LabeledRationalFunction<Double> =
    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))

@Suppress("NOTHING_TO_INLINE")
public inline fun LabeledRationalFunction<Double>.substitute(vararg inputs: Pair<Symbol, Double>): LabeledRationalFunction<Double> =
    this.substitute(mapOf(*inputs))

public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

@Suppress("NOTHING_TO_INLINE")
public inline fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, C>): LabeledRationalFunction<C> =
    this.substitute(ring, mapOf(*inputs))

 // TODO: To optimize calculation
@JvmName("substitutePolynomial")
public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledPolynomial<C>>) : LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))

@Suppress("NOTHING_TO_INLINE")
@JvmName("substitutePolynomial")
public inline fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, LabeledPolynomial<C>>): LabeledRationalFunction<C> =
    this.substitute(ring, mapOf(*inputs))

 // TODO: To optimize calculation
@JvmName("substituteRationalFunction")
public fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Symbol, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
    ring.labeledRationalFunctionSpace {
        numerator.substitute(ring, args) / denominator.substitute(ring, args)
    }

@Suppress("NOTHING_TO_INLINE")
@JvmName("substituteRationalFunction")
public inline fun <C> LabeledRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
    this.substitute(ring, mapOf(*inputs))

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.derivativeWithRespectTo(
    algebra: Ring<C>,
    variable: Symbol,
): LabeledPolynomial<C> = algebra {
    LabeledPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= 1u }) {
            coefficients
                .forEach { (degs, c) ->
                    if (variable !in degs) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                when {
                                    vari != variable -> put(vari, deg)
                                    deg > 1u -> put(vari, deg - 1u)
                                }
                            }
                        },
                        multiplyByDoubling(c, degs[variable]!!)
                    )
                }
        }
    )
}

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: Ring<C>,
    variable: Symbol,
    order: UInt
): LabeledPolynomial<C> = algebra {
    if (order == 0u) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= order }) {
            coefficients
                .forEach { (degs, c) ->
                    if (degs.getOrElse(variable) { 0u } < order) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                when {
                                    vari != variable -> put(vari, deg)
                                    deg > order -> put(vari, deg - order)
                                }
                            }
                        },
                        degs[variable]!!.let { deg ->
                            (deg downTo deg - order + 1u)
                                .fold(c) { acc, ord -> multiplyByDoubling(acc, ord) }
                        }
                    )
                }
        }
    )
}

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    algebra: Ring<C>,
    variablesAndOrders: Map<Symbol, UInt>,
): LabeledPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(
            coefficients.count {
                variablesAndOrders.all { (variable, order) ->
                    it.key.getOrElse(variable) { 0u } >= order
                }
            }
        ) {
            coefficients
                .forEach { (degs, c) ->
                    if (filteredVariablesAndOrders.any { (variable, order) -> degs.getOrElse(variable) { 0u } < order }) return@forEach
                    put(
                        buildMap {
                            degs.forEach { (vari, deg) ->
                                if (vari !in filteredVariablesAndOrders) put(vari, deg)
                                else {
                                    val order = filteredVariablesAndOrders[vari]!!
                                    if (deg > order) put(vari, deg - order)
                                }
                            }
                        },
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            degs[index]!!.let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> multiplyByDoubling(acc2, ord) }
                            }
                        }
                    )
                }
        }
    )
}

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.antiderivativeWithRespectTo(
    algebra: Field<C>,
    variable: Symbol,
): LabeledPolynomial<C> = algebra {
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = degs.withPutOrChanged(variable, 1u) { _, it, _ -> it + 1u }
                    put(
                        newDegs,
                        c / multiplyByDoubling(one, newDegs[variable]!!)
                    )
                }
        }
    )
}

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: Field<C>,
    variable: Symbol,
    order: UInt
): LabeledPolynomial<C> = algebra {
    if (order == 0u) return this@nthAntiderivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = degs.withPutOrChanged(variable, order) { _, it, _ -> it + order }
                    put(
                        newDegs,
                        newDegs[variable]!!.let { deg ->
                            (deg downTo  deg - order + 1u)
                                .fold(c) { acc, ord -> acc / multiplyByDoubling(one, ord) }
                        }
                    )
                }
        }
    )
}

@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    algebra: Field<C>,
    variablesAndOrders: Map<Symbol, UInt>,
): LabeledPolynomial<C> = algebra {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    LabeledPolynomial<C>(
        buildMap(coefficients.size) {
            coefficients
                .forEach { (degs, c) ->
                    val newDegs = mergeBy(degs, filteredVariablesAndOrders) { _, deg, order -> deg + order }
                    put(
                        newDegs,
                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                            newDegs[index]!!.let { deg ->
                                (deg downTo deg - order + 1u)
                                    .fold(acc1) { acc2, ord -> acc2 / multiplyByDoubling(one, ord) }
                            }
                        }
                    )
                }
        }
    )
}