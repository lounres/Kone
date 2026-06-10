/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.fiktion

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data object Fiktion {
//    public data object Scope {
//        @Target(
//            AnnotationTarget.FUNCTION,
//            AnnotationTarget.PROPERTY,
//            AnnotationTarget.PROPERTY_GETTER,
//            AnnotationTarget.PROPERTY_SETTER,
//            AnnotationTarget.TYPE,
//        )
//        @Retention(AnnotationRetention.SOURCE)
//        public annotation class Local {
//            @Target(
//                AnnotationTarget.FUNCTION,
//                AnnotationTarget.PROPERTY,
//                AnnotationTarget.PROPERTY_GETTER,
//                AnnotationTarget.PROPERTY_SETTER,
//                AnnotationTarget.CLASS,
//
//                AnnotationTarget.EXPRESSION,
//            )
//            @Retention(AnnotationRetention.SOURCE)
//            public annotation class Force
//        }
//
//        @Target(
//            AnnotationTarget.FUNCTION,
//            AnnotationTarget.PROPERTY,
//            AnnotationTarget.PROPERTY_GETTER,
//            AnnotationTarget.PROPERTY_SETTER,
//
//            AnnotationTarget.LOCAL_VARIABLE,
//        )
//        @Retention(AnnotationRetention.SOURCE)
//        public annotation class NonLocal
//
//        @Target(
//            AnnotationTarget.FUNCTION,
//            AnnotationTarget.PROPERTY,
//            AnnotationTarget.PROPERTY_GETTER,
//            AnnotationTarget.PROPERTY_SETTER,
//            AnnotationTarget.CLASS,
//
//            AnnotationTarget.TYPE,
//        )
//        @Retention(AnnotationRetention.SOURCE)
//        public annotation class Atomic {
//            @Target(
//                AnnotationTarget.FUNCTION,
//                AnnotationTarget.PROPERTY,
//                AnnotationTarget.PROPERTY_GETTER,
//                AnnotationTarget.PROPERTY_SETTER,
//                AnnotationTarget.CLASS,
//
//                AnnotationTarget.EXPRESSION,
//            )
//            @Retention(AnnotationRetention.SOURCE)
//            public annotation class Force
//        }
//    }
    
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.LOCAL_VARIABLE,
        AnnotationTarget.EXPRESSION,
//        AnnotationTarget.VALUE_PARAMETER,
    )
    @Retention(AnnotationRetention.SOURCE)
    public annotation class Imaginary
    
    @Target(AnnotationTarget.EXPRESSION)
    @Retention(AnnotationRetention.SOURCE)
    public annotation class Real
    
//    @Target(
//        AnnotationTarget.FUNCTION,
////        AnnotationTarget.PROPERTY_GETTER,
////        AnnotationTarget.PROPERTY_SETTER,
//    )
//    public annotation class Intercept
    
    @RequiresOptIn(
        message = "This is Fiktion atomic call interceptor delicate API. Atomic call interceptor should be set only in frameworks before the main program execution.",
        level = ERROR,
    )
    public annotation class AtomicCallInterceptorDelicateApi
}

//@Suppress("WRONG_INVOCATION_KIND")
//@Fiktion
//public fun atomic(block: () -> Unit) {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    error("Fiktion compiler plugin intrinsic was not substituted.")
//}
//
//@Suppress("WRONG_INVOCATION_KIND")
//@Fiktion
//public fun <T> atomic(value: T, block: (T) -> Unit): T {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    error("Fiktion compiler plugin intrinsic was not substituted.")
//}

/*@Fiktion.Scope.Local*/
public inline fun atomic(block: /*@Fiktion.Scope.Local*/ () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block()
}

public interface CallInterceptor {
    public fun entering()
    public fun entered()
    public fun exiting()
    public fun exited()
    
    public data object Idle : CallInterceptor {
        override fun entering() {}
        override fun entered() {}
        override fun exiting() {}
        override fun exited() {}
    }
}

@Fiktion.Imaginary
@Fiktion.AtomicCallInterceptorDelicateApi
public expect inline var callInterceptor: CallInterceptor