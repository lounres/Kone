/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextExclude
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


@GenerateKoneContextKey
public interface FieldExtension<Number, Vector> : CommutativeAlgebra<Number, Vector>, Field<Vector>, VectorSpace<Number, Vector> {
    @KoneContextInclude
    public val numberDivideVector: Divide<Number, Vector, Vector> get() = Divide { left, right -> numberDivideNumber { valueOf(left) / right } }
    
    @KoneContextExclude
    override val vectorDivideInt: Divide<Vector, Int, Vector> get() = numberDivideInt
    @KoneContextExclude
    override val vectorDivideUInt: Divide<Vector, UInt, Vector> get() = numberDivideUInt
    @KoneContextExclude
    override val vectorDivideLong: Divide<Vector, Long, Vector> get() = numberDivideLong
    @KoneContextExclude
    override val vectorDivideULong: Divide<Vector, ULong, Vector> get() = numberDivideULong
    
    public companion object;
}