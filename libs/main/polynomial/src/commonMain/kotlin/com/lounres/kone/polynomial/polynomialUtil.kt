/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring


public inline operator fun <
        C,
        P: Polynomial<C>,
        A: Ring<C>,
        PS: PolynomialSpace<C, P, A>,
        R
        > PS.invoke(block: context(A, PS) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(ring, this)
}

public inline operator fun <
        C,
        P: Polynomial<C>,
        RF: RationalFunction<C, P>,
        A: Ring<C>,
        PS: PolynomialSpace<C, P, A>,
        RFS: RationalFunctionSpace<C, P, RF, A, PS>,
        R
        > RFS.invoke(block: context(A, PS, RFS) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(ring, polynomialSpace, this)
}