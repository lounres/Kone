/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneListRegistry<out E> : KoneRegistry<E> {
    override val elements: KoneList<E>
    public fun find(value: @UnsafeVariance E): KoneIterableList<KoneRegistration<E>>
}

public interface KoneExtendableListRegistry<E> : KoneListRegistry<E>, KoneExtendableRegistry<E> {
    // TODO: Think about such possible analogue:
//    public fun registerAt(index: UInt, element: E): KoneRegistration<E>
}

public interface KoneRemovableListRegistry<out E> : KoneListRegistry<E>, KoneRemovableRegistry<E> {
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneRemovableRegistration<E>>
}

public interface KoneChangeableListRegistry<E> : KoneListRegistry<E>, KoneChangeableRegistry<E> {
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneChangeableRegistration<E>>
}

public interface KoneMutableListRegistry<E> : KoneExtendableListRegistry<E>, KoneRemovableListRegistry<E>, KoneChangeableListRegistry<E>, KoneMutableRegistry<E> {
    override fun find(value: @UnsafeVariance E): KoneIterableList<KoneMutableRegistration<E>>
}

public interface KoneIterableListRegistry<out E> : KoneListRegistry<E>, KoneIterableRegistry<E> {
    override val elements: KoneIterableList<E>
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneRegistration<E>>
}

public interface KoneExtendableIterableListRegistry<E> : KoneIterableListRegistry<E>, KoneExtendableListRegistry<E>, KoneExtendableIterableRegistry<E>

public interface KoneRemovableIterableListRegistry<out E> : KoneIterableListRegistry<E>, KoneRemovableListRegistry<E>, KoneRemovableIterableRegistry<E> {
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneRemovableRegistration<E>>
}

public interface KoneChangeableIterableListRegistry<E> : KoneIterableListRegistry<E>, KoneChangeableListRegistry<E>, KoneChangeableIterableRegistry<E> {
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneChangeableRegistration<E>>
}

public interface KoneMutableIterableListRegistry<E> : KoneExtendableIterableListRegistry<E>, KoneRemovableIterableListRegistry<E>, KoneChangeableIterableListRegistry<E>, KoneMutableListRegistry<E>, KoneMutableIterableRegistry<E> {
    override fun find(element: @UnsafeVariance E): KoneIterableList<KoneMutableRegistration<E>>
}