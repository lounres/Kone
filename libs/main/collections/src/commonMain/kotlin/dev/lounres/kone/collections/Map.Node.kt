/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: Describe contracts on equals and hashCode.

public interface KoneMapNode<out K, out V> {
    public val key: K
    public val value: V
}

public interface KoneMutableMapNode<out K, V> : KoneMapNode<K, V> {
    override var value: V
    public fun remove()
}