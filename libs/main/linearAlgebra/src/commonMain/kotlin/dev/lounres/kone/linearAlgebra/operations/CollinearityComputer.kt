/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.operations


public interface CollinearityComputer<in Vector> {
    public fun checkCollinearity(vector1: Vector, vector2: Vector): Boolean
    
    public companion object;
}