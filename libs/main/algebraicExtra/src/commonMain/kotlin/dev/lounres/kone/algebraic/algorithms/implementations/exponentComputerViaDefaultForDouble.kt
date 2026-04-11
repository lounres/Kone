/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.ExponentComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.exp


private object ExponentComputerViaDefaultForDouble : ExponentComputer<Double> {
    override fun Double.exponent(): Double = exp(this)
}

public fun ExponentComputer.Companion.viaDefaultForDouble(): ExponentComputer<Double> =
    ExponentComputerViaDefaultForDouble

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun ExponentComputer.Companion.setViaDefaultForDouble() {
    ExponentComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}