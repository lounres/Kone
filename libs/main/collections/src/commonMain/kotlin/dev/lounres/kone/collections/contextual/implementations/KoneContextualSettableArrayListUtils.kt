/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.contextual.KoneContextualMutableArray


public fun <E> KoneContextualSettableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneContextualSettableArrayList<E> =
    KoneContextualSettableArrayList(KoneContextualMutableArray(size, initializer))