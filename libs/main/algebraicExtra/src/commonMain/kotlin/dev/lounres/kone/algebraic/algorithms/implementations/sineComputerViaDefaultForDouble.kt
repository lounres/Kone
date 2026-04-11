/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.SineComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType


private object SineComputerViaDefaultForDouble : SineComputer<Double> {
    override fun Double.sin(): Double = kotlin.math.sin(this)
}

public fun SineComputer.Companion.viaDefaultForDouble(): SineComputer<Double> =
    SineComputerViaDefaultForDouble

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun SineComputer.Companion.setViaDefaultForDouble() {
    SineComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}