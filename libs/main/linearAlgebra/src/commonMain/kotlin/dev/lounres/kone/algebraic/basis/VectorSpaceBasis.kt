/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.basis


/**
 * A functional interface for decomposing a vector into coefficients with respect to a vector space basis.
 *
 * @param Number The type of scalar coefficients.
 * @param Vector The type of vectors being decomposed.
 * @param Index The type used to index basis elements.
 */
public fun interface VectorSpaceBasisDecomposition<out Number, in Vector, in Index> {
    /**
     * Decomposes a [vector] into coefficients with respect to the basis.
     *
     * @param vector The vector to decompose.
     * @return A [Result] providing coefficient access.
     */
    public fun decompose(vector: Vector): Result<Number, Index>
    
    /**
     * A finite version of [VectorSpaceBasisDecomposition] indexed by [UInt].
     *
     * @param Number The type of scalar coefficients.
     * @param Vector The type of vectors being decomposed.
     */
    public interface Finite<out Number, Vector> : VectorSpaceBasisDecomposition<Number, Vector, UInt> {
        /**
         * The number of basis elements.
         */
        public val size: UInt
    }
    
    /**
     * The result of decomposing a vector: provides access to scalar coefficients by index.
     * The result satisfies formula \[input = \sum_{index\ i} result&lbrack;i&rbrack; \cdot basis&lbrack;i&rbrack;\]
     *
     * @param Number The type of scalar coefficients.
     * @param Index The type used to index coefficients.
     */
    public fun interface Result<out Number, in Index> {
        /**
         * Returns the coefficient at the given [index].
         *
         * @param index The index of the coefficient.
         * @return The scalar coefficient at [index].
         */
        public operator fun get(index: Index): Number
    }
}

/**
 * Represents a basis of a vector space over a field.
 *
 * @param Number The type of scalar coefficients (from the underlying field).
 * @param Vector The type of vectors.
 * @param Index The type used to index basis elements.
 */
public interface VectorSpaceBasis<out Number, Vector, in Index> : VectorSpaceBasisDecomposition<Number, Vector, Index> {
    /**
     * Returns the basis element at the given [index].
     *
     * @param index The index of the basis element.
     * @return The basis vector at [index].
     */
    public operator fun get(index: Index): Vector
    
    /**
     * A finite version of [VectorSpaceBasis] indexed by [UInt].
     *
     * @param Number The type of scalar coefficients.
     * @param Vector The type of vectors.
     */
    public interface Finite<out Number, Vector> : VectorSpaceBasis<Number, Vector, UInt>, VectorSpaceBasisDecomposition.Finite<Number, Vector>
}