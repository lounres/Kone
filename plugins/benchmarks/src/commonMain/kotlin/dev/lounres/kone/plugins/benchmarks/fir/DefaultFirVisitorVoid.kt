/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarks.fir

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid


abstract class DefaultFirVisitorVoid : FirVisitorVoid() {
    override fun visitElement(element: FirElement) { element.acceptChildren(this) }
    
    override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
        visitElement(qualifiedAccessExpression)
    }
    override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
        visitQualifiedAccessExpression(propertyAccessExpression)
    }
    override fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression) {
        visitPropertyAccessExpression(whenSubjectExpression)
    }
    override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess) {
        visitQualifiedAccessExpression(callableReferenceAccess)
    }
    override fun visitFunctionCall(functionCall: FirFunctionCall) {
        visitQualifiedAccessExpression(functionCall)
    }
    override fun visitComponentCall(componentCall: FirComponentCall) {
        visitFunctionCall(componentCall)
    }
    override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
        visitFunctionCall(implicitInvokeCall)
    }
    override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
        visitFunctionCall(integerLiteralOperatorCall)
    }
    override fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression) {
        visitQualifiedAccessExpression(thisReceiverExpression)
    }
    override fun visitSuperReceiverExpression(superReceiverExpression: FirSuperReceiverExpression) {
        visitQualifiedAccessExpression(superReceiverExpression)
    }
    
    override fun visitDeclaration(declaration: FirDeclaration) {
        visitElement(declaration)
    }
    override fun visitReplSnippet(replSnippet: FirReplSnippet) {
        visitDeclaration(replSnippet)
    }
    override fun visitDanglingModifierList(danglingModifierList: FirDanglingModifierList) {
        visitDeclaration(danglingModifierList)
    }
    override fun visitMemberDeclaration(memberDeclaration: FirMemberDeclaration) {
        visitDeclaration(memberDeclaration)
    }
    override fun visitCallableDeclaration(callableDeclaration: FirCallableDeclaration) {
        visitMemberDeclaration(callableDeclaration)
    }
    override fun visitVariable(variable: FirVariable) {
        visitCallableDeclaration(variable)
    }
    override fun visitValueParameter(valueParameter: FirValueParameter) {
        visitVariable(valueParameter)
    }
    override fun visitField(field: FirField) {
        visitVariable(field)
    }
    override fun visitBackingField(backingField: FirBackingField) {
        visitVariable(backingField)
    }
    override fun visitProperty(property: FirProperty) {
        visitVariable(property)
    }
    override fun visitErrorProperty(errorProperty: FirErrorProperty) {
        visitProperty(errorProperty)
    }
    override fun visitEnumEntry(enumEntry: FirEnumEntry) {
        visitVariable(enumEntry)
    }
    override fun visitFunction(function: FirFunction) {
        visitCallableDeclaration(function)
    }
    override fun visitAnonymousFunction(anonymousFunction: FirAnonymousFunction) {
        visitFunction(anonymousFunction)
    }
    override fun visitConstructor(constructor: FirConstructor) {
        visitFunction(constructor)
    }
    override fun visitErrorPrimaryConstructor(errorPrimaryConstructor: FirErrorPrimaryConstructor) {
        visitConstructor(errorPrimaryConstructor)
    }
    override fun visitNamedFunction(namedFunction: FirNamedFunction) {
        visitFunction(namedFunction)
    }
    override fun visitPropertyAccessor(propertyAccessor: FirPropertyAccessor) {
        visitFunction(propertyAccessor)
    }
    override fun visitErrorFunction(errorFunction: FirErrorFunction) {
        visitFunction(errorFunction)
    }
    override fun visitClassLikeDeclaration(classLikeDeclaration: FirClassLikeDeclaration) {
        visitMemberDeclaration(classLikeDeclaration)
    }
    override fun visitClass(klass: FirClass) {
        visitClassLikeDeclaration(klass)
    }
    override fun visitRegularClass(regularClass: FirRegularClass) {
        visitClass(regularClass)
    }
    override fun visitAnonymousObject(anonymousObject: FirAnonymousObject) {
        visitClass(anonymousObject)
    }
    override fun visitTypeAlias(typeAlias: FirTypeAlias) {
        visitClassLikeDeclaration(typeAlias)
    }
    override fun visitAnonymousInitializer(anonymousInitializer: FirAnonymousInitializer) {
        visitDeclaration(anonymousInitializer)
    }
    override fun visitCodeFragment(codeFragment: FirCodeFragment) {
        visitDeclaration(codeFragment)
    }
    override fun visitReceiverParameter(receiverParameter: FirReceiverParameter) {
        visitDeclaration(receiverParameter)
    }
    override fun visitScriptReceiverParameter(scriptReceiverParameter: FirScriptReceiverParameter) {
        visitReceiverParameter(scriptReceiverParameter)
    }
    override fun visitFile(file: FirFile) {
        visitDeclaration(file)
    }
    override fun visitScript(script: FirScript) {
        visitDeclaration(script)
    }
    override fun visitTypeParameter(typeParameter: FirTypeParameter) {
        visitDeclaration(typeParameter)
    }
}