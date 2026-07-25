/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.assertions.*
import dev.lounres.kone.contexts.invoke


val RelationsTests by testSuite {
    test("test that the default equalities and hashings are the same instances`") {
        AssertionScope.softly {
            Expect of Equality.defaultFor<Int>() toBeTheSameInstanceAs Equality.defaultFor<String>()
            Expect of Equality.absoluteFor<Int>() toBeTheSameInstanceAs Equality.absoluteFor<String>()
            Expect of Hashing.defaultFor<Int>() toBeTheSameInstanceAs Hashing.defaultFor<String>()
        }
    }
    
    test("test behaviours of the default equalities and hashings") {
        AssertionScope.softly {
            val intValues = listOf(1, 2, 3, 4, 5)
            for (a in intValues) for (b in intValues) withClue("Checking properties for $a and $b") {
                Expect of (Equality.defaultFor<Int>()) { a eq b } toBe (a == b)
            }
            val stringValues = listOf("a", "b", "c", "d", "e", "f")
            for (a in stringValues) for (b in stringValues) withClue("Checking properties for \"$a\" and \"$b\"") {
                Expect of (Equality.defaultFor<String>()) { a eq b } toBe (a == b)
            }
        }
    }
}