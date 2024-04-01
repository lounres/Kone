/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality


public interface KoneMapWithContext<K, out KC: Equality<K>, V, out VC: Equality<V>> : KoneMap<K, V> {
    public val keyContext: KC
    public val valueContext: VC
}

public interface KoneMutableMapWithContext<K, out KC: Equality<K>, V, out VC: Equality<V>> : KoneMapWithContext<K, KC, V, VC>, KoneMutableMap<K, V>