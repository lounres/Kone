/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations

import dev.lounres.kone.algebraic.algorithms.PositiveSquareRootComputer
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.sqrt


private object PositiveSquareRootViaDefaultForDouble : PositiveSquareRootComputer<Double> {
    override fun Double.positiveSquareRoot(): Double {
        require(this.isFinite() && this >= 0.0) { "Cannot compute square root value for $this" }
        return sqrt(this)
    }
    override fun Double.positiveSquareRootOrNull(): Double? = if (this.isFinite() && this >= 0.0) sqrt(this) else null
    override fun Double.positiveSquareRootMaybe(): Maybe<Double> = if (this.isFinite() && this >= 0.0) Some(sqrt(this)) else None
}

public fun PositiveSquareRootComputer.Companion.viaDefaultForDouble(): PositiveSquareRootComputer<Double> =
    PositiveSquareRootViaDefaultForDouble

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun PositiveSquareRootComputer.Companion.setViaDefaultForDouble() {
    PositiveSquareRootComputer.Key<Double>(numberType = Double.suppliedType) correspondsTo viaDefaultForDouble()
}