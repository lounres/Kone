/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.utils

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.computationalGeometry.Point
import dev.lounres.kone.computationalGeometry.Vector
import dev.lounres.kone.linearAlgebra.experiment1.utils.*


public inline fun <E> Point<E>.forEach(block: (value: E) -> Unit) {
    coordinates.forEach(block)
}

public inline fun <E> Vector<E>.forEach(block: (value: E) -> Unit) {
    coordinates.forEach(block)
}

public inline fun <E> Point<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    coordinates.forEachIndexed(block)
}

public inline fun <E> Vector<E>.forEachIndexed(block: (index: UInt, value: E) -> Unit) {
    coordinates.forEachIndexed(block)
}

public inline fun <E> Point<E>.withEach(block: E.() -> Unit) {
    coordinates.withEach(block)
}

public inline fun <E> Vector<E>.withEach(block: E.() -> Unit) {
    coordinates.withEach(block)
}

public inline fun <E> Point<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    coordinates.withEachIndexed(block)
}

public inline fun <E> Vector<E>.withEachIndexed(block: E.(index: UInt) -> Unit) {
    coordinates.withEachIndexed(block)
}

public inline fun <E> Point<E>.any(block: (value: E) -> Boolean): Boolean = coordinates.any(block)

public inline fun <E> Vector<E>.any(block: (value: E) -> Boolean): Boolean = coordinates.any(block)

public inline fun <E> Point<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.anyIndexed(block)

public inline fun <E> Vector<E>.anyIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.anyIndexed(block)

public inline fun <E> Point<E>.all(block: (value: E) -> Boolean): Boolean = coordinates.all(block)

public inline fun <E> Vector<E>.all(block: (value: E) -> Boolean): Boolean = coordinates.all(block)

public inline fun <E> Point<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.allIndexed(block)

public inline fun <E> Vector<E>.allIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.allIndexed(block)

public inline fun <E> Point<E>.none(block: (value: E) -> Boolean): Boolean = coordinates.none(block)

public inline fun <E> Vector<E>.none(block: (value: E) -> Boolean): Boolean = coordinates.none(block)

public inline fun <E> Point<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.noneIndexed(block)

public inline fun <E> Vector<E>.noneIndexed(block: (index: UInt, value: E) -> Boolean): Boolean = coordinates.noneIndexed(block)

public inline fun <E, R> Point<E>.map(transform: (E) -> R): Point<R> = Point(coordinates.map(transform))

public inline fun <E, R> Point<E>.mapIndexed(transform: (index: UInt, E) -> R): Point<R> = Point(coordinates.mapIndexed(transform))

public inline fun <E, R> Vector<E>.map(transform: (E) -> R): Vector<R> = Vector(coordinates.map(transform))

public inline fun <E, R> Vector<E>.mapIndexed(transform: (index: UInt, E) -> R): Vector<R> = Vector(coordinates.mapIndexed(transform))

public inline fun <E, R> Point<E>.fold(initial: R, operation: (acc: R, E) -> R): R = coordinates.fold(initial, operation)

public inline fun <E, R> Vector<E>.fold(initial: R, operation: (acc: R, E) -> R): R = coordinates.fold(initial, operation)

public inline fun <E, R> Point<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R = coordinates.foldIndexed(initial, operation)

public inline fun <E, R> Vector<E>.foldIndexed(initial: R, operation: (index: UInt, acc: R, E) -> R): R = coordinates.foldIndexed(initial, operation)

// TODO: Add `reduce`-like extensions

context(Ring<E>)
public fun <E> Point<E>.sum(): E = coordinates.sum()

context(Ring<E>)
public fun <E> Vector<E>.sum(): E = coordinates.sum()

context(Ring<A>)
public inline fun <E, A> Point<E>.sumOf(selector: (E) -> A): A = coordinates.sumOf(selector)

context(Ring<A>)
public inline fun <E, A> Vector<E>.sumOf(selector: (E) -> A): A = coordinates.sumOf(selector)

context(Ring<A>)
public inline fun <E, A> Point<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = coordinates.sumOfIndexed(selector)

context(Ring<A>)
public inline fun <E, A> Vector<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = coordinates.sumOfIndexed(selector)

// TODO: Add bulk operations