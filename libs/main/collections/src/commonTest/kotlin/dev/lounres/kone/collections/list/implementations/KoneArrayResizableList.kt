/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.repeat


object KoneArrayResizableListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayResizableList"
    
    override val listProducer: KoneListProducer get() = KoneArrayResizableListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        context(assertionScope: AssertionScope)
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayResizableList<Any>) {
                fail("The list is invalid")
                return
            }
            if (list.isDisposed) fail("The list is invalid")
            
            val dataSizeNumber = list.dataSizeNumber
            val sizeLowerBound = list.sizeLowerBound
            val sizeUpperBound = list.sizeUpperBound
            val size = list.size
            val data = list.data
            
            if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
            if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
            if (size > sizeUpperBound) fail("The list is invalid")
            if (data.size != sizeUpperBound) fail("The list is invalid")
            
            repeat(sizeUpperBound) { index ->
                if ((index < size) != (data[index] != null)) fail("The list is invalid")
            }
        }
        
        context(assertionScope: AssertionScope)
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            validate(list)
            
            if (iterator !is KoneArrayResizableList.Iterator<Any>) {
                fail("The iterator is invalid")
                return
            }
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}