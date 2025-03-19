/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.ExperimentalKoneAPI
import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.defaultHashing
import kotlin.jvm.JvmName


public val <C> Ring<C>.labeledPolynomialSpace: LabeledPolynomialSpace<C>
    get() = LabeledPolynomialSpace(this)

public val <C> Field<C>.labeledPolynomialSpace: LabeledPolynomialSpaceOverField<C>
    get() = LabeledPolynomialSpaceOverField(this)

public val <Number, NumberContext: Ring<Number>> NumberContext.labeledPolynomialSpaceScope: PolynomialSpaceScope<Number, LabeledPolynomial<Number>, NumberContext, LabeledPolynomialSpace<Number>>
    get() = PolynomialSpaceScope(numberContext = this, polynomialSpace = this.labeledPolynomialSpace)

public val <Number, NumberContext: Field<Number>> NumberContext.labeledPolynomialSpaceScope: PolynomialSpaceScope<Number, LabeledPolynomial<Number>, NumberContext, LabeledPolynomialSpaceOverField<Number>>
    get() = PolynomialSpaceScope(numberContext = this, polynomialSpace = this.labeledPolynomialSpace)

public val <C> LabeledPolynomialSpace<C>.labeledRationalFunctionSpace: LabeledRationalFunctionSpace<C>
    get() = LabeledRationalFunctionSpace(this)

public val <Number, NumberContext: Ring<Number>> NumberContext.labeledRationalFunctionSpaceScope: RationalFunctionSpaceScope<Number, LabeledPolynomial<Number>, LabeledRationalFunction<Number>, NumberContext, LabeledPolynomialSpace<Number>, LabeledRationalFunctionSpace<Number>>
    get() {
        val polynomialSpace = this.labeledPolynomialSpace
        return RationalFunctionSpaceScope(numberContext = this, polynomialSpace = polynomialSpace, rationalFunctionSpace = polynomialSpace.labeledRationalFunctionSpace)
    }

public val <Number, NumberContext: Field<Number>> NumberContext.labeledRationalFunctionSpaceScope: RationalFunctionSpaceScope<Number, LabeledPolynomial<Number>, LabeledRationalFunction<Number>, NumberContext, LabeledPolynomialSpaceOverField<Number>, LabeledRationalFunctionSpace<Number>>
    get() {
        val polynomialSpace = this.labeledPolynomialSpace
        return RationalFunctionSpaceScope(numberContext = this, polynomialSpace = polynomialSpace, rationalFunctionSpace = polynomialSpace.labeledRationalFunctionSpace)
    }

context(_: Ring<C>)
public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, C>): LabeledPolynomial<C> =
    if (coefficients.isEmpty()) this@substitute
    else LabeledPolynomial<C>(
        buildKoneReifiedMap {
            coefficients.entriesView.forEach { (degs, c) ->
                val newDegs = degs.filterKeysReified(keyHashing = defaultHashing()) { it !in args.keysView }
                val newC = args.entriesView.fold(c) { product, (variable, substitution) ->
                    val deg = degs.getOrDefault(variable, 0u)
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                setOrChange(newDegs, { newC }, { it + newC })
            }
        }
    )

context(_: Ring<C>)
public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, C>): LabeledPolynomial<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

// TODO: To optimize boxing
context(_: LabeledPolynomialSpace<C>)
@JvmName("substitutePolynomial")
public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
    coefficients.entriesView.fold(polynomialZero) { acc, (degs, c) ->
        val newDegs = degs.filterKeysReified(keyHashing = defaultHashing()) { it !in args.keysView }
        acc + args.entriesView.fold(LabeledPolynomial<C>(koneReifiedMapOf(newDegs mapsTo c, keyHashing = labeledMonomialSignatureHashing))) { product, (variable, substitution) ->
            val deg = degs.getOrDefault(variable, 0u)
            if (deg == 0u) product else product * power(substitution, deg)
        }
    }

context(_: LabeledPolynomialSpace<C>)
@JvmName("substitutePolynomial")
public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledPolynomial<C>>): LabeledPolynomial<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

// TODO: To optimize boxing
context(_: LabeledPolynomialSpace<C>, rationalFunctionSpace: LabeledRationalFunctionSpace<C>)
@JvmName("substituteRationalFunction")
public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
    coefficients.entriesView.fold(rationalFunctionZero) { acc, (degs, c) ->
        val newDegs = degs.filterKeysReified(keyHashing = defaultHashing()) { it !in args.keysView }
        acc + args.entriesView.fold(LabeledRationalFunction(LabeledPolynomial<C>(koneReifiedMapOf(newDegs mapsTo c, keyHashing = labeledMonomialSignatureHashing)))) { product, (variable, substitution) ->
            val deg = degs.getOrDefault(variable, 0u)
            if (deg == 0u) product else product * rationalFunctionSpace.power(substitution, deg)
        }
    }

context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
@JvmName("substituteRationalFunction")
public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

context(_: Ring<C>)
public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, C>): LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))

context(_: Ring<C>)
public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, C>): LabeledRationalFunction<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

// TODO: To optimize calculation
context(_: LabeledPolynomialSpace<C>)
@JvmName("substitutePolynomial")
public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, LabeledPolynomial<C>>) : LabeledRationalFunction<C> =
    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))

context(_: LabeledPolynomialSpace<C>)
@JvmName("substitutePolynomial")
public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledPolynomial<C>>): LabeledRationalFunction<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

// TODO: To optimize calculation
context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
@JvmName("substituteRationalFunction")
public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
        numerator.substitute(args) / denominator.substitute(args)

context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
@JvmName("substituteRationalFunction")
public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
    this.substitute(koneMapOf(entries = inputs, keyHashing = defaultHashing<LabeledVariable>()))

context(numberContext: Ring<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.derivativeWithRespectTo(variable: LabeledVariable): LabeledPolynomial<C> =
    LabeledPolynomial<C>(
        buildKoneReifiedMap(coefficients.keysView.count { it.getOrDefault(variable, 0u) >= 1u }) {
            coefficients.entriesView.forEach { (degs, c) ->
                if (variable !in degs.keysView) return@forEach
                set(
                    buildKoneReifiedMap {
                        degs.entriesView.forEach { (vari, deg) ->
                            when {
                                vari != variable -> set(vari, deg)
                                deg > 1u -> set(vari, deg - 1u)
                            }
                        }
                    },
                    c * degs[variable]
                )
            }
        }
    )

context(numberContext: Ring<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    variable: LabeledVariable,
    order: UInt
): LabeledPolynomial<C> =
    if (order == 0u) this@nthDerivativeWithRespectTo
    else LabeledPolynomial<C>(
        buildKoneReifiedMap(coefficients.keysView.count { it.getOrDefault(variable, 0u) >= order }) {
            coefficients.entriesView.forEach { (degs, c) ->
                if (degs.getOrDefault(variable, 0u) < order) return@forEach
                set(
                    buildKoneReifiedMap {
                        degs.entriesView.forEach { (vari, deg) ->
                            when {
                                vari != variable -> set(vari, deg)
                                deg > order -> set(vari, deg - order)
                            }
                        }
                    },
                    degs[variable].let { deg ->
                        (deg downTo deg - order + 1u).fold(c) { acc, ord -> (acc doublingTimes ord) }
                    }
                )
            }
        }
    )

context(numberContext: Ring<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    variablesAndOrders: Map<LabeledVariable, UInt>,
): LabeledPolynomial<C> {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    return LabeledPolynomial<C>(
        buildKoneReifiedMap(
            coefficients.keysView.count {
                variablesAndOrders.all { (variable, order) ->
                    it.getOrDefault(variable, 0u) >= order
                }
            }
        ) {
            coefficients.entriesView.forEach { (degs, c) ->
                if (filteredVariablesAndOrders.any { (variable, order) -> degs.getOrDefault(variable, 0u) < order }) return@forEach
                set(
                    buildKoneReifiedMap {
                        degs.entriesView.forEach { (vari, deg) ->
                            if (vari !in filteredVariablesAndOrders) set(vari, deg)
                            else {
                                val order = filteredVariablesAndOrders[vari]!!
                                if (deg > order) set(vari, deg - order)
                            }
                        }
                    },
                    filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
                        degs[index].let { deg ->
                            (deg downTo deg - order + 1u)
                                .fold(acc1) { acc2, ord -> (acc2 doublingTimes ord) }
                        }
                    }
                )
            }
        }
    )
}

context(numberContext: Field<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.antiderivativeWithRespectTo(
    variable: LabeledVariable,
): LabeledPolynomial<C> =
    LabeledPolynomial<C>(
        buildKoneReifiedMap(coefficients.size) {
            coefficients.entriesView.forEach { (degs, c) ->
                val newDegs = degs.withSetOrChangedReified(key = variable, keyHashing = defaultHashing(), valueOnSet = { 1u }, transformOnChange = { it + 1u }) // FIXME
                set(
                    newDegs,
                    c / (one doublingTimes newDegs[variable])
                )
            }
        }
    )

context(numberContext: Field<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    variable: LabeledVariable,
    order: UInt
): LabeledPolynomial<C> =
    if (order == 0u) this@nthAntiderivativeWithRespectTo
    else LabeledPolynomial<C>(
        buildKoneReifiedMap(coefficients.size) {
            coefficients.entriesView.forEach { (degs, c) ->
                val newDegs = degs.withSetOrChangedReified(key = variable, keyHashing = defaultHashing(), valueOnSet = { order }, transformOnChange = { it + order }) // FIXME
                set(
                    newDegs,
                    newDegs[variable].let { deg ->
                        (deg downTo  deg - order + 1u)
                            .fold(c) { acc, ord -> acc / (one doublingTimes ord) }
                    }
                )
            }
        }
    )

context(numberContext: Field<C>)
@ExperimentalKoneAPI
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    variablesAndOrders: KoneMap<LabeledVariable, UInt>,
): LabeledPolynomial<C> {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    return LabeledPolynomial<C>(
        buildKoneReifiedMap(coefficients.size) {
            coefficients.entriesView.forEach { (degs, c) ->
                val newDegs = mergeByReified(degs, filteredVariablesAndOrders, /*defaultReifiedHashing()*/) { _, deg, order -> deg + order } // FIXME
                set(
                    newDegs,
                    filteredVariablesAndOrders.entriesView.fold(c) { acc1, (index, order) ->
                        newDegs[index].let { deg ->
                            (deg downTo deg - order + 1u).fold(acc1) { acc2, ord -> acc2 / (one doublingTimes ord) }
                        }
                    }
                )
            }
        }
    )
}