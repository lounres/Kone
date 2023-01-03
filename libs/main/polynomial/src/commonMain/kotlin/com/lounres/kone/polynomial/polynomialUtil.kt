/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field


// Waiting for context receivers :( FIXME: Uncomment when context receivers will be available

//public inline operator fun <C, P: Polynomial<C>, A: Field<C>, S: PolynomialSpaceWithRing<C, P, A>, R> S.invoke(block: context(A, S) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return block(ring, this)
//}