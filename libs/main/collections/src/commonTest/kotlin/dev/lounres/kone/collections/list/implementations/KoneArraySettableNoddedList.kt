/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.repeat
import kotlin.test.fail


object KoneArraySettableNoddedListDescription : ListImplementationDescription {
    override val name get() = "KoneArraySettableNoddedList"
    
    override val listProducer: KoneListProducer get() = KoneArraySettableNoddedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(list: KoneList<Any>) {
            if (list !is KoneArraySettableNoddedList<Any>) fail("The list is invalid")
            if (list.isDisposed) fail("The list is invalid")
            
            val data = list.data
            
            repeat(data.size) { index ->
                val currentNodeOrNull = data[index]
                if (currentNodeOrNull == null) fail("The list is invalid")
                if (currentNodeOrNull.index != index) fail("The list is invalid")
                if (currentNodeOrNull.isDetached) fail("The list is invalid")
                if (currentNodeOrNull.list !== list) fail("The list is invalid")
            }
        }
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>
        ) {
            validate(list)
            list as KoneArraySettableNoddedList<Any>
            
            if (iterator !is KoneArraySettableNoddedList.Iterator<Any>) fail("The iterator is invalid")
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}