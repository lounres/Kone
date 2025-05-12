/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.relations.Equality
import io.kotest.core.spec.style.FunSpec


interface KoneMutableSetProducer {
    fun <Element> produce(elementContext: Equality<Element>)
}

interface ListImplementationDescription {
    val name: String
    val setProducer: KoneMutableSetProducer
}

class SetImplementationsTests : FunSpec({

})