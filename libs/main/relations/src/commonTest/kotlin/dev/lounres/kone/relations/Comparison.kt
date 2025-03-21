/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.invoke
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.checkAll


class Comparison : FunSpec({
    
    test("test that the default equalities and hashings are the same instances`") {
        assertSoftly {
            defaultEquality<Int>() shouldBeSameInstanceAs defaultEquality<String>()
            absoluteEquality<Int>() shouldNotBeSameInstanceAs absoluteEquality<String>()
            defaultHashing<Int>() shouldBeSameInstanceAs defaultHashing<String>()
        }
    }
    
    test("test behaviours of the default equalities and hashings") {
        assertSoftly {
            checkAll<Int, Int> { a, b ->
                (a == b) shouldBe (defaultEquality<Int>()) { a eq b }
            }
            checkAll<String, String> { a, b ->
                (a == b) shouldBe (defaultEquality<String>()) { a eq b }
            }
        }
    }
    
})