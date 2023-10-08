/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.order.Order


context(Ring<N>, Order<N>)
public interface FlowNetwork<V, N> {
    public val vertices: Set<V>
    public val source: V
    public val sink: V
    public val innerVertices: Set<V>
    public fun capacity(from: V, to: V): N
}

context(FlowNetwork<V, N>)
public interface Flow<V, N> {
    public fun on(from: V, to: V): N
}