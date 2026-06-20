/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.HyperbolicCosineComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo


private object HyperbolicCosineComputerViaDefaultForDouble : HyperbolicCosineComputer<Double> {
    override fun Double.cosh(): Double = kotlin.math.cosh(this)
}

public fun HyperbolicCosineComputer.Companion.viaDefaultForDouble(): HyperbolicCosineComputer<Double> =
    HyperbolicCosineComputerViaDefaultForDouble

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun HyperbolicCosineComputer.Companion.setViaDefaultForDouble() {
    HyperbolicCosineComputer.Key<Double>() correspondsTo viaDefaultForDouble()
}