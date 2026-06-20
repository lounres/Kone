/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.PlanarVectorArgumentComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import kotlin.math.atan2


private object PlanarVectorArgumentComputerViaDefaultForDouble : PlanarVectorArgumentComputer<Double> {
    override fun planarVectorArgument(x: Double, y: Double): Double = atan2(x = x, y = y)
}

public fun PlanarVectorArgumentComputer.Companion.viaDefaultForDouble(): PlanarVectorArgumentComputer<Double> =
    PlanarVectorArgumentComputerViaDefaultForDouble

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun PlanarVectorArgumentComputer.Companion.setViaDefaultForDouble() {
    PlanarVectorArgumentComputer.Key<Double>() correspondsTo viaDefaultForDouble()
}