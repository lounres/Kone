/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality


public interface KoneSetWithContext<E, out EC: Equality<E>> : KoneSet<E> {
    public val elementContext: EC
}

public interface KoneMutableSetWithContext<E, out EC: Equality<E>> : KoneMutableSet<E>, KoneSetWithContext<E, EC>

public interface KoneNoddedSetWithContext<E, out EC: Equality<E>> : KoneSetWithContext<E, EC>, KoneNoddedSet<E>

public interface KoneNoddedMutableSetWithContext<E, out EC: Equality<E>> : KoneMutableSetWithContext<E, EC>, KoneNoddedSetWithContext<E, EC>, KoneNoddedMutableSet<E>