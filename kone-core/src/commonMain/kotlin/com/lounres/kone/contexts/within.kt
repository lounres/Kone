/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.contexts

import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


// FIXME: Replace with idiomatic construction
public fun <C, T> within(context: C, block: context(C) () -> T) : T {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return block(context)
}

// FIXME: Replace with idiomatic construction
public fun <C, R, T> withinWith(context: C, receiver: R, block: context(C) R.() -> T) : T {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return block(context, receiver)
}