/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class SquareRootsKey<@Supply Number> : SuppliedTypeRegistryKey<KoneList<Number>>() {
    override fun toString(): String = "dev.lounres.kone.algebraic.algorithms.SquareRootsKey<${suppliedTypeOf<Number>()}>"
}

@GenerateKoneContextKey
public fun interface SquareRootsComputer<Number> : KoneContext {
    public fun Number.squareRoots(): KoneList<Number>
    
    public companion object;
}

context(squareRootComputer: SquareRootsComputer<Number>)
public fun <Number> Number.squareRoots(): KoneList<Number> =
    with(squareRootComputer) { this@squareRoots.squareRoots() }