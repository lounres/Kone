/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


//public interface KoneSetRegistry<out E> : KoneRegistry<E> {
//    override val elements: KoneSet<E>
//    public fun find(value: @UnsafeVariance E): KoneRegistration<E>?
//}
//
//public interface KoneExtendableSetRegistry<E> : KoneSetRegistry<E>, KoneExtendableRegistry<E>
//
//public interface KoneRemovableSetRegistry<out E> : KoneSetRegistry<E>, KoneRemovableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneRemovableRegistration<E>?
//}
//
//public interface KoneChangeableSetRegistry<E> : KoneSetRegistry<E>, KoneChangeableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneChangeableRegistration<E>?
//}
//
//public interface KoneMutableSetRegistry<E> : KoneExtendableSetRegistry<E>, KoneRemovableSetRegistry<E>, KoneChangeableSetRegistry<E>, KoneMutableRegistry<E> {
//    override fun find(value: @UnsafeVariance E): KoneMutableRegistration<E>?
//}
//
//public interface KoneIterableSetRegistry<out E> : KoneSetRegistry<E>, KoneIterableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneRegistration<E>?
//}
//
//public interface KoneExtendableIterableSetRegistry<E> : KoneIterableSetRegistry<E>, KoneExtendableSetRegistry<E>, KoneExtendableIterableRegistry<E>
//
//public interface KoneRemovableIterableSetRegistry<out E> : KoneIterableSetRegistry<E>, KoneRemovableSetRegistry<E>, KoneRemovableIterableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneRemovableRegistration<E>?
//}
//
//public interface KoneChangeableIterableSetRegistry<E> : KoneIterableSetRegistry<E>, KoneChangeableSetRegistry<E>, KoneChangeableIterableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneChangeableRegistration<E>?
//}
//
//public interface KoneMutableIterableSetRegistry<E> : KoneExtendableIterableSetRegistry<E>, KoneRemovableIterableSetRegistry<E>, KoneChangeableIterableSetRegistry<E>, KoneMutableSetRegistry<E>, KoneMutableIterableRegistry<E> {
//    override fun find(element: @UnsafeVariance E): KoneMutableRegistration<E>?
//}