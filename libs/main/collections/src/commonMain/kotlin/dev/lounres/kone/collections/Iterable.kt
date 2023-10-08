/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

public interface KoneIterable<out E> {
    public operator fun iterator(): KoneIterator<E>
}

public interface KoneSettableIterable<E> : KoneIterable<E> {
    public override operator fun iterator(): KoneSettableIterator<E>
}

public interface KoneExtendableIterable<E> : KoneIterable<E> {
    public override operator fun iterator(): KoneExtendableIterator<E>
}

public interface KoneRemovableIterable<out E> : KoneIterable<E> {
    public override operator fun iterator(): KoneRemovableIterator<E>
}

public interface KoneMutableIterable<E> : KoneSettableIterable<E>, KoneExtendableIterable<E>, KoneRemovableIterable<E> {
    public override operator fun iterator(): KoneMutableIterator<E>
}

public interface KoneLinearIterable<out E> : KoneIterable<E> {
    public override operator fun iterator(): KoneLinearIterator<E>
}

public interface KoneSettableLinearIterable<E> : KoneLinearIterable<E>, KoneSettableIterable<E> {
    public override operator fun iterator(): KoneSettableLinearIterator<E>
}

public interface KoneExtendableLinearIterable<E> : KoneLinearIterable<E>, KoneExtendableIterable<E> {
    public override operator fun iterator(): KoneExtendableLinearIterator<E>
}

public interface KoneRemovableLinearIterable<out E> : KoneLinearIterable<E>, KoneRemovableIterable<E> {
    public override operator fun iterator(): KoneRemovableLinearIterator<E>
}

public interface KoneMutableLinearIterable<E> : KoneSettableLinearIterable<E>, KoneExtendableLinearIterable<E>, KoneRemovableLinearIterable<E>, KoneMutableIterable<E> {
    public override operator fun iterator(): KoneMutableLinearIterator<E>
}