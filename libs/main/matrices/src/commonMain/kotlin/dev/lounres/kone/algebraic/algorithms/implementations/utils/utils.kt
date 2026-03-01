package dev.lounres.kone.algebraic.algorithms.implementations.utils

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrElse


public inline fun <T> KoneContextRegistry.requestFor(key: RegistryKey<T>, requester: () -> String): T =
    getOrElse(key) { error("${requester()} requested absent key $key") }