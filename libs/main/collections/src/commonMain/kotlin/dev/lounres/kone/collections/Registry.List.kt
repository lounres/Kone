/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneListRegistry<out Element> : KoneRegistry<Element> {
    override val registrationsView: KoneIterable<KoneRegistration<Element>>
    override val elementsView: KoneList<Element>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneRegistration<E>>
}

public interface KoneExtendableListRegistry<Element> : KoneListRegistry<Element>, KoneExtendableRegistry<Element>

public interface KoneRemovableListRegistry<out Element> : KoneListRegistry<Element>, KoneRemovableRegistry<Element> {
    override val registrationsView: KoneIterable<KoneRemovableRegistration<Element>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneRemovableRegistration<E>>
}

public interface KoneChangeableListRegistry<Element> : KoneListRegistry<Element>, KoneChangeableRegistry<Element> {
    override val registrationsView: KoneIterable<KoneChangeableRegistration<Element>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneChangeableRegistration<E>>
}

public interface KoneMutableListRegistry<Element> : KoneExtendableListRegistry<Element>, KoneRemovableListRegistry<Element>, KoneChangeableListRegistry<Element>, KoneMutableRegistry<Element> {
    override val registrationsView: KoneIterable<KoneMutableRegistration<Element>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneMutableRegistration<E>>
}