/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("CanConvertToMultiDollarString")

package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.name.Name


val internalSuppliedTypesStoragePropertyName: Name = Name.identifier("suppliedTypesStorage")

//fun internalSupplierPropertyName(classifierFqName: FqName, typeParameterName: Name): Name =
//    Name.identifier("\$suppliedTypePropertyFor_${classifierFqName.pathSegments().joinToString(separator = "-")}_${typeParameterName}")
//fun internalSupplierPropertyName(classifierClassId: ClassId, typeParameterName: Name): Name =
//    internalSupplierPropertyName(classifierClassId.asSingleFqName(), typeParameterName)

fun internalSupplierParameterName(typeParameterName: Name): Name = Name.identifier("\$suppliedTypeParameterFor_${typeParameterName}")
val noSuppliedTypeParameterInClassStubParameterName: Name = Name.identifier("\$noSuppliedTypeParameterInClassStub")

const val suppliableFunctionGeneratedBodyMessage = "Intrinsic function call was not substituted. Ensure you have applied supplied types compiler plugin."