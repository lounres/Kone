/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI


/**
 * Represents a reified version of [KoneSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneReifiedSet<out Element> : KoneSet<@UnsafeVariance Element> {
    public companion object
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneRemovableReifiedSet<out Element> : KoneRemovableSet<@UnsafeVariance Element>, KoneReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableReifiedSet<Element> : KoneMutableSet<Element>, KoneRemovableReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedReifiedSet<out Element> : KoneNoddedSet<@UnsafeVariance Element>, KoneReifiedSet<Element> {
    public companion object
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneRemovableNoddedReifiedSet<out Element> : KoneRemovableNoddedSet<@UnsafeVariance Element>, KoneRemovableReifiedSet<Element>, KoneNoddedReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedReifiedSet<Element> : KoneMutableNoddedSet<Element>, KoneMutableReifiedSet<Element>, KoneRemovableNoddedReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneLinkedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedReifiedSet<out Element> : KoneReifiedSet<Element>, KoneLinkedSet<@UnsafeVariance Element> {
    public companion object
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneRemovableLinkedReifiedSet<out Element> : KoneLinkedReifiedSet<Element>, KoneRemovableReifiedSet<Element>, KoneRemovableLinkedSet<@UnsafeVariance Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableLinkedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedReifiedSet<Element> : KoneRemovableLinkedReifiedSet<Element>, KoneMutableReifiedSet<Element>, KoneMutableLinkedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneLinkedNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedNoddedReifiedSet<out Element> : KoneNoddedReifiedSet<Element>, KoneLinkedReifiedSet<Element>, KoneLinkedNoddedSet<@UnsafeVariance Element> {
    public companion object
}

@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneRemovableLinkedNoddedReifiedSet<out Element> : KoneLinkedNoddedReifiedSet<Element>, KoneRemovableLinkedReifiedSet<Element>, KoneRemovableNoddedReifiedSet<Element>, KoneRemovableLinkedNoddedSet<@UnsafeVariance Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableLinkedNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedNoddedReifiedSet<Element> : KoneRemovableLinkedNoddedReifiedSet<Element>, KoneMutableLinkedReifiedSet<Element>, KoneMutableNoddedReifiedSet<Element>, KoneMutableLinkedNoddedSet<Element> {
    
    public companion object
}