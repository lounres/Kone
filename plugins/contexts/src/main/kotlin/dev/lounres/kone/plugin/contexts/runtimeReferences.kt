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
const val koneRegistryPackageFQNameString = "dev.lounres.kone.registry"
// Library
const val koneContextClassShortNameString = "KoneContext"
const val registryClassShortNameString = "Registry"
const val registryKeyClassShortNameString = "RegistryKey"
// Public runtime
const val koneContextIncludeAnnotationShortNameString = "KoneContextInclude"
const val koneContextExcludeAnnotationShortNameString = "KoneContextExclude"
const val localReceiversFunctionShortNameString = "localReceivers"
const val localContextsFunctionShortNameString = "localContexts"
const val localUnwrapFunctionShortNameString = "localUnwrap"
const val koneLocalUnwrapFunctionShortNameString = "koneLocalUnwrap"
const val receiversFunctionShortNameString = "receivers"
const val contextsFunctionShortNameString = "contexts"
const val unwrapFunctionShortNameString = "unwrap"
// Private runtime
const val koneContextsPluginScopeArgumentStubFunctionShortNameString = "_koneContextsPluginScopeArgumentStub"

val koneContextsPackageFQName = FqName(koneContextsPackageFQNameString)
val koneRegistryPackageFQName = FqName(koneRegistryPackageFQNameString)
// Library
val koneContextClassShortName = FqName(koneContextClassShortNameString)
val registryClassShortName = FqName(registryClassShortNameString)
val registryKeyClassShortName = FqName(registryKeyClassShortNameString)
// Public runtime
val koneContextIncludeAnnotationShortName = FqName(koneContextIncludeAnnotationShortNameString)
val koneContextExcludeAnnotationShortName = FqName(koneContextExcludeAnnotationShortNameString)
val localReceiversFunctionShortName = Name.identifier(localReceiversFunctionShortNameString)
val localContextsFunctionShortName = Name.identifier(localContextsFunctionShortNameString)
val localUnwrapFunctionShortName = Name.identifier(localUnwrapFunctionShortNameString)
val koneLocalUnwrapFunctionShortName = Name.identifier(koneLocalUnwrapFunctionShortNameString)
val receiversFunctionShortName = Name.identifier(receiversFunctionShortNameString)
val contextsFunctionShortName = Name.identifier(contextsFunctionShortNameString)
val unwrapFunctionShortName = Name.identifier(unwrapFunctionShortNameString)
// Private runtime
val koneContextsPluginScopeArgumentStubFunctionShortName = Name.identifier(koneContextsPluginScopeArgumentStubFunctionShortNameString)

// Library
val koneContextClassId = ClassId(
    packageFqName = koneContextsPackageFQName,
    relativeClassName = koneContextClassShortName,
    isLocal = false
)
val registryClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = registryClassShortName,
    isLocal = false
)
val registryKeyClassId = ClassId(
    packageFqName = koneRegistryPackageFQName,
    relativeClassName = registryKeyClassShortName,
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
val koneLocalUnwrapFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = koneLocalUnwrapFunctionShortName,
)
val receiversFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = receiversFunctionShortName,
)
val contextsFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = contextsFunctionShortName,
)
val unwrapFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = unwrapFunctionShortName,
)
// Private runtime
val koneContextsPluginScopeArgumentStubFunctionCallableId = CallableId(
    packageName = koneContextsPackageFQName,
    className = null,
    callableName = koneContextsPluginScopeArgumentStubFunctionShortName,
)