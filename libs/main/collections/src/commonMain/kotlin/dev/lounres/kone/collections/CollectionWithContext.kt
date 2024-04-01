/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


public interface KoneCollectionWithContext<E, out EC: Equality<E>> : KoneCollection<E> {
    public val elementContext: EC
}

public interface KoneExtendableCollectionWithContext<E, out EC: Equality<E>> : KoneExtendableCollection<E>, KoneCollectionWithContext<E, EC>

public interface KoneRemovableCollectionWithContext<E, out EC: Equality<E>> : KoneRemovableCollection<E>, KoneCollectionWithContext<E, EC>

public interface KoneMutableCollectionWithContext<E, out EC: Equality<E>> : KoneMutableCollection<E>, KoneExtendableCollectionWithContext<E, EC>, KoneRemovableCollectionWithContext<E, EC>

public interface KoneListWithContext<E, out EC: Equality<E>> : KoneList<E>, KoneCollectionWithContext<E, EC> {
    override fun contains(element: @UnsafeVariance E): Boolean =
        indexThat { _, currentElement -> elementContext { currentElement eq element } } != size

    override fun indexOf(element: @UnsafeVariance E): UInt =
        indexThat { _, currentElement -> elementContext { currentElement eq element } }

    override fun lastIndexOf(element: @UnsafeVariance E): UInt =
        lastIndexThat { _, currentElement -> elementContext { currentElement eq element } }
}

public interface KoneSettableListWithContext<E, out EC: Equality<E>> : KoneSettableList<E>, KoneListWithContext<E, EC>

public interface KoneExtendableListWithContext<E, out EC: Equality<E>> : KoneExtendableList<E>, KoneListWithContext<E, EC>, KoneExtendableCollectionWithContext<E, EC>

public interface KoneRemovableListWithContext<E, out EC: Equality<E>> : KoneRemovableList<E>, KoneListWithContext<E, EC>, KoneRemovableCollectionWithContext<E, EC>

public interface KoneMutableListWithContext<E, out EC: Equality<E>> : KoneMutableList<E>, KoneSettableListWithContext<E, EC>, KoneExtendableListWithContext<E, EC>, KoneRemovableListWithContext<E, EC>,
    KoneMutableCollectionWithContext<E, EC>

public interface KoneSetWithContext<E, out EC: Equality<E>> : KoneSet<E>, KoneCollectionWithContext<E, EC>

public interface KoneExtendableSetWithContext<E, out EC: Equality<E>> : KoneExtendableSet<E>, KoneSetWithContext<E, EC>, KoneExtendableCollectionWithContext<E, EC>

public interface KoneRemovableSetWithContext<E, out EC: Equality<E>> : KoneRemovableSet<E>, KoneSetWithContext<E, EC>, KoneRemovableCollectionWithContext<E, EC>

public interface KoneMutableSetWithContext<E, out EC: Equality<E>> : KoneMutableSet<E>, KoneExtendableSetWithContext<E, EC>, KoneRemovableSetWithContext<E, EC>, KoneMutableCollectionWithContext<E, EC>
