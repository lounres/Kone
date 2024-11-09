/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices

import dev.lounres.kone.collections.KoneIterable


public interface LatticeWithConnectivity<C, K, V>: Lattice<C, K, V> {
    public fun KoneIterable<Position<C, K>>.isConnected(): Boolean
}