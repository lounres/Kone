/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistry<out Element> {
    public val size: UInt
    
    public val registrationsView: KoneIterable<KoneRegistration<Element>>
    public val elementsView: KoneIterable<Element>
}

public interface KoneExtendableRegistry<Element> : KoneRegistry<Element> {
    public fun register(element: Element): KoneRegistration<Element>
    // TODO: Think about such possible analogues:
//    public fun registerSeveral(number: UInt, builder: (index: UInt) -> E)
//    public fun registerAllFrom(elements: KoneIterableCollection<E>)
}

public interface KoneRemovableRegistry<out Element> : KoneRegistry<Element> {
    override val registrationsView: KoneIterable<KoneRemovableRegistration<Element>>
}

public interface KoneChangeableRegistry<Element> : KoneRegistry<Element> {
    override val registrationsView: KoneIterable<KoneChangeableRegistration<Element>>
}

public interface KoneMutableRegistry<Element> : KoneExtendableRegistry<Element>, KoneRemovableRegistry<Element>, KoneChangeableRegistry<Element> {
    override val registrationsView: KoneIterable<KoneMutableRegistration<Element>>
}