/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


public interface AlgebraicContext


@OptIn(ExperimentalContracts::class)
public inline operator fun <A: AlgebraicContext, R> A.invoke(block: A.() -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return run(block)
}


// FIXME: KT-51243
//@OptIn(ExperimentalContracts::class)
//public inline operator fun <A: AlgebraicContext, R> A.invoke(block: context(A) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return run(block)
//}