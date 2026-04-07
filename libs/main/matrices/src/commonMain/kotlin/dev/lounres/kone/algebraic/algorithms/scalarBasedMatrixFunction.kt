/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.OUT


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

public interface ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> : ScalarBaseForMatrixFunction<ComplexNumber<Number>> {
    public fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number
    
    public companion object
}

public class ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> {
    override val impliedKeys: ImpliedKeysRegistry<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> = ImpliedKeysRegistry {
        @OptIn(DelicateSuppliedTypeConstructor::class)
        val complexNumberType = SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType,
                ),
            ),
            isNullable = false,
        )
        ScalarBaseForMatrixExponentKey<ComplexNumber<Number>>(numberType = complexNumberType) implies { it }
    }
    override fun equals(other: Any?): Boolean = other is ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<$numberType>"
}

public class ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<Number>(
    public val numberType: SuppliedType,
) : RegistryKey<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> {
    override val impliedKeys: ImpliedKeysRegistry<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> = ImpliedKeysRegistry {
        @OptIn(DelicateSuppliedTypeConstructor::class)
        val complexNumberType = SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.algebraic.ComplexNumber",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = numberType,
                ),
            ),
            isNullable = false,
        )
        ScalarBaseForMatrixLogarithmKey<ComplexNumber<Number>>(numberType = complexNumberType) implies { it }
    }
    override fun equals(other: Any?): Boolean = other is ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<*> && numberType == other.numberType
    override fun hashCode(): Int = numberType.hashCode()
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<$numberType>"
}

public fun interface ScalarBasedMatrixFunctionApplier<Number, Matrix : MDList2<Number>, in Function: ScalarBaseForMatrixFunction<Number>> : KoneContext {
    public fun Matrix.after(function: Function): Matrix
    
    public companion object;
    
    public class Key<Number, Matrix : MDList2<Number>, Function: ScalarBaseForMatrixFunction<Number>>(
        public val matrixType: SuppliedType,
        public val functionType: SuppliedType,
    ) : RegistryKey<ScalarBasedMatrixFunctionApplier<Number, Matrix, Function>> {
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && matrixType == other.matrixType && functionType == other.functionType
        override fun hashCode(): Int = matrixType.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBasedMatrixFunctionApplier.Key<?, $matrixType, $functionType>"
    }
}

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix, Function>)
public fun <Number, Matrix : MDList2<Number>, Function: ScalarBaseForMatrixFunction<Number>> Matrix.after(function: Function): Matrix =
    with(scalarBasedMatrixFunctionApplier) { this@after.after(function) }

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix, Function>)
public operator fun <Number, Matrix : MDList2<Number>, Function: ScalarBaseForMatrixFunction<Number>> Function.invoke(matrix: Matrix): Matrix =
    with(scalarBasedMatrixFunctionApplier) { matrix.after(this@invoke) }