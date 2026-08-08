/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer


object KoneArraySettableListDescription : ListImplementationDescription {
    override val name get() = "KoneArraySettableList"
    
    override val listProducer: KoneListProducer get() = KoneArraySettableListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        context(assertionScope: AssertionScope)
        override fun validate(list: KoneList<Any>) {
            if (list !is KoneArraySettableList<Any>) fail("The list is invalid")
        }
        context(assertionScope: AssertionScope)
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>
        ) {
            validate(list)
            list as KoneArraySettableList<Any>
            
            if (iterator !is KoneArraySettableList.Iterator<Any>) {
                fail("The iterator is invalid")
                return
            }
            if (iterator.data.array !== list.data.array) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}