/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterable<out Element> {
    public val size: UInt
    public operator fun iterator(): KoneIterator<Element>
}

public interface KoneReversibleIterable<out Element>: KoneIterable<Element> {
    public override operator fun iterator(): KoneReversibleIterator<Element>
}

public interface KoneSettableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneSettableIterator<Element>
}

public interface KoneReversibleSettableIterable<Element> : KoneReversibleIterable<Element>, KoneSettableIterable<Element> {
    public override operator fun iterator(): KoneReversibleSettableIterator<Element>
}

public interface KoneExtendableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneExtendableIterator<Element>
}

public interface KoneReversibleExtendableIterable<Element> : KoneReversibleIterable<Element>, KoneExtendableIterable<Element> {
    public override operator fun iterator(): KoneReversibleExtendableIterator<Element>
}

public interface KoneRemovableIterable<out Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneRemovableIterator<Element>
}

public interface KoneReversibleRemovableIterable<out Element> : KoneReversibleIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleRemovableIterator<Element>
}

public interface KoneMutableIterable<Element> : KoneSettableIterable<Element>, KoneExtendableIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneMutableIterator<Element>
}

public interface KoneReversibleMutableIterable<Element> : KoneMutableIterable<Element>, KoneReversibleSettableIterable<Element>, KoneReversibleExtendableIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleMutableIterator<Element>
}

public interface KoneLinearIterable<out Element> : KoneReversibleIterable<Element> {
    public override operator fun iterator(): KoneLinearIterator<Element>
}

public interface KoneSettableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleSettableIterable<Element> {
    public override operator fun iterator(): KoneSettableLinearIterator<Element>
}

public interface KoneExtendableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleExtendableIterable<Element> {
    public override operator fun iterator(): KoneExtendableLinearIterator<Element>
}

public interface KoneRemovableLinearIterable<out Element> : KoneLinearIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneRemovableLinearIterator<Element>
}

public interface KoneMutableLinearIterable<Element> : KoneSettableLinearIterable<Element>, KoneExtendableLinearIterable<Element>, KoneRemovableLinearIterable<Element>, KoneReversibleMutableIterable<Element> {
    public override operator fun iterator(): KoneMutableLinearIterator<Element>
}