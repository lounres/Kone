/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList
import dev.lounres.kone.option.Option
import org.jetbrains.kotlinx.lincheck.RandomProvider
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.paramgen.ParameterGenerator
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.random.asKotlinRandom
import kotlin.random.nextUInt
import kotlin.test.Ignore
import kotlin.test.Test


class KoneIterableCollectionParameterGenerator(randomProvider: RandomProvider, configuration: String): ParameterGenerator<KoneIterableCollection<Int>> {
    private val random = randomProvider.createRandom().asKotlinRandom()
    override fun generate(): KoneIterableCollection<Int> = KoneIterableList(random.nextUInt(8u, 128u)) { random.nextInt() }
}

class ElementBuilderParameterGenerator(randomProvider: RandomProvider, configuration: String): ParameterGenerator<(UInt) -> Int> {
    private val random = randomProvider.createRandom().asKotlinRandom()
    private val randomGenerator: (UInt) -> Int = { random.nextInt() }
    override fun generate(): (UInt) -> Int = randomGenerator
}

class ElementPredicateParameterGenerator(randomProvider: RandomProvider, configuration: String): ParameterGenerator<(Int) -> Boolean> {
    private val random = randomProvider.createRandom().asKotlinRandom()
    private val randomGenerator: (Int) -> Boolean = { random.nextBoolean() }
    override fun generate(): (Int) -> Boolean = randomGenerator
}

class ElementIndexedPredicateParameterGenerator(randomProvider: RandomProvider, configuration: String): ParameterGenerator<(UInt, Int) -> Boolean> {
    private val random = randomProvider.createRandom().asKotlinRandom()
    private val randomGenerator: (UInt, Int) -> Boolean = { _, _ -> random.nextBoolean() }
    override fun generate(): (UInt, Int) -> Boolean = randomGenerator
}

@Param(name = "elements", gen = KoneIterableCollectionParameterGenerator::class)
@Param(name = "elementsNumber", gen = IntGen::class, conf = "8:128")
@Param(name = "elementsBuilder", gen = ElementBuilderParameterGenerator::class)
@Param(name = "elementsPredicate", gen = ElementPredicateParameterGenerator::class)
@Param(name = "elementsIndexedPredicate", gen = ElementIndexedPredicateParameterGenerator::class)
class KoneLockingMutableListWrapperWithKoneResizableLinkedArrayListConcurrencyTest {
    private val list = KoneLockingMutableListWrapper<Int>(KoneResizableLinkedArrayList())

    @Operation
    fun getSize(): UInt = list.size

    @Operation
    fun get(index: UInt): Int = list.get(index)
    @Operation
    fun getMaybe(index: UInt): Option<Int> = list.getMaybe(index)
    @Operation
    fun contains(element: Int): Boolean = list.contains(element)
    @Operation
    fun indexOf(element: Int): UInt = list.indexOf(element)
    @Operation
    fun indexThat(@Param(name = "elementsIndexedPredicate") predicate: (index: UInt, element: Int) -> Boolean): UInt = list.indexThat(predicate)
    @Operation
    fun lastIndexOf(element: Int): UInt = list.lastIndexOf(element)
    @Operation
    fun lastIndexThat(@Param(name = "elementsIndexedPredicate") predicate: (index: UInt, element: Int) -> Boolean): UInt = list.lastIndexThat(predicate)

    @Operation
    fun set(index: UInt, element: Int) {
        list.set(index, element)
    }

    @Operation
    fun add(element: Int) {
        list.add(element)
    }
    @Operation
    fun addAt(index: UInt, element: Int) {
        list.addAt(index, element)
    }
    @Operation
    fun addSeveral(@Param(name = "elementsNumber") number: UInt, @Param(name = "elementsBuilder") builder: (UInt) -> Int) {
        list.addSeveral(number, builder)
    }
    @Operation
    fun addSeveralAt(@Param(name = "elementsNumber") number: UInt, index: UInt, @Param(name = "elementsBuilder") builder: (UInt) -> Int) {
        list.addSeveralAt(number, index, builder)
    }
    @Operation
    fun addAllFrom(@Param(name = "elements") elements: KoneIterableCollection<Int>) {
        list.addAllFrom(elements)
    }
    @Operation
    fun addAllFromAt(index: UInt, @Param(name = "elements") elements: KoneIterableCollection<Int>) {
        list.addAllFromAt(index, elements)
    }

    @Operation
    fun remove(element: Int) {
        list.remove(element)
    }
    @Operation
    fun removeAt(index: UInt) {
        list.removeAt(index)
    }
    @Operation
    fun removeAllThat(@Param(name = "elementsPredicate") predicate: (element: Int) -> Boolean) {
        list.removeAllThat(predicate)
    }
    @Operation
    fun removeAllThatIndexed(@Param(name = "elementsPredicate") predicate: (index: UInt, element: Int) -> Boolean) {
        list.removeAllThatIndexed(predicate)
    }
    @Operation
    fun removeAll() {
        list.removeAll()
    }

    @Test
    @Ignore
    fun stressTest() {
        StressOptions()
            .check(this::class)
    }

    @Test
    @Ignore
    fun modelCheckingTest() {
        ModelCheckingOptions()
            .check(this::class)
    }
}