/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.basis


public fun interface VectorSpaceBasisDecomposition<out Number, in Vector, in Index> {
    public fun decompose(vector: Vector): Result<Number, Index>
    
    public interface Finite<out Number, Vector> : VectorSpaceBasisDecomposition<Number, Vector, UInt> {
        public val size: UInt
    }
    
    public fun interface Result<out Number, in Index> {
        public operator fun get(index: Index): Number
    }
}

public interface VectorSpaceBasis<out Number, Vector, in Index> : VectorSpaceBasisDecomposition<Number, Vector, Index> {
    public operator fun get(index: Index): Vector
    
    public interface Finite<out Number, Vector> : VectorSpaceBasis<Number, Vector, UInt>, VectorSpaceBasisDecomposition.Finite<Number, Vector>
}