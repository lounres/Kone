/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistry<out E> {
    public val size: UInt
    
    public val registrationsView: KoneIterable<KoneRegistration<E>>
    public val elementsView: KoneIterable<E>
}

public interface KoneExtendableRegistry<E> : KoneRegistry<E> {
    public fun register(element: E): KoneRegistration<E>
    // TODO: Think about such possible analogues:
//    public fun registerSeveral(number: UInt, builder: (index: UInt) -> E)
//    public fun registerAllFrom(elements: KoneIterableCollection<E>)
}

public interface KoneRemovableRegistry<out E> : KoneRegistry<E> {
    override val registrationsView: KoneIterable<KoneRemovableRegistration<E>>
}

public interface KoneChangeableRegistry<E> : KoneRegistry<E> {
    override val registrationsView: KoneIterable<KoneChangeableRegistration<E>>
}

public interface KoneMutableRegistry<E> : KoneExtendableRegistry<E>, KoneRemovableRegistry<E>, KoneChangeableRegistry<E> {
    override val registrationsView: KoneIterable<KoneMutableRegistration<E>>
}