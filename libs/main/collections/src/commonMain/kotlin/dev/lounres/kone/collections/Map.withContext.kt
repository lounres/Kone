/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality


public interface KoneMapWithContext<Key, out KeyContext: Equality<Key>, Value> : KoneMap<Key, Value> {
    public val keyContext: KeyContext
}

public interface KoneMutableMapWithContext<Key, out KeyContext: Equality<Key>, Value> : KoneMapWithContext<Key, KeyContext, Value>, KoneMutableMap<Key, Value>