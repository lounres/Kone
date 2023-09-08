/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.KoneContext
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME", "PARAMETER_NAME_CHANGED_ON_OVERRIDE") // FIXME: Waiting for KT-31420
public interface PolynomialSpace<C, P, out A: Ring<C>>: KoneContext {
    // region Context accessors
    public val constantRing: A
    // endregion

    // region Constant constants
    public val constantZero: C get() = constantRing.zero
    public val constantOne: C get() = constantRing.one
    // endregion
}

@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialSpace<C, V, P, out A: Ring<C>>: PolynomialSpace<C, P, A> {

    // region Variable-Variable operations
    @JvmName("unaryPlusVariable")
    public operator fun V.unaryPlus(): P
}