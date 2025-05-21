/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


fun internalSupplierPropertyName(classifierFqName: FqName, typeParameterName: Name): Name =
    Name.identifier("\$supplied_type_variable_for_${classifierFqName.pathSegments().joinToString(separator = "-")}_${typeParameterName}")
fun internalSupplierParameterName(typeParameterName: Name): Name =
    Name.identifier("\$supplied_type_argument_for_${typeParameterName}")