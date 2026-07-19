/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.contexts


@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextHolderInclude
@Target(AnnotationTarget.PROPERTY)
public annotation class KoneContextHolderExclude

public fun localReceivers(vararg receivers: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}
public fun localContexts(vararg contexts: Any?) {
    contextsPluginExceptionForRuntimeDeclarations()
}
public fun KoneContext.Companion.unwrap(vararg koneContexts: KoneContext) {
    contextsPluginExceptionForRuntimeDeclarations()
}