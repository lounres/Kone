/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.CosineComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType


private object CosineComputerViaDefaultForDouble : CosineComputer<Double> {
    override fun Double.cos(): Double = kotlin.math.cos(this)
}

public fun CosineComputer.Companion.viaDefaultForDouble(): CosineComputer<Double> =
    CosineComputerViaDefaultForDouble

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CosineComputer.Companion.setViaDefaultForDouble() {
    CosineComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}