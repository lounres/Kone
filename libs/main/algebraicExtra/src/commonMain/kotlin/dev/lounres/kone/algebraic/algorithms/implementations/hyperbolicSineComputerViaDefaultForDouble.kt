/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.HyperbolicSineComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType


private object HyperbolicSineComputerViaDefaultForDouble : HyperbolicSineComputer<Double> {
    override fun Double.sinh(): Double = kotlin.math.sinh(this)
}

public fun HyperbolicSineComputer.Companion.viaDefaultForDouble(): HyperbolicSineComputer<Double> =
    HyperbolicSineComputerViaDefaultForDouble

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun HyperbolicSineComputer.Companion.setViaDefaultForDouble() {
    HyperbolicSineComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}