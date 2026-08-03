/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.multidimensionalCollections.MDList2


// The underlying ring is commutative
@GenerateKoneContextKey
public interface MatrixCategoryOverRing<Number, Matrix: MDList2<Number>> : KoneContext {
    // region Matrix-Int operations
    @KoneContextInclude
    public val matrixTimesInt: Times<Matrix, Int, Matrix>
    // endregion
    
    // region Matrix-UInt operations
    @KoneContextInclude
    public val matrixTimesUInt: Times<Matrix, UInt, Matrix>
    // endregion
    
    // region Matrix-Long operations
    @KoneContextInclude
    public val matrixTimesLong: Times<Matrix, Long, Matrix>
    // endregion
    
    // region Matrix-ULong operations
    @KoneContextInclude
    public val matrixTimesULong: Times<Matrix, ULong, Matrix>
    // endregion
    
    // region Matrix-Number operations
    @KoneContextInclude
    public val matrixTimesNumber: Times<Matrix, Number, Matrix>
    // endregion
    
    // region Int-Matrix operations
    @KoneContextInclude
    public val intTimesMatrix: Times<Int, Matrix, Matrix>
    // endregion
    
    // region UInt-Matrix operations
    @KoneContextInclude
    public val uIntTimesMatrix: Times<UInt, Matrix, Matrix>
    // endregion
    
    // region Long-Matrix operations
    @KoneContextInclude
    public val longTimesMatrix: Times<Long, Matrix, Matrix>
    // endregion
    
    // region ULong-Matrix operations
    @KoneContextInclude
    public val uLongTimesMatrix: Times<ULong, Matrix, Matrix>
    // endregion
    
    // region Number-Matrix operations
    @KoneContextInclude
    public val numberTimesMatrix: Times<Number, Matrix, Matrix>
    // endregion
    
    // region Matrix-Matrix operations
    @KoneContextInclude
    public val matrixUnaryMinus: UnaryMinus<Matrix, Matrix>
    @KoneContextInclude
    public val matrixPlusMatrix: Plus<Matrix, Matrix, Matrix>
    @KoneContextInclude
    public val matrixMinusMatrix: Minus<Matrix, Matrix, Matrix>
    // endregion
    
    public companion object;
}

@GenerateKoneContextKey
public interface MatrixCategoryOverField<Number, Matrix: MDList2<Number>> : MatrixCategoryOverRing<Number, Matrix> {
    // region Matrix-Int operations
    @KoneContextInclude
    public val matrixDivideInt: Divide<Matrix, Int, Matrix>
    // endregion

    // region Matrix-UInt operations
    @KoneContextInclude
    public val matrixDivideUInt: Divide<Matrix, UInt, Matrix>
    // endregion

    // region Matrix-Long operations
    @KoneContextInclude
    public val matrixDivideLong: Divide<Matrix, Long, Matrix>
    // endregion

    // region Matrix-ULong operations
    @KoneContextInclude
    public val matrixDivideULong: Divide<Matrix, ULong, Matrix>
    // endregion

    // region Matrix-Number operations
    @KoneContextInclude
    public val matrixDivideNumber: Divide<Matrix, Number, Matrix>
    // endregion
    
    public companion object;
}