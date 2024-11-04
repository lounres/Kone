/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneListRegistry<out E> : KoneRegistry<E> {
    override val registrationsView: KoneIterable<KoneRegistration<E>>
    override val elementsView: KoneList<E>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneRegistration<E>>
}

public interface KoneExtendableListRegistry<E> : KoneListRegistry<E>, KoneExtendableRegistry<E>

public interface KoneRemovableListRegistry<out E> : KoneListRegistry<E>, KoneRemovableRegistry<E> {
    override val registrationsView: KoneIterable<KoneRemovableRegistration<E>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneRemovableRegistration<E>>
}

public interface KoneChangeableListRegistry<E> : KoneListRegistry<E>, KoneChangeableRegistry<E> {
    override val registrationsView: KoneIterable<KoneChangeableRegistration<E>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneChangeableRegistration<E>>
}

public interface KoneMutableListRegistry<E> : KoneExtendableListRegistry<E>, KoneRemovableListRegistry<E>, KoneChangeableListRegistry<E>, KoneMutableRegistry<E> {
    override val registrationsView: KoneIterable<KoneMutableRegistration<E>>
//    override fun find(element: @UnsafeVariance E): KoneList<KoneMutableRegistration<E>>
}