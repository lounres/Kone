/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.defaultFor
import kotlin.jvm.JvmName


public val <C> CommutativeRing<C>.labeledPolynomialSpace: LabeledPolynomialSpace<C>
    get() = LabeledPolynomialSpace(this)

public val <C> Field<C>.labeledPolynomialSpace: LabeledPolynomialSpaceOverField<C>
    get() = LabeledPolynomialSpaceOverField(this)

//public val <C> LabeledPolynomialSpace<C>.labeledRationalFunctionSpace: LabeledRationalFunctionSpace<C>
//    get() = LabeledRationalFunctionSpace(this)

context(_: CommutativeRing<C>)
public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, C>): LabeledPolynomial<C> =
    if (coefficients.isEmpty()) this@substitute
    else LabeledPolynomial<C>(
        KoneReifiedMap.build {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                val newDegs = degs.filterKeysReified(keyHashing = Hashing.defaultFor()) { it !in args.keysView }
                val newC = args.nodesView.fold(c) { product, subEntry ->
                    val variable = subEntry.key
                    val substitution = subEntry.value
                    val deg = degs.getOrDefault(variable, 0u)
                    if (deg == 0u) product else product * power(substitution, deg)
                }
                setOrChange(newDegs, { newC }, { it + newC })
            }
        }
    )

context(_: CommutativeRing<C>)
public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, C>): LabeledPolynomial<C> =
    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))

// TODO: To optimize boxing
@JvmName("substitutePolynomial")
context(polynomialSpace: LabeledPolynomialSpace<C>)
public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, LabeledPolynomial<C>>) : LabeledPolynomial<C> =
    coefficients.nodesView.fold(polynomialSpace.zero) { acc, entry ->
        val degs = entry.key
        val c = entry.value
        val newDegs = degs.filterKeysReified(keyHashing = Hashing.defaultFor()) { it !in args.keysView }
        acc + args.nodesView.fold(LabeledPolynomial<C>(KoneReifiedMap.of(newDegs mapsTo c, keyHashing = labeledMonomialSignatureHashing))) { product, subEntry ->
            val variable = subEntry.key
            val substitution = subEntry.value
            val deg = degs.getOrDefault(variable, 0u)
            if (deg == 0u) product else product * power(substitution, deg)
        }
    }

@JvmName("substitutePolynomial")
context(_: LabeledPolynomialSpace<C>)
public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledPolynomial<C>>): LabeledPolynomial<C> =
    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))

//// TODO: To optimize boxing
//@JvmName("substituteRationalFunction")
//context(_: LabeledPolynomialSpace<C>, rationalFunctionSpace: LabeledRationalFunctionSpace<C>)
//public fun <C> LabeledPolynomial<C>.substitute(args: KoneMap<LabeledVariable, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
//    coefficients.nodesView.fold(rationalFunctionZero) { acc, entry ->
//        val degs = entry.key
//        val c = entry.value
//        val newDegs = degs.filterKeysReified(keyHashing = Hashing.defaultFor()) { it !in args.keysView }
//        acc + args.nodesView.fold(LabeledRationalFunction(LabeledPolynomial<C>(KoneReifiedMap.of(newDegs mapsTo c, keyHashing = labeledMonomialSignatureHashing)))) { product, subEntry ->
//            val variable = subEntry.key
//            val substitution = subEntry.value
//            val deg = degs.getOrDefault(variable, 0u)
//            if (deg == 0u) product else product * rationalFunctionSpace.power(substitution, deg)
//        }
//    }
//
//@JvmName("substituteRationalFunction")
//context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
//public fun <C> LabeledPolynomial<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
//    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))

//context(_: Ring<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, C>): LabeledRationalFunction<C> =
//    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))
//
//context(_: Ring<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, C>): LabeledRationalFunction<C> =
//    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))
//
//// TODO: To optimize calculation
//@JvmName("substitutePolynomial")
//context(_: LabeledPolynomialSpace<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, LabeledPolynomial<C>>) : LabeledRationalFunction<C> =
//    LabeledRationalFunction(numerator.substitute(args), denominator.substitute(args))
//
//@JvmName("substitutePolynomial")
//context(_: LabeledPolynomialSpace<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledPolynomial<C>>): LabeledRationalFunction<C> =
//    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))
//
//// TODO: To optimize calculation
//@JvmName("substituteRationalFunction")
//context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(args: KoneMap<LabeledVariable, LabeledRationalFunction<C>>) : LabeledRationalFunction<C> =
//        numerator.substitute(args) / denominator.substitute(args)
//
//@JvmName("substituteRationalFunction")
//context(_: LabeledPolynomialSpace<C>, _: LabeledRationalFunctionSpace<C>)
//public fun <C> LabeledRationalFunction<C>.substitute(vararg inputs: KoneMapEntry<LabeledVariable, LabeledRationalFunction<C>>): LabeledRationalFunction<C> =
//    this.substitute(KoneMap.of(entries = inputs, keyHashing = Hashing.defaultFor<LabeledVariable>()))

context(numberContext: Ring<C>)
public fun <C> LabeledPolynomial<C>.derivativeWithRespectTo(variable: LabeledVariable): LabeledPolynomial<C> =
    LabeledPolynomial<C>(
        KoneReifiedMap.build(coefficients.keysView.count { it.getOrDefault(variable, 0u) >= 1u }) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                if (variable !in degs.keysView) return@forEach
                set(
                    KoneReifiedMap.build {
                        degs.nodesView.forEach { subEntry ->
                            val vari = subEntry.key
                            val deg = subEntry.value
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
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    variable: LabeledVariable,
    order: UInt
): LabeledPolynomial<C> =
    if (order == 0u) this@nthDerivativeWithRespectTo
    else LabeledPolynomial<C>(
        KoneReifiedMap.build(coefficients.keysView.count { it.getOrDefault(variable, 0u) >= order }) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                if (degs.getOrDefault(variable, 0u) < order) return@forEach
                set(
                    KoneReifiedMap.build {
                        degs.nodesView.forEach { subEntry ->
                            val vari = subEntry.key
                            val deg = subEntry.value
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
public fun <C> LabeledPolynomial<C>.nthDerivativeWithRespectTo(
    variablesAndOrders: Map<LabeledVariable, UInt>,
): LabeledPolynomial<C> {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
    return LabeledPolynomial<C>(
        KoneReifiedMap.build(
            coefficients.keysView.count {
                variablesAndOrders.all { (variable, order) ->
                    it.getOrDefault(variable, 0u) >= order
                }
            }
        ) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                if (filteredVariablesAndOrders.any { (variable, order) -> degs.getOrDefault(variable, 0u) < order }) return@forEach
                set(
                    KoneReifiedMap.build {
                        degs.nodesView.forEach { subEntry ->
                            val vari = subEntry.key
                            val deg = subEntry.value
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
public fun <C> LabeledPolynomial<C>.antiderivativeWithRespectTo(
    variable: LabeledVariable,
): LabeledPolynomial<C> =
    LabeledPolynomial<C>(
        KoneReifiedMap.build(coefficients.size) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                val newDegs = degs.withSetOrChangedReified(key = variable, keyHashing = Hashing.defaultFor(), valueOnSet = { 1u }, transformOnChange = { it + 1u }) // FIXME
                set(
                    newDegs,
                    c / (numberContext.one doublingTimes newDegs[variable])
                )
            }
        }
    )

context(numberContext: Field<C>)
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    variable: LabeledVariable,
    order: UInt
): LabeledPolynomial<C> =
    if (order == 0u) this@nthAntiderivativeWithRespectTo
    else LabeledPolynomial<C>(
        KoneReifiedMap.build(coefficients.size) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                val newDegs = degs.withSetOrChangedReified(key = variable, keyHashing = Hashing.defaultFor(), valueOnSet = { order }, transformOnChange = { it + order }) // FIXME
                set(
                    newDegs,
                    newDegs[variable].let { deg ->
                        (deg downTo  deg - order + 1u)
                            .fold(c) { acc, ord -> acc / (numberContext.one doublingTimes ord) }
                    }
                )
            }
        }
    )

context(numberContext: Field<C>)
public fun <C> LabeledPolynomial<C>.nthAntiderivativeWithRespectTo(
    variablesAndOrders: KoneMap<LabeledVariable, UInt>,
): LabeledPolynomial<C> {
    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
    return LabeledPolynomial<C>(
        KoneReifiedMap.build(coefficients.size) {
            coefficients.nodesView.forEach { entry ->
                val degs = entry.key
                val c = entry.value
                val newDegs = mergeByReified(degs, filteredVariablesAndOrders, /*defaultReifiedHashing()*/) { _, deg, order -> deg + order } // FIXME
                set(
                    newDegs,
                    filteredVariablesAndOrders.nodesView.fold(c) { acc1, subEntry ->
                        val index = subEntry.key
                        val order = subEntry.value
                        newDegs[index].let { deg ->
                            (deg downTo deg - order + 1u).fold(acc1) { acc2, ord -> acc2 / (numberContext.one doublingTimes ord) }
                        }
                    }
                )
            }
        }
    )
}