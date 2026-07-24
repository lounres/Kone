/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts


private class ContextsPluginException(message: String? = null, reason: Throwable? = null) : IllegalStateException(message, reason)

@PublishedApi
internal fun contextsPluginExceptionForRuntimeDeclarations(): Nothing = throw ContextsPluginException(contextsPluginExceptionForRuntimeDeclarationsMessage)

//@Deprecated(message = "...", level = HIDDEN)
//public fun contextsPluginLambdaLeadingCall() {
////    error("...")
//}