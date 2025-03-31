/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.set.KoneReifiedSet


// TODO: Describe contracts on equals and hashCode.

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneReifiedMap<out Key, out Value> : KoneMap<@UnsafeVariance Key, Value> {
    override val keysView: KoneReifiedSet<Key>
    override val keys: KoneReifiedSet<Key> get() = keysView
    
    override fun getNodeOrNull(key: @UnsafeVariance Key): KoneMapNode<Key, Value>?
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableReifiedMap<Key, Value> : KoneReifiedMap<Key, Value>, KoneMutableMap<Key, Value> {
    override val keysView: KoneReifiedSet<Key>
    override val keys: KoneReifiedSet<Key>
}