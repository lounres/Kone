/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.algebraic.ComplexNumber
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


public fun interface ScalarBaseForMatrixFunction<Number> {
    public fun evaluate(derivativeOrder: UInt, value: Number): Number
    
    public companion object
}

@Suppliable
public class ScalarBaseForMatrixExponentKey<@Supply Number> : SuppliedTypeRegistryKey<ScalarBaseForMatrixFunction<Number>>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentKey<${suppliedTypeOf<Number>()}>"
}

@Suppliable
public class ScalarBaseForMatrixLogarithmKey<@Supply Number> : SuppliedTypeRegistryKey<ScalarBaseForMatrixFunction<Number>>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmKey<${suppliedTypeOf<Number>()}>"
}

public interface ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number> : ScalarBaseForMatrixFunction<ComplexNumber<Number>> {
    public fun bound(derivativeOrder: UInt, convexHullVertices: KoneIterable<ComplexNumber<Number>>): Number
    
    public companion object
}

@Suppliable
public class ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<@Supply Number> : SuppliedTypeRegistryKey<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>>() {
    override val impliedKeys: ImpliedKeysRegistry<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> by lazy {
        ImpliedKeysRegistry {
            ScalarBaseForMatrixExponentKey<ComplexNumber<Number>>().impliesSame()
        }
    }
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixExponentWithComplexNumberConvexHullBoundKey<${suppliedTypeOf<Number>()}>"
}

@Suppliable
public class ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<@Supply Number> : SuppliedTypeRegistryKey<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>>() {
    override val impliedKeys: ImpliedKeysRegistry<ScalarBaseForMatrixFunctionWithComplexNumberConvexHullBound<Number>> by lazy {
        ImpliedKeysRegistry {
            ScalarBaseForMatrixLogarithmKey<ComplexNumber<Number>>().impliesSame()
        }
    }
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.ScalarBaseForMatrixLogarithmWithComplexNumberConvexHullBoundKey<${suppliedTypeOf<Number>()}>"
}

@GenerateKoneContextKey
public fun interface ScalarBasedMatrixFunctionApplier<Number, Matrix : MDList2<Number>, in Function: ScalarBaseForMatrixFunction<Number>> : KoneContext {
    public fun Matrix.after(function: Function): Matrix
    
    public companion object;
}

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix, Function>)
public fun <Number, Matrix : MDList2<Number>, Function: ScalarBaseForMatrixFunction<Number>> Matrix.after(function: Function): Matrix =
    with(scalarBasedMatrixFunctionApplier) { this@after.after(function) }

context(scalarBasedMatrixFunctionApplier: ScalarBasedMatrixFunctionApplier<Number, Matrix, Function>)
public operator fun <Number, Matrix : MDList2<Number>, Function: ScalarBaseForMatrixFunction<Number>> Function.invoke(matrix: Matrix): Matrix =
    with(scalarBasedMatrixFunctionApplier) { matrix.after(this@invoke) }