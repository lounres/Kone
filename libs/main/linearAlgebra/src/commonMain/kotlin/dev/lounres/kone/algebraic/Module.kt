/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


@GenerateKoneContextKey
public interface LeftModule<Number, Vector> : CommutativeGroup<Vector> {
    @KoneContextInclude
    public val numberTimesVector: Times<Number, Vector, Vector>
    
    public companion object;
}

@GenerateKoneContextKey
public interface RightModule<Number, Vector> : CommutativeGroup<Vector> {
    @KoneContextInclude
    public val vectorTimesNumber: Times<Vector, Number, Vector>
    
    public companion object;
}

// The underlying ring is commutative
@GenerateKoneContextKey
public interface Module<Number, Vector> : LeftModule<Number, Vector>, RightModule<Number, Vector> {
    public companion object;
}