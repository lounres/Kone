/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.LogarithmComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.ln


private object LogarithmComputerViaDefaultForDouble : LogarithmComputer<Double> {
    override fun Double.logarithm(): Double = ln(this)
}

public fun LogarithmComputer.Companion.viaDefaultForDouble(): LogarithmComputer<Double> =
    LogarithmComputerViaDefaultForDouble

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun LogarithmComputer.Companion.setViaDefaultForDouble() {
    LogarithmComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}