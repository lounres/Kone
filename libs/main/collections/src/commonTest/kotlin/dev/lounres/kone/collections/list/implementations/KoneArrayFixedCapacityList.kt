/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.repeat
import io.kotest.assertions.fail


object KoneArrayFixedCapacityListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayFixedCapacityList"
    
    override val listProducer: KoneListProducer get() = KoneArrayFixedCapacityListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayFixedCapacityList<Any>) fail("The list is invalid")
            if (list.isDisposed) fail("The list is invalid")
            
            val size = list.size
            val data = list.data
            
            if (size > data.size) fail("The list is invalid")
            
            repeat(data.size) { index ->
                if ((index < size) != (data[index] != null)) fail("The list is invalid")
            }
        }
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            validate(list)
            
            if (iterator !is KoneArrayFixedCapacityList.Iterator<Any>) fail("The iterator is invalid")
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}