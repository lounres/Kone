/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerUtils

import org.jetbrains.kotlin.diagnostics.AbstractSourceElementPositioningStrategy
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory2
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory3
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory4
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.psi.KtElement
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


public class KtDiagnosticFactory0Delegate(
    private val severity: Severity,
    private val defaultPositioningStrategy: AbstractSourceElementPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
    private val psiType: KClass<*> = KtElement::class,
) {
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<KtDiagnosticsContainer, KtDiagnosticFactory0> =
        ReadOnlyProperty { thisRef, property ->
            KtDiagnosticFactory0(
                name = property.name,
                severity = severity,
                defaultPositioningStrategy = defaultPositioningStrategy,
                psiType = psiType,
                rendererFactory = thisRef.getRendererFactory()
            )
        }
    
    public companion object {
        public fun ERROR(): KtDiagnosticFactory0Delegate = KtDiagnosticFactory0Delegate(severity = Severity.ERROR)
        public fun WARNING(): KtDiagnosticFactory0Delegate = KtDiagnosticFactory0Delegate(severity = Severity.WARNING)
    }
}

public class KtDiagnosticFactory1Delegate<A>(
    private val severity: Severity,
    private val defaultPositioningStrategy: AbstractSourceElementPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
    private val psiType: KClass<*> = KtElement::class,
) {
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<KtDiagnosticsContainer, KtDiagnosticFactory1<A>> =
        ReadOnlyProperty { thisRef, property ->
            KtDiagnosticFactory1(
                name = property.name,
                severity = severity,
                defaultPositioningStrategy = defaultPositioningStrategy,
                psiType = psiType,
                rendererFactory = thisRef.getRendererFactory()
            )
        }
    
    public companion object {
        public fun <A> ERROR(): KtDiagnosticFactory1Delegate<A> = KtDiagnosticFactory1Delegate(severity = Severity.ERROR)
        public fun <A> WARNING(): KtDiagnosticFactory1Delegate<A> = KtDiagnosticFactory1Delegate(severity = Severity.WARNING)
    }
}

public class KtDiagnosticFactory2Delegate<A, B>(
    private val severity: Severity,
    private val defaultPositioningStrategy: AbstractSourceElementPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
    private val psiType: KClass<*> = KtElement::class,
) {
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<KtDiagnosticsContainer, KtDiagnosticFactory2<A, B>> =
        ReadOnlyProperty { thisRef, property ->
            KtDiagnosticFactory2(
                name = property.name,
                severity = severity,
                defaultPositioningStrategy = defaultPositioningStrategy,
                psiType = psiType,
                rendererFactory = thisRef.getRendererFactory()
            )
        }
    
    public companion object {
        public fun <A, B> ERROR(): KtDiagnosticFactory2Delegate<A, B> = KtDiagnosticFactory2Delegate(severity = Severity.ERROR)
        public fun <A, B> WARNING(): KtDiagnosticFactory2Delegate<A, B> = KtDiagnosticFactory2Delegate(severity = Severity.WARNING)
    }
}

public class KtDiagnosticFactory3Delegate<A, B, C>(
    private val severity: Severity,
    private val defaultPositioningStrategy: AbstractSourceElementPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
    private val psiType: KClass<*> = KtElement::class,
) {
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<KtDiagnosticsContainer, KtDiagnosticFactory3<A, B, C>> =
        ReadOnlyProperty { thisRef, property ->
            KtDiagnosticFactory3(
                name = property.name,
                severity = severity,
                defaultPositioningStrategy = defaultPositioningStrategy,
                psiType = psiType,
                rendererFactory = thisRef.getRendererFactory()
            )
        }
    
    public companion object {
        public fun <A, B, C> ERROR(): KtDiagnosticFactory3Delegate<A, B, C> = KtDiagnosticFactory3Delegate(severity = Severity.ERROR)
        public fun <A, B, C> WARNING(): KtDiagnosticFactory3Delegate<A, B, C> = KtDiagnosticFactory3Delegate(severity = Severity.WARNING)
    }
}

public class KtDiagnosticFactory4Delegate<A, B, C, D>(
    private val severity: Severity,
    private val defaultPositioningStrategy: AbstractSourceElementPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
    private val psiType: KClass<*> = KtElement::class,
) {
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<KtDiagnosticsContainer, KtDiagnosticFactory4<A, B, C, D>> =
        ReadOnlyProperty { thisRef, property ->
            KtDiagnosticFactory4(
                name = property.name,
                severity = severity,
                defaultPositioningStrategy = defaultPositioningStrategy,
                psiType = psiType,
                rendererFactory = thisRef.getRendererFactory()
            )
        }
    
    public companion object {
        public fun <A, B, C, D> ERROR(): KtDiagnosticFactory4Delegate<A, B, C, D> = KtDiagnosticFactory4Delegate(severity = Severity.ERROR)
        public fun <A, B, C, D> WARNING(): KtDiagnosticFactory4Delegate<A, B, C, D> = KtDiagnosticFactory4Delegate(severity = Severity.WARNING)
    }
}