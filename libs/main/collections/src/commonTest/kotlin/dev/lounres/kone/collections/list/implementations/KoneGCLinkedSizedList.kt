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
import kotlin.test.fail


object KoneGCLinkedSizedListDescription : ListImplementationDescription {
    override val name get() = "KoneGCLinkedSizedList"
    
    internal object Validator {
        fun <Element: Any> validate(list: KoneGCLinkedSizedList<Element>) {
            if (list.isDisposed) fail("The list is invalid")
            
            val size = list.size
            val start = list.start
            val end = list.end
            
            if (size == 0u) {
                if (start != null || end != null) fail("The list is invalid")
            } else {
                if (start == null || end == null) fail("The list is invalid")
                
                if (start.previousNode != null) fail("The list is invalid")
                
                var index = 0u
                var currentNode = start
                while (currentNode != null) {
                    if (index >= size) fail("The list is invalid")
                    val nextNode = currentNode._nextNode
                    if (nextNode != null && nextNode._previousNode != currentNode) fail("The list is invalid")
                    currentNode = nextNode
                    index++
                }
                if (index != size) fail("The list is invalid")
            }
        }
        
        fun validateWithIterator(
            list: KoneGCLinkedSizedList<Any>,
            iterator: KoneGCLinkedSizedList.Iterator<Any>
        ) {
            validate(list)
            
            if (iterator.list !== list) fail("The iterator is invalid")
            
            val nextNode = iterator.nextNode
            val nextIndex = iterator._nextIndex
            
            if (nextNode != null && nextNode.list !== list) fail("The iterator is invalid")
            if (nextIndex != null && nextIndex != (nextNode?.index ?: list.size)) fail("The iterator is invalid")
        }
    }
    
    override val listProducer: KoneListProducer get() = KoneGCLinkedSizedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneGCLinkedSizedList<Any>) fail("The list is invalid")
            Validator.validate(list)
        }
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            if (list !is KoneGCLinkedSizedList<Any>) fail("The list is invalid")
            if (iterator !is KoneGCLinkedSizedList.Iterator<Any>) fail("The iterator is invalid")
            Validator.validateWithIterator(list, iterator)
        }
    }
}