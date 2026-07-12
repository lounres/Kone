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
const val useLocallyAsExtensionReceiversFunctionShortNameString = "useLocallyAsExtensionReceivers"
const val unwrapLocallyAsExtensionReceiversFunctionShortNameString = "unwrapLocallyAsExtensionReceivers"
// Private runtime

val koneContextsPackageFQName = FqName(koneContextsPackageFQNameString)
// Library
val koneContextClassShortName = FqName(koneContextClassShortNameString)
val koneContextHolderClassShortName = FqName(koneContextHolderClassShortNameString)
// Public runtime
val koneContextHolderIncludeAnnotationShortName = FqName(koneContextHolderIncludeAnnotationShortNameString)
val koneContextHolderExcludeAnnotationShortName = FqName(koneContextHolderExcludeAnnotationShortNameString)
val useLocallyAsExtensionReceiversFunctionShortName = Name.identifier(useLocallyAsExtensionReceiversFunctionShortNameString)
val unwrapLocallyAsExtensionReceiversFunctionShortName = Name.identifier(unwrapLocallyAsExtensionReceiversFunctionShortNameString)
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
val useLocallyAsExtensionReceiversFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = useLocallyAsExtensionReceiversFunctionShortName,
)
val unwrapLocallyAsExtensionReceiversFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = unwrapLocallyAsExtensionReceiversFunctionShortName,
)
// Private runtime