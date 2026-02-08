/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.relations.Equality


interface KoneMutableSetProducer {
    fun <Element> produce(elementContext: Equality<Element>)
}

interface ListImplementationDescription {
    val name: String
    val setProducer: KoneMutableSetProducer
}

val SetImplementationsTests by testSuite {

}