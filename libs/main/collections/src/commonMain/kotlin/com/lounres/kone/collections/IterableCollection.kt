/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections


public interface KoneIterableCollection<out E> : KoneCollection<E>, KoneIterable<E>

public interface KoneIterableExtendableCollection<E> : KoneIterableCollection<E>, KoneExtendableIterable<E>

public interface KoneIterableRemovableCollection<out E> : KoneIterableCollection<E>, KoneRemovableCollection<E>

public interface KoneIterableMutableCollection<E> : KoneIterableExtendableCollection<E>, KoneIterableRemovableCollection<E>, KoneMutableIterable<E>

public interface KoneIterableList<out E> : KoneIterableCollection<E>, KoneLinearIterable<E>

public interface KoneIterableMutableList<E> : KoneIterableList<E>, KoneIterableMutableCollection<E>, KoneMutableLinearIterable<E>

public interface KoneIterableSet<out E> : KoneSet<E>, KoneIterableCollection<E>

public interface KoneIterableMutableSet<E> : KoneMutableSet<E>, KoneIterableSet<E>, KoneIterableRemovableCollection<E>