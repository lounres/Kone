/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistration<out E> {
    public val element: E
}

public interface KoneRemovableRegistration<out E>: KoneRegistration<E> {
    public fun remove()
}

public interface KoneChangeableRegistration<E>: KoneRegistration<E> {
    override var element: E
}

public interface KoneMutableRegistration<E>: KoneRemovableRegistration<E>, KoneChangeableRegistration<E>