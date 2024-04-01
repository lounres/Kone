/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.linearAlgebra.experiment1.VectorSpace


public class EuclideanSpace<N, A>(
    public val numberRing: A,
    public val vectorSpace: VectorSpace<N>
) where A: Ring<N>, A: Order<N>