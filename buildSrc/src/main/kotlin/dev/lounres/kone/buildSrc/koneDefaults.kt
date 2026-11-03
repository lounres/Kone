/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.buildSrc


public val koneCompilerOptions: List<String> = listOf(
//    "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
//    "-Xvalue-classes",
//    "-Xcontract-syntax-v2",
    "-Xexpect-actual-classes",
    "-Xconsistent-data-class-copy-visibility",
    "-Xcontext-sensitive-resolution",
    "-Xreturn-value-checker=full",
    "-Xlocal-type-aliases",
    "-Xname-based-destructuring=complete",
    "-Xcollection-literals",
    "-Xcompanion-blocks-and-extensions",
//    "-Xfull-value-classes",
    
    // TODO: Waiting for JDK 28
//    "-Xvalhalla-value-classes",
//    "-Xjvm-enable-preview",
    
    // Kone compiler plugins order
    "-Xcompiler-plugin-order=dev.lounres.kone.plugin.suppliedTypes>dev.lounres.kone.plugin.contextsKeys",
)

public val koneOptIns: List<String> = listOf(
    "kotlin.experimental.ExperimentalTypeInference",
    "kotlin.contracts.ExperimentalContracts",
    "kotlin.ExperimentalStdlibApi",
    "kotlin.ExperimentalSubclassOptIn",
    "kotlin.ExperimentalUnsignedTypes",
    "kotlin.uuid.ExperimentalUuidApi",
    "kotlin.concurrent.atomics.ExperimentalAtomicApi",
    "kotlinx.serialization.ExperimentalSerializationApi",
    "dev.lounres.kone.annotations.UnstableKoneAPI",
    "dev.lounres.kone.annotations.ExperimentalKoneAPI",
)