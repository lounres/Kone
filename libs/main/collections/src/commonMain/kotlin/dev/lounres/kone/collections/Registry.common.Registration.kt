/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneRegistration<out Element> {
    public val element: Element
}

public interface KoneRemovableRegistration<out Element>: KoneRegistration<Element> {
    public fun remove()
}

public interface KoneChangeableRegistration<Element>: KoneRegistration<Element> {
    override var element: Element
}

public interface KoneMutableRegistration<Element>: KoneRemovableRegistration<Element>, KoneChangeableRegistration<Element>