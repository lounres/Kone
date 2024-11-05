/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality


public interface KoneSetWithContext<Element, out ElementContext: Equality<Element>> : KoneSet<Element> {
    public val elementContext: ElementContext
}

public interface KoneMutableSetWithContext<Element, out ElementContext: Equality<Element>> : KoneMutableSet<Element>, KoneSetWithContext<Element, ElementContext>

public interface KoneNoddedSetWithContext<Element, out ElementContext: Equality<Element>> : KoneSetWithContext<Element, ElementContext>, KoneNoddedSet<Element>

public interface KoneNoddedMutableSetWithContext<Element, out ElementContext: Equality<Element>> : KoneMutableSetWithContext<Element, ElementContext>, KoneNoddedSetWithContext<Element, ElementContext>, KoneNoddedMutableSet<Element>