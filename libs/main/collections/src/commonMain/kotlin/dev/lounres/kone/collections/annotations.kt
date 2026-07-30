/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


/**
 * Marks that the annotated interface is delicate to inherit and requires reading its contracts carefully.
 */
@Target(AnnotationTarget.CLASS)
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Please, read the contracts of the interface carefully before inheriting it."
)
public annotation class DelicateCollectionsInheritanceAPI

/**
 * Marks that the annotated constructor or builder function is delicate to use and requires reading its contracts carefully.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Please, read the contracts of the constructor or builder carefully before using it."
)
public annotation class DelicateListBackedCollectionsBuilderAPI