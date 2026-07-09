/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.canvas

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.setSafeField
import dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.KoneContextRegistryBuilderDsl
import dev.lounres.kone.contexts.buildWithProvider
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo


public val defaultKoneContextRegistryBuilder: context(KoneContextRegistry.Provider) (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit = {
    Double.setSafeField()
//    EuclideanSpace2OverField.setFor<Double>()
    EuclideanSpace2OverField.Key<Double>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = contextOf<KoneContextRegistry.Provider>().get()
        EuclideanSpace2OverField(koneContextRegistry[Field.Key<Double>()])
    }
}

public val defaultKoneContextRegistry: KoneContextRegistry = KoneContextRegistry.buildWithProvider {
    defaultKoneContextRegistryBuilder()
}