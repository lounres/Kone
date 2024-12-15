/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneReifiedSet<out Element> : KoneSet<@UnsafeVariance Element> {
    override operator fun contains(element: @UnsafeVariance Element): Boolean
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableReifiedSet<Element> : KoneMutableSet<Element>, KoneReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedReifiedSet<out Element> : KoneNoddedSet<@UnsafeVariance Element>, KoneReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedMutableReifiedSet<Element> : KoneNoddedMutableSet<Element>, KoneMutableReifiedSet<Element>, KoneNoddedReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedReifiedSet<out Element> : KoneLinkedSet<@UnsafeVariance Element>, KoneReifiedSet<Element>