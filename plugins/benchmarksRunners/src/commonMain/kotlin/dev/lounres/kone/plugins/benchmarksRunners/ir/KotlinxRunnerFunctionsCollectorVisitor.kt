/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.ir

import dev.lounres.kone.plugins.benchmarksRunners.koneBenchmarkRunnerAnnotationClassId
import dev.lounres.kone.scope
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid


class KotlinxRunnerFunctionsCollectorVisitor(
    private val runnerDescriptions: MutableList<RunnerDescription>,
) : IrVisitorVoid() {
    override fun visitElement(element: IrElement) { element.acceptChildrenVoid(this) }
    
    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Nothing?) {
        scope {
            val parent = declaration.parent
            when {
                parent is IrClass && parent.kind == OBJECT -> {}
                parent is IrFile -> {}
                parent is IrScript -> {}
                parent is IrReplSnippet -> {}
                else -> return@scope
            }
            val runnerAnnotation = declaration.annotations.singleOrNull { it.classId == koneBenchmarkRunnerAnnotationClassId } ?: return@scope
            val runnerName = (runnerAnnotation.arguments[0] as? IrConst)?.value?.let { it as String } ?: declaration.fqName.asString()
            runnerDescriptions += RunnerDescription(
                name = runnerName,
                theRunnerFunction = declaration,
                parent = parent,
            )
        }
        super.visitSimpleFunction(declaration, data)
    }
    
    override fun visitBody(body: IrBody) {}
}