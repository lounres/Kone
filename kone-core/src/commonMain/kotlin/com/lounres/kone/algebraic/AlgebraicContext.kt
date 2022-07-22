/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


public interface AlgebraicContext


@OptIn(ExperimentalContracts::class)
public inline operator fun <A: AlgebraicContext, R> A.invoke(block: context(A) () -> R): R {
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return run(block)
}