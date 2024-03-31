/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneMutableListBackedSet(context: Equality<E> = defaultEquality()): KoneMutableListBackedSet<E> =
    KoneMutableListBackedSet(KoneResizableLinkedArrayList(context = context))