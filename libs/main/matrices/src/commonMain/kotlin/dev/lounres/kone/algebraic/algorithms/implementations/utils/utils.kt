/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.algorithms.implementations.utils

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrElse


public inline fun <T> KoneContextRegistry.requestFor(key: RegistryKey<T>, requester: () -> String): T =
    getOrElse(key) { error("${requester()} requested absent key $key") }