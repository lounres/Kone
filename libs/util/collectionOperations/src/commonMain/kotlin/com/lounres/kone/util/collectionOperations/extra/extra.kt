/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.util.collectionOperations.extra

import com.lounres.kone.util.collectionOperations.cartesianProduct


public inline operator fun <E1, E2> Iterable<E1>.times(other: Iterable<E2>): Sequence<Pair<E1, E2>> =
    cartesianProduct(this, other)