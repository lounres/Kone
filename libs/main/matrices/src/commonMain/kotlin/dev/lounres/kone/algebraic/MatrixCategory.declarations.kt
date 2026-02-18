package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.multidimensionalCollections.MDList2


// The underlying ring is commutative
public interface MatrixCategoryOverRing<Number, Matrix: MDList2<Number>> : KoneContext {
    // region Matrix-Int operations
    public operator fun Matrix.times(other: Int): Matrix
    // endregion
    
    // region Matrix-UInt operations
    public operator fun Matrix.times(other: UInt): Matrix
    // endregion
    
    // region Matrix-Long operations
    public operator fun Matrix.times(other: Long): Matrix
    // endregion
    
    // region Matrix-ULong operations
    public operator fun Matrix.times(other: ULong): Matrix
    // endregion
    
    // region Int-Matrix operations
    public operator fun Int.times(other: Matrix): Matrix
    // endregion
    
    // region UInt-Matrix operations
    public operator fun UInt.times(other: Matrix): Matrix
    // endregion
    
    // region Long-Matrix operations
    public operator fun Long.times(other: Matrix): Matrix
    // endregion
    
    // region ULong-Matrix operations
    public operator fun ULong.times(other: Matrix): Matrix
    // endregion
    
    // region Matrix-Number operations
    public operator fun Matrix.times(other: Number): Matrix
    // endregion
    
    // region Number-Matrix operations
    public operator fun Number.times(other: Matrix): Matrix
    // endregion
    
    // region Matrix-Matrix operations
    public operator fun Matrix.unaryMinus(): Matrix
    public operator fun Matrix.plus(other: Matrix): Matrix
    public operator fun Matrix.minus(other: Matrix): Matrix
    // endregion
}

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: Int): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: UInt): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: Long): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: ULong): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Int.times(other: Matrix): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> UInt.times(other: Matrix): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Long.times(other: Matrix): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> ULong.times(other: Matrix): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.times(other: Number): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Number.times(other: Matrix): Matrix =
    with(matrixCategory) { this@times * other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.unaryMinus(): Matrix =
    with(matrixCategory) { -this@unaryMinus }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.plus(other: Matrix): Matrix =
    with(matrixCategory) { this@plus + other }

context(matrixCategory: MatrixCategoryOverRing<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.minus(other: Matrix): Matrix =
    with(matrixCategory) { this@minus - other }

public interface MatrixCategoryOverField<Number, Matrix: MDList2<Number>> : MatrixCategoryOverRing<Number, Matrix> {
    // region Matrix-Int operations
    public operator fun Matrix.div(other: Int): Matrix
    // endregion

    // region Matrix-UInt operations
    public operator fun Matrix.div(other: UInt): Matrix
    // endregion

    // region Matrix-Long operations
    public operator fun Matrix.div(other: Long): Matrix
    // endregion

    // region Matrix-ULong operations
    public operator fun Matrix.div(other: ULong): Matrix
    // endregion

    // region Matrix-Number operations
    public operator fun Matrix.div(other: Number): Matrix
    // endregion
}

context(matrixCategory: MatrixCategoryOverField<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.div(other: Int): Matrix =
    with(matrixCategory) { this@div / other }

context(matrixCategory: MatrixCategoryOverField<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.div(other: UInt): Matrix =
    with(matrixCategory) { this@div / other }

context(matrixCategory: MatrixCategoryOverField<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.div(other: Long): Matrix =
    with(matrixCategory) { this@div / other }

context(matrixCategory: MatrixCategoryOverField<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.div(other: ULong): Matrix =
    with(matrixCategory) { this@div / other }

context(matrixCategory: MatrixCategoryOverField<Number, Matrix>)
public operator fun <Number, Matrix : MDList2<Number>> Matrix.div(other: Number): Matrix =
    with(matrixCategory) { this@div / other }