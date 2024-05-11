/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistry<out E> {
    public val elements: KoneCollection<E>
    public val registrations: KoneSet<KoneRegistration<E>>
}

public interface KoneExtendableRegistry<E> : KoneRegistry<E> {
    public fun register(element: E): KoneRegistration<E>
    // TODO: Think about such possible analogues:
//    public fun registerSeveral(number: UInt, builder: (index: UInt) -> E)
//    public fun registerAllFrom(elements: KoneIterableCollection<E>)
}

public interface KoneRemovableRegistry<out E> : KoneRegistry<E> {
    override val registrations: KoneSet<KoneRemovableRegistration<E>>
}

public interface KoneChangeableRegistry<E> : KoneRegistry<E> {
    override val registrations: KoneSet<KoneChangeableRegistration<E>>
}

public interface KoneMutableRegistry<E> : KoneExtendableRegistry<E>, KoneRemovableRegistry<E>, KoneChangeableRegistry<E> {
    override val registrations: KoneSet<KoneMutableRegistration<E>>
    override fun register(element: E): KoneMutableRegistration<E>
}

public interface KoneIterableRegistry<out E> : KoneRegistry<E> {
        override val registrations: KoneIterableSet<KoneRegistration<E>>
}

public interface KoneExtendableIterableRegistry<E> : KoneIterableRegistry<E>, KoneExtendableRegistry<E>

public interface KoneRemovableIterableRegistry<out E> : KoneIterableRegistry<E>, KoneRemovableRegistry<E> {
    override val registrations: KoneIterableSet<KoneRemovableRegistration<E>>
}

public interface KoneChangeableIterableRegistry<E> : KoneIterableRegistry<E>, KoneChangeableRegistry<E> {
    override val registrations: KoneIterableSet<KoneChangeableRegistration<E>>
}

public interface KoneMutableIterableRegistry<E> : KoneExtendableIterableRegistry<E>, KoneRemovableIterableRegistry<E>, KoneChangeableIterableRegistry<E>, KoneMutableRegistry<E> {
    override val registrations: KoneIterableSet<KoneMutableRegistration<E>>
}