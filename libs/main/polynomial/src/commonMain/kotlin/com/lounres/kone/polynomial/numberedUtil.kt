///*
// * Copyright © 2023 Gleb Minaev
// * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
// */
//
//package com.lounres.kone.polynomial
//
//import com.lounres.kone.algebraic.Field
//import com.lounres.kone.algebraic.Ring
//import com.lounres.kone.algebraic.field
//import com.lounres.kone.algebraic.util.doublingTimes
//import com.lounres.kone.annotations.ExperimentalKoneAPI
//import com.lounres.kone.context.invoke
//import com.lounres.kone.util.mapOperations.putOrChange
//import space.kscience.kmath.structures.Buffer
//import kotlin.jvm.JvmName
//import kotlin.math.max
//import kotlin.math.min
//
//
//public inline val <C, A : Ring<C>> A.numberedPolynomialSpace: NumberedPolynomialSpace<C, A>
//    get() = NumberedPolynomialSpace(this)
//
//public inline val <C, A : Field<C>> A.numberedPolynomialSpace: NumberedPolynomialSpaceOverField<C, A>
//    get() = NumberedPolynomialSpaceOverField(this)
//
//public inline val <C, A : Ring<C>> A.numberedRationalFunctionSpace: DefaultNumberedRationalFunctionSpace<C, A>
//    get() = NumberedRationalFunctionSpace(this.numberedPolynomialSpace)
//
//public inline val <C, A : Field<C>> A.numberedRationalFunctionSpace: DefaultNumberedRationalFunctionSpaceOverField<C, A>
//    get() = NumberedRationalFunctionSpaceOverField(this.numberedPolynomialSpace)
//
//public fun NumberedPolynomial<Double>.substitute(args: Map<Int, Double>): NumberedPolynomial<Double> = Double.field {
//    NumberedPolynomial<Double>(
//        buildMap(coefficients.size) {
//            for ((degs, c) in coefficients) {
//                val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
//                val newC = args.entries.fold(c) { product, (variable, substitution) ->
//                    val deg = degs.getOrElse(variable) { 0u }
//                    if (deg == 0u) product else product * substitution.pow(deg.toInt())
//                }
//                putOrChange(newDegs, newC) { _, it, _ -> it + newC }
//            }
//        }
//    )
//}
//
//@Suppress("NOTHING_TO_INLINE")
//public inline fun NumberedPolynomial<Double>.substitute(vararg inputs: Pair<Int, Double>): NumberedPolynomial<Double> =
//    this.substitute(mapOf(*inputs))
//
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedPolynomial<C> = ring {
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            for ((degs, c) in coefficients) {
//                val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
//                val newC = args.entries.fold(c) { product, (variable, substitution) ->
//                    val deg = degs.getOrElse(variable) { 0u }
//                    if (deg == 0u) product else product * power(substitution, deg)
//                }
//                putOrChange(newDegs, newC) { _, it, _ -> it + newC }
//            }
//        }
//    )
//}
//
//@Suppress("NOTHING_TO_INLINE")
//public inline fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, C>): NumberedPolynomial<C> =
//    this.substitute(ring, mapOf(*inputs))
//
// // TODO: To optimize boxing
//@JvmName("substitutePolynomial")
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> =
//    ring.numberedPolynomialSpace {
//        coefficients.entries.fold(zero) { acc, (degs, c) ->
//            val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
//            acc + args.entries.fold(NumberedPolynomial<C>(mapOf(newDegs to c))) { product, (variable, substitution) ->
//                val deg = degs.getOrElse(variable) { 0u }
//                if (deg == 0u) product else product * power(substitution, deg)
//            }
//        }
//    }
//
//@Suppress("NOTHING_TO_INLINE")
//@JvmName("substitutePolynomial")
//public inline fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> =
//    this.substitute(ring, mapOf(*inputs))
//
// // TODO: To optimize boxing
//@JvmName("substituteRationalFunction")
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
//    ring.numberedRationalFunctionSpace {
//        coefficients.entries.fold(zero) { acc, (degs, c) ->
//            val newDegs = degs.mapIndexed { index, deg -> if (index !in args) deg else 0u }.cleanUp()
//            acc + args.entries.fold(NumberedRationalFunction(NumberedPolynomial<C>(mapOf(newDegs to c)))) { product, (variable, substitution) ->
//                val deg = degs.getOrElse(variable) { 0u }
//                if (deg == 0u) product else product * power(substitution, deg)
//            }
//        }
//    }
//
//@Suppress("NOTHING_TO_INLINE")
//@JvmName("substituteRationalFunction")
//public inline fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> =
//    this.substitute(ring, mapOf(*inputs))
//
//public fun NumberedRationalFunction<Double>.substitute(args: Map<Int, Double>): NumberedRationalFunction<Double> =
//    NumberedRationalFunction(numerator.substitute(args), denominator.substitute(args))
//
//@Suppress("NOTHING_TO_INLINE")
//public inline fun NumberedRationalFunction<Double>.substitute(vararg inputs: Pair<Int, Double>): NumberedRationalFunction<Double> =
//    this.substitute(mapOf(*inputs))
//
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, C>): NumberedRationalFunction<C> =
//    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))
//
//@Suppress("NOTHING_TO_INLINE")
//public inline fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, C>): NumberedRationalFunction<C> =
//    this.substitute(ring, mapOf(*inputs))
//
// // TODO: To optimize calculation
//@JvmName("substitutePolynomial")
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedPolynomial<C>>) : NumberedRationalFunction<C> =
//    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))
//
//@Suppress("NOTHING_TO_INLINE")
//@JvmName("substitutePolynomial")
//public inline fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, NumberedPolynomial<C>>): NumberedRationalFunction<C> =
//    this.substitute(ring, mapOf(*inputs))
//
// // TODO: To optimize calculation
//@JvmName("substituteRationalFunction")
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Map<Int, NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
//    ring.numberedRationalFunctionSpace {
//        numerator.substitute(ring, args) / denominator.substitute(ring, args)
//    }
//
//@Suppress("NOTHING_TO_INLINE")
//@JvmName("substituteRationalFunction")
//public inline fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, vararg inputs: Pair<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> =
//    this.substitute(ring, mapOf(*inputs))
//
//public fun NumberedPolynomial<Double>.substitute(args: Buffer<Double>): NumberedPolynomial<Double> = Double.field {
//    val lastSubstitutionVariable = args.size - 1
//    NumberedPolynomial<Double>(
//        buildMap(coefficients.size) {
//            for ((degs, c) in coefficients) {
//                val lastDegsIndex = degs.lastIndex
//                val newDegs =
//                    if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
//                    else degs.toMutableList().apply {
//                        for (i in 0..lastSubstitutionVariable) this[i] = 0u
//                    }
//                val newC = (0..min(lastDegsIndex, lastSubstitutionVariable)).fold(c) { product, variable ->
//                    val deg = degs[variable]
//                    if (deg == 0u) product else product * args[variable].pow(deg.toInt())
//                }
//                putOrChange(newDegs, newC) { _, it, _ -> it + newC }
//            }
//        }
//    )
//}
//
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<C>): NumberedPolynomial<C> = ring {
//    val lastSubstitutionVariable = args.size - 1
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            for ((degs, c) in coefficients) {
//                val lastDegsIndex = degs.lastIndex
//                val newDegs =
//                    if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
//                    else degs.toMutableList().apply {
//                        for (i in 0..lastSubstitutionVariable) this[i] = 0u
//                    }
//                val newC = (0..min(lastDegsIndex, lastSubstitutionVariable)).fold(c) { product, variable ->
//                    val deg = degs[variable]
//                    if (deg == 0u) product else product * power(args[variable], deg)
//                }
//                putOrChange(newDegs, newC) { _, it, _ -> it + newC }
//            }
//        }
//    )
//}
// // TODO: To optimize boxing
//@JvmName("substitutePolynomial")
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<NumberedPolynomial<C>>) : NumberedPolynomial<C> =
//    ring.numberedPolynomialSpace {
//        val lastSubstitutionVariable = args.size - 1
//        coefficients.entries.fold(zero) { acc, (degs, c) ->
//            val lastDegsIndex = degs.lastIndex
//            val newDegs =
//                if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
//                else degs.toMutableList().apply {
//                    for (i in 0..lastSubstitutionVariable) this[i] = 0u
//                }
//            acc + (0..min(lastDegsIndex, lastSubstitutionVariable))
//                .fold(NumberedPolynomial<C>(mapOf(newDegs to c))) { product, variable ->
//                    val deg = degs[variable]
//                    if (deg == 0u) product else product * power(args[variable], deg)
//                }
//        }
//    }
// // TODO: To optimize boxing
//@JvmName("substituteRationalFunction")
//public fun <C> NumberedPolynomial<C>.substitute(ring: Ring<C>, args: Buffer<NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
//    ring.numberedRationalFunctionSpace {
//        val lastSubstitutionVariable = args.size - 1
//        coefficients.entries.fold(zero) { acc, (degs, c) ->
//            val lastDegsIndex = degs.lastIndex
//            val newDegs =
//                if (lastDegsIndex <= lastSubstitutionVariable) emptyList()
//                else degs.toMutableList().apply {
//                    for (i in 0..lastSubstitutionVariable) this[i] = 0u
//                }
//            acc + (0..min(lastDegsIndex, lastSubstitutionVariable))
//                .fold(NumberedRationalFunction(NumberedPolynomial<C>(mapOf(newDegs to c)))) { product, variable ->
//                    val deg = degs[variable]
//                    if (deg == 0u) product else product * power(args[variable], deg)
//                }
//        }
//    }
//
//public fun NumberedRationalFunction<Double>.substitute(args: Buffer<Double>): NumberedRationalFunction<Double> =
//    NumberedRationalFunction(numerator.substitute(args), denominator.substitute(args))
//
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<C>): NumberedRationalFunction<C> =
//    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))
// // TODO: To optimize calculation
//@JvmName("substitutePolynomial")
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<NumberedPolynomial<C>>) : NumberedRationalFunction<C> =
//    NumberedRationalFunction(numerator.substitute(ring, args), denominator.substitute(ring, args))
// // TODO: To optimize calculation
//@JvmName("substituteRationalFunction")
//public fun <C> NumberedRationalFunction<C>.substitute(ring: Ring<C>, args: Buffer<NumberedRationalFunction<C>>) : NumberedRationalFunction<C> =
//    ring.numberedRationalFunctionSpace {
//        numerator.substitute(ring, args) / denominator.substitute(ring, args)
//    }
//
//internal const val fullSubstitutionExceptionMessage: String = "Fully substituting buffer should cover all variables of the polynomial."
//
//public fun NumberedPolynomial<Double>.substituteFully(args: Buffer<Double>): Double = Double.field {
//    val lastSubstitutionVariable = args.size - 1
//    require(coefficients.keys.all { it.lastIndex <= lastSubstitutionVariable }) { fullSubstitutionExceptionMessage }
//    coefficients.entries.fold(.0) { acc, (degs, c) ->
//        acc + degs.foldIndexed(c) { variable, product, deg ->
//            if (deg == 0u) product else product * args[variable].pow(deg.toInt())
//        }
//    }
//}
//
//public fun <C> NumberedPolynomial<C>.substituteFully(ring: Ring<C>, args: Buffer<C>): C = ring {
//    val lastSubstitutionVariable = args.size - 1
//    require(coefficients.keys.all { it.lastIndex <= lastSubstitutionVariable }) { fullSubstitutionExceptionMessage }
//    coefficients.entries.fold(zero) { acc, (degs, c) ->
//        acc + degs.foldIndexed(c) { variable, product, deg ->
//            if (deg == 0u) product else product * power(args[variable], deg)
//        }
//    }
//}
//
//public fun NumberedRationalFunction<Double>.substituteFully(args: Buffer<Double>): Double =
//    numerator.substituteFully(args) / denominator.substituteFully(args)
//
//public fun <C> NumberedRationalFunction<C>.substituteFully(ring: Field<C>, args: Buffer<C>): C = ring {
//    numerator.substituteFully(ring, args) / denominator.substituteFully(ring, args)
//}
//
//public fun <C> NumberedPolynomial<C>.asFunctionOver(ring: Ring<C>): (Buffer<C>) -> C = { substituteFully(ring, it) }
//
//public fun <C> NumberedPolynomial<C>.asFunctionOfConstantOver(ring: Ring<C>): (Buffer<C>) -> C = { substituteFully(ring, it) }
//
//public fun <C> NumberedPolynomial<C>.asFunctionOfPolynomialOver(ring: Ring<C>): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = { substitute(ring, it) }
//
//public fun <C> NumberedPolynomial<C>.asFunctionOfRationalFunctionOver(ring: Ring<C>): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }
//
//public fun <C> NumberedRationalFunction<C>.asFunctionOver(ring: Field<C>): (Buffer<C>) -> C = { substituteFully(ring, it) }
//
//public fun <C> NumberedRationalFunction<C>.asFunctionOfConstantOver(ring: Field<C>): (Buffer<C>) -> C = { substituteFully(ring, it) }
//
//public fun <C> NumberedRationalFunction<C>.asFunctionOfPolynomialOver(ring: Ring<C>): (Buffer<NumberedPolynomial<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }
//
//public fun <C> NumberedRationalFunction<C>.asFunctionOfRationalFunctionOver(ring: Ring<C>): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = { substitute(ring, it) }
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.derivativeWithRespectTo(
//    ring: Ring<C>,
//    variable: Int,
//): NumberedPolynomial<C> = ring {
//    NumberedPolynomial<C>(
//        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= 1u }) {
//            coefficients
//                .forEach { (degs, c) ->
//                    if (degs.lastIndex < variable) return@forEach
//                    put(
//                        degs.mapIndexed { index, deg ->
//                            when {
//                                index != variable -> deg
//                                deg > 0u -> deg - 1u
//                                else -> return@forEach
//                            }
//                        }.cleanUp(),
//                        doublingTimes(c, degs[variable])
//                    )
//                }
//        }
//    )
//}
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
//    ring: Ring<C>,
//    variable: Int,
//    order: UInt
//): NumberedPolynomial<C> = ring {
//    if (order == 0u) return this@nthDerivativeWithRespectTo
//    NumberedPolynomial<C>(
//        buildMap(coefficients.count { it.key.getOrElse(variable) { 0u } >= order }) {
//            coefficients
//                .forEach { (degs, c) ->
//                    if (degs.lastIndex < variable) return@forEach
//                    put(
//                        degs.mapIndexed { index, deg ->
//                            when {
//                                index != variable -> deg
//                                deg >= order -> deg - order
//                                else -> return@forEach
//                            }
//                        }.cleanUp(),
//                        degs[variable].let { deg ->
//                            (deg downTo deg - order + 1u)
//                                .fold(c) { acc, ord -> doublingTimes(acc, ord) }
//                        }
//                    )
//                }
//        }
//    )
//}
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.nthDerivativeWithRespectTo(
//    ring: Ring<C>,
//    variablesAndOrders: Map<Int, UInt>,
//): NumberedPolynomial<C> = ring {
//    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
//    if (filteredVariablesAndOrders.isEmpty()) return this@nthDerivativeWithRespectTo
//    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            coefficients
//                .forEach { (degs, c) ->
//                    if (degs.lastIndex < maxRespectedVariable) return@forEach
//                    put(
//                        degs.mapIndexed { index, deg ->
//                            if (index !in filteredVariablesAndOrders) return@mapIndexed deg
//                            val order = filteredVariablesAndOrders[index]!!
//                            if (deg >= order) deg - order else return@forEach
//                        }.cleanUp(),
//                        filteredVariablesAndOrders.entries.fold(c) { acc1, (index, order) ->
//                            degs[index].let { deg ->
//                                (deg downTo deg - order + 1u)
//                                    .fold(acc1) { acc2, ord -> doublingTimes(acc2, ord) }
//                            }
//                        }
//                    )
//                }
//        }
//    )
//}
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.antiderivativeWithRespectTo(
//    ring: Field<C>,
//    variable: Int,
//): NumberedPolynomial<C> = ring {
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            coefficients
//                .forEach { (degs, c) ->
//                    put(
//                        List(max(variable + 1, degs.size)) { degs.getOrElse(it) { 0u } + if (it != variable) 0u else 1u },
//                        c / doublingTimes(one, degs.getOrElse(variable) { 0u } + 1u)
//                    )
//                }
//        }
//    )
//}
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
//    ring: Field<C>,
//    variable: Int,
//    order: UInt
//): NumberedPolynomial<C> = ring {
//    if (order == 0u) return this@nthAntiderivativeWithRespectTo
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            coefficients
//                .forEach { (degs, c) ->
//                    put(
//                        List(max(variable + 1, degs.size)) { degs.getOrElse(it) { 0u } + if (it != variable) 0u else order },
//                        degs.getOrElse(variable) { 0u }.let { deg ->
//                            (deg + 1u .. deg + order)
//                                .fold(c) { acc, ord -> acc / doublingTimes(one, ord) }
//                        }
//                    )
//                }
//        }
//    )
//}
//
//@ExperimentalKoneAPI
//public fun <C> NumberedPolynomial<C>.nthAntiderivativeWithRespectTo(
//    ring: Field<C>,
//    variablesAndOrders: Map<Int, UInt>,
//): NumberedPolynomial<C> = ring {
//    val filteredVariablesAndOrders = variablesAndOrders.filterValues { it != 0u }
//    if (filteredVariablesAndOrders.isEmpty()) return this@nthAntiderivativeWithRespectTo
//    val maxRespectedVariable = filteredVariablesAndOrders.keys.maxOrNull()!!
//    NumberedPolynomial<C>(
//        buildMap(coefficients.size) {
//            coefficients
//                .forEach { (degs, c) ->
//                    put(
//                        List(max(maxRespectedVariable + 1, degs.size)) { degs.getOrElse(it) { 0u } + filteredVariablesAndOrders.getOrElse(it) { 0u } },
//                        filteredVariablesAndOrders.entries.fold(c) { acc1, (variable, order) ->
//                            degs.getOrElse(variable) { 0u }.let { deg ->
//                                (deg + 1u .. deg + order)
//                                    .fold(acc1) { acc, ord -> acc / doublingTimes(one, ord) }
//                            }
//                        }
//                    )
//                }
//        }
//    )
//}