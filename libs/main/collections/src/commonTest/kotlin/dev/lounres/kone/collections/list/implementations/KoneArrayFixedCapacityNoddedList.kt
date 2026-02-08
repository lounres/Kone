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


object KoneArrayFixedCapacityNoddedListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayFixedCapacityNoddedList"
    
    override val listProducer: KoneListProducer get() = KoneArrayFixedCapacityNoddedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayFixedCapacityNoddedList<Any>) fail("The list is invalid")
            if (list.isDisposed) fail("The list is invalid")
            
            val size = list.size
            val data = list.data
            
            if (size > data.size) fail("The list is invalid")
            
            repeat(data.size) { index ->
                if (index < size) {
                    val node = data[index]
                    if (node == null) fail("The list is invalid")
                    if (node.list !== list) fail("The list is invalid")
                    if (node.index != index) fail("The list is invalid")
                } else {
                    if (data[index] != null) fail("The list is invalid")
                }
            }
        }
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            validate(list)
            
            if (iterator !is KoneArrayFixedCapacityNoddedList.Iterator<Any>) fail("The iterator is invalid")
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}