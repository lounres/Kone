/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.comparison.Equality


public fun <E> KoneMutableListBackedSet(context: Equality<E>): KoneMutableListBackedSet<E> =
    KoneMutableListBackedSet(context = context, KoneResizableLinkedArrayList(context = context))

context(Equality<E>)
public fun <E> KoneMutableListBackedSet(): KoneMutableListBackedSet<E> =
    KoneMutableListBackedSet(context = this@Equality, KoneResizableLinkedArrayList(context = this@Equality))