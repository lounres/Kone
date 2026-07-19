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
const val koneContextHolderClassShortNameString = "KoneContextHolder"
// Public runtime
const val koneContextHolderIncludeAnnotationShortNameString = "KoneContextHolderInclude"
const val koneContextHolderExcludeAnnotationShortNameString = "KoneContextHolderExclude"
const val localReceiversFunctionShortNameString = "localReceivers"
const val localContextsFunctionShortNameString = "localContexts"
const val unwrapFunctionShortNameString = "unwrap"
// Private runtime

val koneContextsPackageFQName = FqName(koneContextsPackageFQNameString)
// Library
val koneContextClassShortName = FqName(koneContextClassShortNameString)
val koneContextHolderClassShortName = FqName(koneContextHolderClassShortNameString)
// Public runtime
val koneContextHolderIncludeAnnotationShortName = FqName(koneContextHolderIncludeAnnotationShortNameString)
val koneContextHolderExcludeAnnotationShortName = FqName(koneContextHolderExcludeAnnotationShortNameString)
val localReceiversFunctionShortName = Name.identifier(localReceiversFunctionShortNameString)
val localContextsFunctionShortName = Name.identifier(localContextsFunctionShortNameString)
val unwrapFunctionShortName = Name.identifier(unwrapFunctionShortNameString)
// Private runtime

// Library
val koneContextClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextClassShortName,
    isLocal = false
)
val koneContextHolderClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextHolderClassShortName,
    isLocal = false
)
// Public runtime
val koneContextHolderIncludeAnnotationClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextHolderIncludeAnnotationShortName,
    isLocal = false,
)
val koneContextHolderExcludeAnnotationClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextHolderExcludeAnnotationShortName,
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
val unwrapFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = unwrapFunctionShortName,
)
// Private runtime