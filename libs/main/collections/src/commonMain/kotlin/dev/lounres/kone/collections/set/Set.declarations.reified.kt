/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI


@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneReifiedSet<out Element> : KoneSet<@UnsafeVariance Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableReifiedSet<Element> : KoneMutableSet<Element>, KoneReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedReifiedSet<out Element> : KoneNoddedSet<@UnsafeVariance Element>, KoneReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedReifiedSet<Element> : KoneMutableNoddedSet<Element>, KoneMutableReifiedSet<Element>, KoneNoddedReifiedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedReifiedSet<out Element> : KoneReifiedSet<Element>, KoneLinkedSet<@UnsafeVariance Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedReifiedSet<Element> : KoneLinkedReifiedSet<Element>, KoneMutableReifiedSet<Element>, KoneMutableLinkedSet<Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedNoddedReifiedSet<out Element> : KoneNoddedReifiedSet<Element>, KoneLinkedReifiedSet<Element>, KoneLinkedNoddedSet<@UnsafeVariance Element>

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedNoddedReifiedSet<Element> : KoneLinkedNoddedReifiedSet<Element>, KoneMutableLinkedReifiedSet<Element>, KoneMutableNoddedReifiedSet<Element>, KoneMutableLinkedNoddedSet<Element>