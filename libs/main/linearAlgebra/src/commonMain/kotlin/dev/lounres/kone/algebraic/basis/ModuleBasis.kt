/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.basis

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.localUnwrap


/**
 * A functional interface for decomposing a module element into coefficients with respect to a module basis.
 *
 * @param Number The type of scalar coefficients (from the underlying ring).
 * @param Vector The type of module elements being decomposed.
 * @param Index The type used to index basis elements.
 */
public fun interface ModuleBasisDecomposition<out Number, in Vector, in Index> {
    /**
     * Decomposes a [vector] into coefficients with respect to the basis.
     *
     * @param vector The module element to decompose.
     * @return A [Result] providing coefficient access and the scalar norm.
     */
    public fun decompose(vector: Vector): Result<Number, Index>
    
    /**
     * A finite version of [ModuleBasisDecomposition] indexed by [UInt].
     *
     * @param Number The type of scalar coefficients.
     * @param Vector The type of module elements.
     */
    public interface Finite<out Number, in Vector> : ModuleBasisDecomposition<Number, Vector, UInt> {
        /**
         * The number of basis elements.
         */
        public val size: UInt
    }
    
    /**
     * The result of decomposing a module element: provides a common scalar and per-index coefficients.
     * The result satisfies formula \[scalar \cdot input = \sum_{index\ i} result&lbrack;i&rbrack; \cdot basis&lbrack;i&rbrack;\]
     *
     * @param Number The type of scalar coefficients.
     * @param Index The type used to index coefficients.
     *
     * @usesMathJax
     */
    public interface Result<out Number, in Index> {
        /**
         * The common scalar factor for all coefficients.
         */
        public val scalar: Number
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
 * Converts a [ModuleBasisDecomposition] to a [VectorSpaceBasisDecomposition] over a field
 * by dividing each coefficient by the common scalar.
 *
 * @param field The field context providing division.
 * @receiver The module basis decomposition to convert.
 * @param Number The type of scalar coefficients.
 * @param Vector The type of vectors.
 * @param Index The type used to index basis elements.
 * @return A new [VectorSpaceBasisDecomposition] with normalized coefficients.
 */
context(field: Field<Number>)
public fun <Number, Vector, Index> ModuleBasisDecomposition<Number, Vector, Index>.toVectorSpaceBasisDecomposition(): VectorSpaceBasisDecomposition<Number, Vector, Index> =
    VectorSpaceBasisDecomposition { vector ->
        val decomposition = this.decompose(vector)
        
        VectorSpaceBasisDecomposition.Result { index ->
            KoneContext.localUnwrap(field)
            decomposition[index] / decomposition.scalar
        }
    }

/**
 * Converts a finite [ModuleBasisDecomposition.Finite] to a [VectorSpaceBasisDecomposition.Finite] over a field
 * by dividing each coefficient by the common scalar.
 *
 * @param field The field context providing division.
 * @receiver The finite module basis decomposition to convert.
 * @param Number The type of scalar coefficients.
 * @param Vector The type of vectors.
 * @return A new finite [VectorSpaceBasisDecomposition] with normalized coefficients.
 */
context(field: Field<Number>)
public fun <Number, Vector> ModuleBasisDecomposition.Finite<Number, Vector>.toVectorSpaceBasisDecomposition(): VectorSpaceBasisDecomposition.Finite<Number, Vector> =
    object : VectorSpaceBasisDecomposition.Finite<Number, Vector> {
        override val size: UInt get() = this@toVectorSpaceBasisDecomposition.size
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, UInt> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                KoneContext.localUnwrap(field)
                decomposition[index] / decomposition.scalar
            }
        }
    }

/**
 * Represents a basis of a module over a ring.
 *
 * @param Number The type of scalar coefficients (from the underlying ring).
 * @param Vector The type of module elements.
 * @param Index The type used to index basis elements.
 */
public interface ModuleBasis<out Number, Vector, in Index> : ModuleBasisDecomposition<Number, Vector, Index> {
    /**
     * Returns the basis element at the given [index].
     *
     * @param index The index of the basis element.
     * @return The basis vector at [index].
     */
    public operator fun get(index: Index): Vector
    
    /**
     * A finite version of [ModuleBasis] indexed by [UInt].
     *
     * @param Number The type of scalar coefficients.
     * @param Vector The type of module elements.
     */
    public interface Finite<out Number, Vector> : ModuleBasis<Number, Vector, UInt>, ModuleBasisDecomposition.Finite<Number, Vector>
}

/**
 * Converts a [ModuleBasis] to a [VectorSpaceBasis] over a field
 * by dividing each decomposition coefficient by the common scalar.
 *
 * @param field The field context providing division.
 * @receiver The module basis to convert.
 * @param Number The type of scalar coefficients.
 * @param Vector The type of vectors.
 * @param Index The type used to index basis elements.
 * @return A new [VectorSpaceBasis] with normalized coefficients.
 */
context(field: Field<Number>)
public fun <Number, Vector, Index> ModuleBasis<Number, Vector, Index>.toVectorSpaceBasisDecomposition(): VectorSpaceBasis<Number, Vector, Index> =
    object : VectorSpaceBasis<Number, Vector, Index> {
        override fun get(index: Index): Vector = this@toVectorSpaceBasisDecomposition[index]
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, Index> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                KoneContext.localUnwrap(field)
                decomposition[index] / decomposition.scalar
            }
        }
    }

/**
 * Converts a finite [ModuleBasis.Finite] to a [VectorSpaceBasis.Finite] over a field
 * by dividing each decomposition coefficient by the common scalar.
 *
 * @param field The field context providing division.
 * @receiver The finite module basis to convert.
 * @param Number The type of scalar coefficients.
 * @param Vector The type of vectors.
 * @return A new finite [VectorSpaceBasis] with normalized coefficients.
 */
context(field: Field<Number>)
public fun <Number, Vector> ModuleBasis.Finite<Number, Vector>.toVectorSpaceBasisDecomposition(): VectorSpaceBasis.Finite<Number, Vector> =
    object : VectorSpaceBasis.Finite<Number, Vector> {
        override val size: UInt get() = this@toVectorSpaceBasisDecomposition.size
        
        override fun get(index: UInt): Vector = this@toVectorSpaceBasisDecomposition[index]
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, UInt> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                KoneContext.localUnwrap(field)
                decomposition[index] / decomposition.scalar
            }
        }
    }