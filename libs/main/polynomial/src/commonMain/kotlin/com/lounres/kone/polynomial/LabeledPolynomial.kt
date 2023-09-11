/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial


@Suppress("INAPPLICABLE_JVM_NAME") // FIXME: Waiting for KT-31420
public interface MultivariatePolynomialManipulationSpace<V, VP, MS> {
    // region Manipulation
    public fun variablePower(variable: V, power: UInt): VP

    public fun signatureOf(vararg variablePowers: VP): MS
    // endregion

    public operator fun V.unaryPlus(): MS = signatureOf(variablePower(this, 1u))
}

public typealias LabeledMonomialSignature = Map<String, UInt>
public typealias LabeledPolynomialCoefficients<C> = Map<LabeledMonomialSignature, C>

public open class LabeledPolynomialSpace :
    MultivariatePolynomialManipulationSpace<String, Pair<String, UInt>, LabeledMonomialSignature>
{
    public override fun variablePower(variable: String, power: UInt): Pair<String, UInt> = Pair(variable, power)
    public override fun signatureOf(vararg variablePowers: Pair<String, UInt>): LabeledMonomialSignature = mapOf(*variablePowers)
}

public fun main(): Unit = LabeledPolynomialSpace().run {
    val x = "x"
    val xPoly = +x
}