/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.lattices


public interface LatticeWithConnectivity<C, K, V>: Lattice<C, K, V> {
    public fun Collection<Position<C, K>>.isConnected(): Boolean
}