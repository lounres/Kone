/*
 * Copyright © 2025 Gleb Minaev
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

/**
 * Represents a reified version of [KoneMutableSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableReifiedSet<Element> : KoneMutableSet<Element>, KoneReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneNoddedReifiedSet<out Element> : KoneNoddedSet<@UnsafeVariance Element>, KoneReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableNoddedReifiedSet<Element> : KoneMutableNoddedSet<Element>, KoneMutableReifiedSet<Element>, KoneNoddedReifiedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneLinkedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedReifiedSet<out Element> : KoneReifiedSet<Element>, KoneLinkedSet<@UnsafeVariance Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableLinkedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedReifiedSet<Element> : KoneLinkedReifiedSet<Element>, KoneMutableReifiedSet<Element>, KoneMutableLinkedSet<Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneLinkedNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneLinkedNoddedReifiedSet<out Element> : KoneNoddedReifiedSet<Element>, KoneLinkedReifiedSet<Element>, KoneLinkedNoddedSet<@UnsafeVariance Element> {
    public companion object
}

/**
 * Represents a reified version of [KoneMutableLinkedNoddedSet].
 */
@SubclassOptInRequired(DelicateCollectionsInheritanceAPI::class)
public interface KoneMutableLinkedNoddedReifiedSet<Element> : KoneLinkedNoddedReifiedSet<Element>, KoneMutableLinkedReifiedSet<Element>, KoneMutableNoddedReifiedSet<Element>, KoneMutableLinkedNoddedSet<Element> {
    public companion object
}