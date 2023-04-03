/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections


public interface KoneIterableCollection<out E> : KoneCollection<E>, KoneIterable<E>

public interface KoneExtendableIterableCollection<E> : KoneIterableCollection<E>, KoneExtendableIterable<E>, KoneExtendableCollection<E>

public interface KoneRemovableIterableCollection<out E> : KoneIterableCollection<E>, KoneRemovableIterable<E>, KoneRemovableCollection<E>

public interface KoneMutableIterableCollection<E> : KoneExtendableIterableCollection<E>, KoneRemovableIterableCollection<E>, KoneMutableIterable<E>

public interface KoneIterableList<out E> : KoneList<E>, KoneIterableCollection<E>, KoneLinearIterable<E>

public interface KoneMutableIterableList<E> : KoneMutableList<E>, KoneIterableList<E>, KoneMutableIterableCollection<E>, KoneMutableLinearIterable<E>

public interface KoneIterableSet<out E> : KoneSet<E>, KoneIterableCollection<E>

public interface KoneMutableIterableSet<E> : KoneMutableSet<E>, KoneIterableSet<E>, KoneRemovableIterableCollection<E>