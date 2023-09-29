/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterableCollection<out E> : KoneCollection<E>, KoneIterable<E>

public interface KoneExtendableIterableCollection<E> : KoneIterableCollection<E>, KoneExtendableIterable<E>,
    KoneExtendableCollection<E>

public interface KoneRemovableIterableCollection<out E> : KoneIterableCollection<E>, KoneRemovableIterable<E>,
    KoneRemovableCollection<E>

public interface KoneMutableIterableCollection<E> : KoneExtendableIterableCollection<E>,
    KoneRemovableIterableCollection<E>, KoneMutableIterable<E>

public interface KoneIterableList<out E> : KoneList<E>, KoneIterableCollection<E>, KoneLinearIterable<E>

public interface KoneSettableIterableList<E> : KoneIterableList<E>, KoneSettableList<E>, KoneSettableLinearIterable<E>

public interface KoneMutableIterableList<E> : KoneMutableList<E>, KoneSettableIterableList<E>,
    KoneMutableIterableCollection<E>, KoneMutableLinearIterable<E>

public interface KoneIterableSet<out E> : KoneSet<E>, KoneIterableCollection<E>

public interface KoneMutableIterableSet<E> : KoneMutableSet<E>, KoneIterableSet<E>, KoneRemovableIterableCollection<E>