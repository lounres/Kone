/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring


// Waiting for context receivers :( FIXME: Uncomment when context receivers will be available

//public inline operator fun <
//        C,
//        P: Polynomial<C>,
//        A: Field<C>,
//        PS: PolynomialSpaceWithRing<C, P, A>,
//        R
//        > PS.invoke(block: context(A, PS) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return block(ring, this)
//}

//public inline operator fun <
//        C,
//        P: Polynomial<C>,
//        RF: RationalFunction<C, P>,
//        PS: PolynomialSpace<C, P>,
//        RFS: RationalFunctionSpaceWithPolynomialSpace<C, P, RF, PS>,
//        R
//        > RFS.invoke(block: context(PS, RFS) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return block(polynomialRing, this)
//}

//public inline operator fun <
//        C,
//        P: Polynomial<C>,
//        RF: RationalFunction<C, P>,
//        A: Ring<C>,
//        PS: PolynomialSpaceWithRing<C, P, A>,
//        RFS: RationalFunctionSpaceWithPolynomialSpace<C, P, RF, PS>,
//        R
//        > RFS.invoke(block: context(A, PS, RFS) () -> R): R {
////    FIXME: KT-32313
////    contract {
////        callsInPlace(block, EXACTLY_ONCE)
////    }
//    return block(polynomialRing.ring, polynomialRing, this)
//}