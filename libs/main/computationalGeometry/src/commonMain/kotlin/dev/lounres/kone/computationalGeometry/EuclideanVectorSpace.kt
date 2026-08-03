/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


@GenerateKoneContextKey
public interface EuclideanVectorSpaceOverRing<Number, Vector> : Module<Number, Vector> {
    @KoneContextInclude
    public val vectorDotVector: Dot<Vector, Vector, Number>
    
    public companion object;
}

@GenerateKoneContextKey
public interface EuclideanVectorSpaceOverField<Number, Vector> : VectorSpace<Number, Vector>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
}