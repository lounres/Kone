/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison

import dev.lounres.kone.context.invoke
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.checkAll


class SomeClass

class Comparison : StringSpec({
    
    "test that the default and absolute equalities and hashings are the same instances`" {
        assertSoftly {
            defaultEquality<Int>() shouldBeSameInstanceAs defaultEquality<String>()
            defaultEquality<Int>() shouldBeSameInstanceAs defaultHashing<String>()
            absoluteEquality<Int>() shouldNotBeSameInstanceAs defaultEquality<String>()
            absoluteEquality<Int>() shouldBeSameInstanceAs absoluteHashing<String>()
        }
    }
    
    "test behaviours of the default and absolute equalities and hashings" {
        assertSoftly {
            withClue("testing default equality/hashing") {
                val defaultIntEquality = defaultEquality<Int>()
                checkAll<Int, Int> { a, b ->
                    (a == b) shouldBe defaultIntEquality { a eq b }
                }
                val defaultStringEquality = defaultEquality<String>()
                checkAll<String, String> { a, b ->
                    (a == b) shouldBe defaultStringEquality { a eq b }
                }
            }
        }
    }
    
})