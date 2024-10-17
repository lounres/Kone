/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E, EC: Equality<E>> KoneResizableLinkedArrayListRegistry(elementContext: EC): KoneResizableLinkedArrayListRegistry<E, EC> =
    KoneResizableLinkedArrayListRegistry(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableLinkedArrayListRegistry(): KoneResizableLinkedArrayListRegistry<E, Equality<E>> =
    KoneResizableLinkedArrayListRegistry(size = 0u, elementContext = defaultEquality())