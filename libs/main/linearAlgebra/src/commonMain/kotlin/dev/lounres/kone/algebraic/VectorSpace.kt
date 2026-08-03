/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


@GenerateKoneContextKey
public interface VectorSpace<Number, Vector> : Module<Number, Vector> {
    @KoneContextInclude
    public val vectorDivideNumber: Divide<Vector, Number, Vector>
    @KoneContextInclude
    public val vectorDivideInt: Divide<Vector, Int, Vector>
    @KoneContextInclude
    public val vectorDivideUInt: Divide<Vector, UInt, Vector>
    @KoneContextInclude
    public val vectorDivideLong: Divide<Vector, Long, Vector>
    @KoneContextInclude
    public val vectorDivideULong: Divide<Vector, ULong, Vector>
    
    public companion object;
    
    @GenerateKoneContextKey
    public interface FiniteDimensional<Number, Vector> : VectorSpace<Number, Vector> {
        public val dimension: UInt
        
        public companion object;
    }
}