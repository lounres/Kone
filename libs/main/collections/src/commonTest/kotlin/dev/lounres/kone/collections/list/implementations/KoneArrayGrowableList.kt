/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.repeat
import kotlin.test.fail


object KoneArrayGrowableListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayGrowableList"
    
    override val listProducer: KoneListProducer get() = KoneArrayGrowableListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayGrowableList<Any>) fail("The list is invalid")
            if (list.isDisposed) fail("The list is invalid")
            
            val sizeUpperBound = list.sizeUpperBound
            val size = list.size
            val data = list.data
            
            if (context(UInt.context) { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
            if (size > sizeUpperBound) fail("The list is invalid")
            if (data.size != sizeUpperBound) fail("The list is invalid")
            
            repeat(sizeUpperBound) { index ->
                if ((index < size) != (data[index] != null)) fail("The list is invalid")
            }
        }
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            validate(list)
            
            if (iterator !is KoneArrayGrowableList.Iterator<Any>) fail("The iterator is invalid")
            if (iterator.list !== list) fail("The iterator is invalid")
            
            if (iterator.currentIndex > list.size) fail("The iterator is invalid")
        }
    }
}