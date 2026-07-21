/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contexts

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


const val koneContextsPackageFQNameString = "dev.lounres.kone.contexts"
// Library
const val koneContextClassShortNameString = "KoneContext"
// Public runtime
const val koneContextIncludeAnnotationShortNameString = "KoneContextInclude"
const val koneContextExcludeAnnotationShortNameString = "KoneContextExclude"
const val localReceiversFunctionShortNameString = "localReceivers"
const val localContextsFunctionShortNameString = "localContexts"
const val localUnwrapFunctionShortNameString = "localUnwrap"
// Private runtime

val koneContextsPackageFQName = FqName(koneContextsPackageFQNameString)
// Library
val koneContextClassShortName = FqName(koneContextClassShortNameString)
// Public runtime
val koneContextIncludeAnnotationShortName = FqName(koneContextIncludeAnnotationShortNameString)
val koneContextExcludeAnnotationShortName = FqName(koneContextExcludeAnnotationShortNameString)
val localReceiversFunctionShortName = Name.identifier(localReceiversFunctionShortNameString)
val localContextsFunctionShortName = Name.identifier(localContextsFunctionShortNameString)
val localUnwrapFunctionShortName = Name.identifier(localUnwrapFunctionShortNameString)
// Private runtime

// Library
val koneContextClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextClassShortName,
    isLocal = false
)
// Public runtime
val koneContextIncludeAnnotationClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextIncludeAnnotationShortName,
    isLocal = false,
)
val koneContextExcludeAnnotationClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextExcludeAnnotationShortName,
    isLocal = false,
)
val localReceiversFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = localReceiversFunctionShortName,
)
val localContextsFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = localContextsFunctionShortName,
)
val localUnwrapFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = localUnwrapFunctionShortName,
)
// Private runtime