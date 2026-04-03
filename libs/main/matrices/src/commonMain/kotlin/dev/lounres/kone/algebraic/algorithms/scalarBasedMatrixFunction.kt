/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public fun interface ScalarBaseForMatrixFunction<Number> {
    public fun evaluate(derivativeOrder: UInt, value: Number): Number
    
    public companion object
}

public class ScalarBaseForMatrixExponentKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<ScalarBaseForMatrixFunction<Number>> {
    override fun equals(other: Any?): Boolean = other is ScalarBaseForMatrixLogarithmKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentKey<$numberType>"
}

public class ScalarBaseForMatrixLogarithmKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<ScalarBaseForMatrixFunction<Number>> {
    override fun equals(other: Any?): Boolean = other is ScalarBaseForMatrixLogarithmKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmKey<$numberType>"
}

public fun interface ScalarBasedMatrixFunctionApplier<Number, Matrix : MDList2<Number>> : KoneContext {
    public fun Matrix.after(scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>>(
        public val matrixType: SuppliedType,
    ) : RegistryKey<ScalarBasedMatrixFunctionApplier<Number, Matrix>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *> && matrixType == other.matrixType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBasedMatrixFunctionApplier.Key<?, $matrixType>"
    }
}

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>)
public fun <Number, Matrix : MDList2<Number>> Matrix.after(scalarBaseForMatrixFunction: ScalarBaseForMatrixFunction<Number>): Matrix =
    with(scalarBasedMatrixFunctionApplier) { this@after.after(scalarBaseForMatrixFunction) }

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> ScalarBaseForMatrixFunction<Number>.invoke(matrix: Matrix): Matrix =
    with(scalarBasedMatrixFunctionApplier) { matrix.after(this@invoke) }