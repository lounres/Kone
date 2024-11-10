/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: Check that exceptions are used correctly

/**
 * Throws [IndexOutOfBoundsException] with message including provided [index] and [size].
 */
public fun indexOutOfBoundsException(index: UInt, size: UInt): Nothing =
    throw IndexOutOfBoundsException("Index $index is out of bounds for length $size")

/**
 * Represents exception that is thrown when user tries to get, set, remove, or bypass the next or the previous non-existent element.
 */
public class NoFollowingElementInIteratorException(message: String = "There is no following element in the iterator") : NoSuchElementException(message)

/**
 * Throws [NoFollowingElementInIteratorException] with the provided [message] describing that there is no next element.
 */
public fun noNextElementInIteratorException(message: String = "There is no next element in the iterator"): Nothing =
    throw NoFollowingElementInIteratorException(message)

/**
 * Throws [NoFollowingElementInIteratorException] with the provided [message] describing that there is no previous element.
 */
public fun noPreviousElementInIteratorException(message: String = "There is no previous element in the iterator"): Nothing =
    throw NoFollowingElementInIteratorException(message)

/**
 * Represents exception that is thrown when user tries to get element that matches the provided predicate but there is no such element.
 */
public class NoElementMatchingThePredicateException(message: String = "There is no element matching the predicate.") : NoSuchElementException(message)

/**
 * Throws [NoElementMatchingThePredicateException] with the provided [message] describing that there is no element matching the predicate.
 */
public fun noElementMatchingThePredicateException(message: String = "There is no element matching the predicate."): Nothing =
    throw NoElementMatchingThePredicateException(message)

/**
 * Represents exception that is thrown when user tries to overflow a data structure with fixed capacity.
 */
public class CapacityOverflowException(message: String = "Overflow of collection with fixed capacity") : RuntimeException(message)

/**
 * Throws [CapacityOverflowException] with message including provided [capacity].
 */
public fun capacityOverflowException(capacity: UInt): Nothing =
    throw CapacityOverflowException("Overflow of collection with fixed capacity of $capacity")

/**
 * Represents exception that is thrown when user tries to get some data structure's element that matches the provided key but there is no such element.
 */
public class NoMatchingKeyException(message: String = "There is no value for requested key"): NoSuchElementException(message)

/**
 * Throws [NoMatchingKeyException] with message including provided [key].
 */
public fun noMatchingKeyException(key: Any?): Nothing =
    throw NoMatchingKeyException("There is no value for key $key")

/**
 * Represents exception that is thrown when user tries to change detached node's properties.
 */
public class DetachedNodeException(message: String = "The node is already detached and the operation is undefined.") : IllegalStateException(message)

/**
 * Throws [DetachedNodeException] with the provided [message] describing
 * that the node is already detached and doesn't support used operation.
 */
public fun detachedNodeException(message: String = "The node is already detached and the operation is undefined."): Nothing =
    throw DetachedNodeException(message)

/**
 * Represents exception that is thrown when user tries to use disposed object.
 */
public class DisposedInstanceException(message: String = "The object is already disposed and the operation is undefined.") : IllegalStateException(message)

/**
 * Throws [DisposedInstanceException] with the provided [message] describing
 * that the object is already disposed and doesn't support used operation.
 */
public fun disposedInstanceException(message: String = "The object is already disposed and the operation is undefined."): Nothing =
    throw DisposedInstanceException(message)