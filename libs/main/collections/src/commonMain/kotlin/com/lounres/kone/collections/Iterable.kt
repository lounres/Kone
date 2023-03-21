/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections

public interface KoneIterable<out E> {
    public operator fun iterator(): KoneIterator<E>
}

public interface KoneExtendableIterable<E> : KoneIterable<E> {
    public override operator fun iterator(): KoneExtendableIterator<E>
}

public interface KoneRemovableIterable<out E> : KoneIterable<E> {
    public override operator fun iterator(): KoneRemovableIterator<E>
}

public interface KoneMutableIterable<E> : KoneRemovableIterable<E>, KoneExtendableIterable<E> {
    public override operator fun iterator(): KoneMutableIterator<E>
}

public interface KoneLinearIterable<out E> : KoneIterable<E> {
    public override operator fun iterator(): KoneLinearIterator<E>
}

public interface KoneExtendableLinearIterable<E> : KoneLinearIterable<E>, KoneExtendableIterable<E> {
    public override operator fun iterator(): KoneExtendableLinearIterator<E>
}

public interface KoneRemovableLinearIterable<out E> : KoneLinearIterable<E>, KoneRemovableIterable<E> {
    public override operator fun iterator(): KoneRemovableLinearIterator<E>
}

public interface KoneMutableLinearIterable<E> : KoneRemovableLinearIterable<E>, KoneExtendableLinearIterable<E>, KoneMutableIterable<E> {
    public override operator fun iterator(): KoneMutableLinearIterator<E>
}