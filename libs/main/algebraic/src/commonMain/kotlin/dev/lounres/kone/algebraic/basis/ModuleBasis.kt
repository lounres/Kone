/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.basis

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div


public fun interface ModuleBasisDecomposition<out Number, in Vector, in Index> {
    public fun decompose(vector: Vector): Result<Number, Index>
    
    public interface Finite<out Number, Vector> : ModuleBasisDecomposition<Number, Vector, UInt> {
        public val size: UInt
    }
    
    public interface Result<out Number, in Index> {
        public val scalar: Number
        public operator fun get(index: Index): Number
    }
}

context(_: Field<Number>)
public fun <Number, Vector, Index> ModuleBasisDecomposition<Number, Vector, Index>.toVectorSpaceBasisDecomposition(): VectorSpaceBasisDecomposition<Number, Vector, Index> =
    VectorSpaceBasisDecomposition { vector ->
        val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
        
        VectorSpaceBasisDecomposition.Result { index ->
            decomposition[index] / decomposition.scalar
        }
    }

context(_: Field<Number>)
public fun <Number, Vector> ModuleBasisDecomposition.Finite<Number, Vector>.toVectorSpaceBasisDecomposition(): VectorSpaceBasisDecomposition.Finite<Number, Vector> =
    object : VectorSpaceBasisDecomposition.Finite<Number, Vector> {
        override val size: UInt get() = this@toVectorSpaceBasisDecomposition.size
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, UInt> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                decomposition[index] / decomposition.scalar
            }
        }
    }

public interface ModuleBasis<out Number, Vector, in Index> : ModuleBasisDecomposition<Number, Vector, Index> {
    public operator fun get(index: Index): Vector
    
    public interface Finite<out Number, Vector> : ModuleBasis<Number, Vector, UInt>, ModuleBasisDecomposition.Finite<Number, Vector>
}

context(_: Field<Number>)
public fun <Number, Vector, Index> ModuleBasis<Number, Vector, Index>.toVectorSpaceBasisDecomposition(): VectorSpaceBasis<Number, Vector, Index> =
    object : VectorSpaceBasis<Number, Vector, Index> {
        override fun get(index: Index): Vector = this@toVectorSpaceBasisDecomposition[index]
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, Index> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                decomposition[index] / decomposition.scalar
            }
        }
    }

context(_: Field<Number>)
public fun <Number, Vector> ModuleBasis.Finite<Number, Vector>.toVectorSpaceBasisDecomposition(): VectorSpaceBasis.Finite<Number, Vector> =
    object : VectorSpaceBasis.Finite<Number, Vector> {
        override val size: UInt get() = this@toVectorSpaceBasisDecomposition.size
        
        override fun get(index: UInt): Vector = this@toVectorSpaceBasisDecomposition[index]
        
        override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, UInt> {
            val decomposition = this@toVectorSpaceBasisDecomposition.decompose(vector)
            
            return VectorSpaceBasisDecomposition.Result { index ->
                decomposition[index] / decomposition.scalar
            }
        }
    }