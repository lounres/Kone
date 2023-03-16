/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections

public interface KoneIterable<out E> {
    public operator fun iterator(): KoneIterator<E>
}

public interface KoneIterableCollection<out E>: KoneIterable<E>, KoneCollection<E>

public interface KoneMutableIterableCollection<E> : KoneMutableCollection<E>, KoneIterableCollection<E>