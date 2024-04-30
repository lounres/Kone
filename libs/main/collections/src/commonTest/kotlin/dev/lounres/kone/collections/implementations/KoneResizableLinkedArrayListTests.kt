/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import kotlin.test.Test
import kotlin.test.assertEquals


class KoneResizableLinkedArrayListTests {
    @Test
    fun `iterator addNext 1`() {
        val list = KoneResizableLinkedArrayList<Int>(0u) { it.toInt() }

        assertEquals(0u, list.size, "Initial size mismatch")
        assertEquals("[]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            addNext(-1)
        }

        assertEquals(1u, list.size, "Initial size mismatch")
        assertEquals("[-1]", list.toString(), "Initial list content mismatch")
    }
    @Test
    fun `iterator addNext 2`() {
        val list = KoneResizableLinkedArrayList<Int>(1u) { it.toInt() }

        assertEquals(1u, list.size, "Initial size mismatch")
        assertEquals("[0]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            addNext(-1)
        }

        assertEquals(2u, list.size, "Initial size mismatch")
        assertEquals("[-1, 0]", list.toString(), "Initial list content mismatch")
    }
    @Test
    fun `iterator addNext 3`() {
        val list = KoneResizableLinkedArrayList<Int>(1u) { it.toInt() }

        assertEquals(1u, list.size, "Initial size mismatch")
        assertEquals("[0]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            moveNext()
            addNext(-1)
        }

        assertEquals(2u, list.size, "Initial size mismatch")
        assertEquals("[0, -1]", list.toString(), "Initial list content mismatch")
    }
    @Test
    fun `iterator addNext 4`() {
        val list = KoneResizableLinkedArrayList<Int>(2u) { it.toInt() }

        assertEquals(2u, list.size, "Initial size mismatch")
        assertEquals("[0, 1]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            addNext(-1)
        }

        assertEquals(3u, list.size, "Initial size mismatch")
        assertEquals("[-1, 0, 1]", list.toString(), "Initial list content mismatch")
    }
    @Test
    fun `iterator addNext 5`() {
        val list = KoneResizableLinkedArrayList<Int>(2u) { it.toInt() }

        assertEquals(2u, list.size, "Initial size mismatch")
        assertEquals("[0, 1]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            moveNext()
            addNext(-1)
        }

        assertEquals(3u, list.size, "Initial size mismatch")
        assertEquals("[0, -1, 1]", list.toString(), "Initial list content mismatch")
    }
    @Test
    fun `iterator addNext 6`() {
        val list = KoneResizableLinkedArrayList<Int>(2u) { it.toInt() }

        assertEquals(2u, list.size, "Initial size mismatch")
        assertEquals("[0, 1]", list.toString(), "Initial list content mismatch")

        list.iterator().apply {
            moveNext()
            moveNext()
            addNext(-1)
        }

        assertEquals(3u, list.size, "Initial size mismatch")
        assertEquals("[0, 1, -1]", list.toString(), "Initial list content mismatch")
    }
}