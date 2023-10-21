/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterable<out E> {
    public operator fun iterator(): KoneIterator<E>
}

public interface KoneReversibleIterable<out E>: KoneIterable<E> {
    public override operator fun iterator(): KoneReversibleIterator<E>
}

public interface KoneSettableIterable<E> : KoneIterable<E> {
    public override operator fun iterator(): KoneSettableIterator<E>
}

public interface KoneReversibleSettableIterable<E> : KoneReversibleIterable<E>, KoneSettableIterable<E> {
    public override operator fun iterator(): KoneReversibleSettableIterator<E>
}

public interface KoneExtendableIterable<E> : KoneIterable<E> {
    public override operator fun iterator(): KoneExtendableIterator<E>
}

public interface KoneReversibleExtendableIterable<E> : KoneReversibleIterable<E>, KoneExtendableIterable<E> {
    public override operator fun iterator(): KoneReversibleExtendableIterator<E>
}

public interface KoneRemovableIterable<out E> : KoneIterable<E> {
    public override operator fun iterator(): KoneRemovableIterator<E>
}

public interface KoneReversibleRemovableIterable<out E> : KoneReversibleIterable<E>, KoneRemovableIterable<E> {
    public override operator fun iterator(): KoneReversibleRemovableIterator<E>
}

public interface KoneMutableIterable<E> : KoneSettableIterable<E>, KoneExtendableIterable<E>, KoneRemovableIterable<E> {
    public override operator fun iterator(): KoneMutableIterator<E>
}

public interface KoneReversibleMutableIterable<E> : KoneMutableIterable<E>, KoneReversibleSettableIterable<E>, KoneReversibleExtendableIterable<E>, KoneReversibleRemovableIterable<E> {
    public override operator fun iterator(): KoneReversibleMutableIterator<E>
}

public interface KoneLinearIterable<out E> : KoneReversibleIterable<E> {
    public override operator fun iterator(): KoneLinearIterator<E>
}

public interface KoneSettableLinearIterable<E> : KoneLinearIterable<E>, KoneReversibleSettableIterable<E> {
    public override operator fun iterator(): KoneSettableLinearIterator<E>
}

public interface KoneExtendableLinearIterable<E> : KoneLinearIterable<E>, KoneReversibleExtendableIterable<E> {
    public override operator fun iterator(): KoneExtendableLinearIterator<E>
}

public interface KoneRemovableLinearIterable<out E> : KoneLinearIterable<E>, KoneReversibleRemovableIterable<E> {
    public override operator fun iterator(): KoneRemovableLinearIterator<E>
}

public interface KoneMutableLinearIterable<E> : KoneSettableLinearIterable<E>, KoneExtendableLinearIterable<E>, KoneRemovableLinearIterable<E>, KoneReversibleMutableIterable<E> {
    public override operator fun iterator(): KoneMutableLinearIterator<E>
}