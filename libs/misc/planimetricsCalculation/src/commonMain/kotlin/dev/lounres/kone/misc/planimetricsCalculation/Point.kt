/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.misc.planimetricsCalculation

import dev.lounres.kone.linearAlgebra.ColumnVector
import dev.lounres.kone.linearAlgebra.RowVector
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.polynomial.LabeledPolynomial
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


public data class Point<E>(
    val x: LabeledPolynomial<E>,
    val y: LabeledPolynomial<E>,
    val z: LabeledPolynomial<E>
) {
    public val rowVector: RowVector<LabeledPolynomial<E>> get() = RowVector(x, y, z)
    public val columnVector: ColumnVector<LabeledPolynomial<E>> get() = ColumnVector(x, y, z)
    
    public operator fun getValue(thisRef: Any?, property: KProperty<*>) : Point<E> = this
    
    public companion object {
//        context(_: PlanimetricsCalculationSpace<E>)
//        public operator fun <E> provideDelegate(thisRef: Any?, property: KProperty<*>): Point<E> = Point(property.name)
    }

    @Suppress("ClassName")
    public object finite {
//        context(_: PlanimetricsCalculationSpace<E>)
//        public operator fun <E> provideDelegate(thisRef: Any?, property: KProperty<*>) : Point<E> =
//            calculate {
//                property.name.let {
//                    Point(
//                        LabeledVariable(it + "_x").asLabeledPolynomial<E>(),
//                        LabeledVariable(it + "_y").asLabeledPolynomial<E>(),
//                        polynomialOne
//                    )
//                }
//            }
    }
}

public data class AbsoluteType<T>(
    public val kClass: KClass<T & Any>,
    public val typeArguments: List<AbsoluteType<*>>,
    public val isNullable: Boolean,
)

public fun <T> absoluteTypeOf(): AbsoluteType<T> = error("Intrinsic function call. Should be replaced by compiler plugin.")

public interface RegistryKey<T> {
    public val typeKey: AbsoluteType<T>
}

public interface Registry {
    public operator fun <T> get(registryKey: RegistryKey<T>): T
    public fun <T> getMaybe(registryKey: RegistryKey<T>): Maybe<T>
}