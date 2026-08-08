/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterable.contains
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.repeat


object KoneArrayGrowableNoddedListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayGrowableNoddedList"
    
    override val listProducer: KoneListProducer get() = KoneArrayGrowableNoddedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        context(assertionScope: AssertionScope)
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayGrowableNoddedList<Any>) {
                fail("The list is invalid")
                return
            }
            if (list.isDisposed) fail("The list is invalid")
            
            val sizeUpperBound = list.sizeUpperBound
            val size = list.size
            val data = list.data
            
            if ((Equality.defaultFor<UInt>()) { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
            if (size > sizeUpperBound) fail("The list is invalid")
            if (data.size != sizeUpperBound) fail("The list is invalid")
            
            repeat(sizeUpperBound) { index ->
                val currentNodeOrNull = data[index]
                if (index < size) {
                    if (currentNodeOrNull == null) fail("The list is invalid")
                    else {
                        if (currentNodeOrNull.index != index) fail("The list is invalid")
                        if (currentNodeOrNull.isDetached) fail("The list is invalid")
                        if (currentNodeOrNull.list !== list) fail("The list is invalid")
                    }
                } else {
                    if (currentNodeOrNull != null) fail("The list is invalid")
                }
            }
        }
        
        context(assertionScope: AssertionScope)
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            validate(list)
            
            if (iterator !is KoneArrayGrowableNoddedList.Iterator<Any>) {
                fail("The iterator is invalid")
                return
            }
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}