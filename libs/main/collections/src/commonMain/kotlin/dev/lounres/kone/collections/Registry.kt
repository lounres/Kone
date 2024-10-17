/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistry<out E> {
    public val size: UInt
    public val registrationsView: KoneSet<KoneRegistration<E>>
    public val elementsView: KoneCollection<E>
}

public interface KoneExtendableRegistry<E> : KoneRegistry<E> {
    public fun register(element: E): KoneRegistration<E>
    // TODO: Think about such possible analogues:
//    public fun registerSeveral(number: UInt, builder: (index: UInt) -> E)
//    public fun registerAllFrom(elements: KoneIterableCollection<E>)
}

public interface KoneRemovableRegistry<out E> : KoneRegistry<E> {
    override val registrationsView: KoneSet<KoneRemovableRegistration<E>>
}

public interface KoneChangeableRegistry<E> : KoneRegistry<E> {
    override val registrationsView: KoneSet<KoneChangeableRegistration<E>>
}

public interface KoneMutableRegistry<E> : KoneExtendableRegistry<E>, KoneRemovableRegistry<E>, KoneChangeableRegistry<E> {
    override val registrationsView: KoneSet<KoneMutableRegistration<E>>
    override fun register(element: E): KoneMutableRegistration<E>
}

public interface KoneIterableRegistry<out E> : KoneRegistry<E> {
    override val registrationsView: KoneIterableSet<KoneRegistration<E>>
    override val elementsView: KoneIterableCollection<E>
}

public interface KoneExtendableIterableRegistry<E> : KoneIterableRegistry<E>, KoneExtendableRegistry<E>

public interface KoneRemovableIterableRegistry<out E> : KoneIterableRegistry<E>, KoneRemovableRegistry<E> {
    override val registrationsView: KoneIterableSet<KoneRemovableRegistration<E>>
}

public interface KoneChangeableIterableRegistry<E> : KoneIterableRegistry<E>, KoneChangeableRegistry<E> {
    override val registrationsView: KoneIterableSet<KoneChangeableRegistration<E>>
}

public interface KoneMutableIterableRegistry<E> : KoneExtendableIterableRegistry<E>, KoneRemovableIterableRegistry<E>, KoneChangeableIterableRegistry<E>, KoneMutableRegistry<E> {
    override val registrationsView: KoneIterableSet<KoneMutableRegistration<E>>
}