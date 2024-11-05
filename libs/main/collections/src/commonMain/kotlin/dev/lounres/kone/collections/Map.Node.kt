/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: Describe contracts on equals and hashCode.

public interface KoneMapNode<out Key, out Value> {
    public val key: Key
    public val value: Value
}

public interface KoneMutableMapNode<out Key, Value> : KoneMapNode<Key, Value> {
    override var value: Value
    public fun remove()
}