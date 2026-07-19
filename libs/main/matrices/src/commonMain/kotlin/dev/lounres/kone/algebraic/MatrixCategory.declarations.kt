/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextHolderInclude
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


// The underlying ring is commutative
public interface MatrixCategoryOverRing<Number, Matrix: MDList2<Number>> : KoneContext {
    // region Matrix-Int operations
    @KoneContextHolderInclude
    public val matrixTimesInt: Times<Matrix, Int, Matrix>
    // endregion
    
    // region Matrix-UInt operations
    @KoneContextHolderInclude
    public val matrixTimesUInt: Times<Matrix, UInt, Matrix>
    // endregion
    
    // region Matrix-Long operations
    @KoneContextHolderInclude
    public val matrixTimesLong: Times<Matrix, Long, Matrix>
    // endregion
    
    // region Matrix-ULong operations
    @KoneContextHolderInclude
    public val matrixTimesULong: Times<Matrix, ULong, Matrix>
    // endregion
    
    // region Matrix-Number operations
    @KoneContextHolderInclude
    public val matrixTimesNumber: Times<Matrix, Number, Matrix>
    // endregion
    
    // region Int-Matrix operations
    @KoneContextHolderInclude
    public val intTimesMatrix: Times<Int, Matrix, Matrix>
    // endregion
    
    // region UInt-Matrix operations
    @KoneContextHolderInclude
    public val uIntTimesMatrix: Times<UInt, Matrix, Matrix>
    // endregion
    
    // region Long-Matrix operations
    @KoneContextHolderInclude
    public val longTimesMatrix: Times<Long, Matrix, Matrix>
    // endregion
    
    // region ULong-Matrix operations
    @KoneContextHolderInclude
    public val uLongTimesMatrix: Times<ULong, Matrix, Matrix>
    // endregion
    
    // region Number-Matrix operations
    @KoneContextHolderInclude
    public val numberTimesMatrix: Times<Number, Matrix, Matrix>
    // endregion
    
    // region Matrix-Matrix operations
    @KoneContextHolderInclude
    public val matrixUnaryMinus: UnaryMinus<Matrix, Matrix>
    @KoneContextHolderInclude
    public val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix>
    @KoneContextHolderInclude
    public val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix>
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<MatrixCategoryOverRing<Number, Matrix>>() {
        override fun toString(): String = "dev.lounres.kone.algebraic.MatrixCategoryOverRing.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}

public interface MatrixCategoryOverField<Number, Matrix: MDList2<Number>> : MatrixCategoryOverRing<Number, Matrix> {
    // region Matrix-Int operations
    @KoneContextHolderInclude
    public val matrixDivideInt: Divide<Matrix, Int, Matrix>
    // endregion

    // region Matrix-UInt operations
    @KoneContextHolderInclude
    public val matrixDivideUInt: Divide<Matrix, UInt, Matrix>
    // endregion

    // region Matrix-Long operations
    @KoneContextHolderInclude
    public val matrixDivideLong: Divide<Matrix, Long, Matrix>
    // endregion

    // region Matrix-ULong operations
    @KoneContextHolderInclude
    public val matrixDivideULong: Divide<Matrix, ULong, Matrix>
    // endregion

    // region Matrix-Number operations
    @KoneContextHolderInclude
    public val matrixDivideNumber: Divide<Matrix, Number, Matrix>
    // endregion
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number, @Supply Matrix : MDList2<Number>> : SuppliedTypeRegistryKey<MatrixCategoryOverField<Number, Matrix>>() {
        override val impliedKeys: ImpliedKeysRegistry<MatrixCategoryOverField<Number, Matrix>> by lazy {
            ImpliedKeysRegistry {
                MatrixCategoryOverRing.Key<Number, Matrix>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.MatrixCategoryOverField.Key<${suppliedTypeOf<Number>()}, ${suppliedTypeOf<Matrix>()}>"
    }
}