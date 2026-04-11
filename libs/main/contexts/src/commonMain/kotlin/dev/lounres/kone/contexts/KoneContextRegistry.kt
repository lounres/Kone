/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.contexts

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.ProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName


/**
 * Type-safe registry (a.k.a. type-safe map) made especially for contexts in Kone.
 */
@JvmInline
public value class KoneContextRegistry(
    /**
     * Underlying type-safe registry.
     */
    public val contexts: OwnedProviderRegistry<KoneContextRegistry>
) : ProviderRegistry by contexts {
    public companion object;
    
    public fun interface Provider {
        public fun get(): KoneContextRegistry
    }
}

public fun KoneContextRegistry.tryToGetAll() {
    @Suppress("ControlFlowWithEmptyBody")
    for (_ in this.asRegistrationIterable()) {}
}

///**
// * Provides receiver for Kone context registry.
// */
//public inline operator fun <R> KoneContextRegistry.invoke(block: KoneContextRegistry.() -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    return block(this)
//}

@Suppress("UnusedReceiverParameter")
public inline fun <Result> KoneContextRegistry.koneContext(
    block: () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

@JvmName("koneContextContextual")
context(_: KoneContextRegistry)
public inline fun <Result> koneContext(
    block: () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

public inline fun <Context1, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    block: context(Context1) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Result> koneContext(
    key1: RegistryKey<Context1>,
    block: context(Context1) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
    )
}

public inline fun <Context1, Context2, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    block: context(Context1, Context2) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    block: context(Context1, Context2) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
    )
}

public inline fun <Context1, Context2, Context3, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    block: context(Context1, Context2, Context3) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    block: context(Context1, Context2, Context3) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    block: context(Context1, Context2, Context3, Context4) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    block: context(Context1, Context2, Context3, Context4) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    block: context(Context1, Context2, Context3, Context4, Context5) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    block: context(Context1, Context2, Context3, Context4, Context5) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
        this[key10],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
        koneContextRegistry[key10],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
        this[key10],
        this[key11],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
        koneContextRegistry[key10],
        koneContextRegistry[key11],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
        this[key10],
        this[key11],
        this[key12],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
        koneContextRegistry[key10],
        koneContextRegistry[key11],
        koneContextRegistry[key12],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    key13: RegistryKey<Context13>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
        this[key10],
        this[key11],
        this[key12],
        this[key13],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    key13: RegistryKey<Context13>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
        koneContextRegistry[key10],
        koneContextRegistry[key11],
        koneContextRegistry[key12],
        koneContextRegistry[key13],
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Context14, Result> KoneContextRegistry.koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    key13: RegistryKey<Context13>,
    key14: RegistryKey<Context14>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Context14) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this[key1],
        this[key2],
        this[key3],
        this[key4],
        this[key5],
        this[key6],
        this[key7],
        this[key8],
        this[key9],
        this[key10],
        this[key11],
        this[key12],
        this[key13],
        this[key14],
    )
}

@JvmName("koneContextContextual")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Context14, Result> koneContext(
    key1: RegistryKey<Context1>,
    key2: RegistryKey<Context2>,
    key3: RegistryKey<Context3>,
    key4: RegistryKey<Context4>,
    key5: RegistryKey<Context5>,
    key6: RegistryKey<Context6>,
    key7: RegistryKey<Context7>,
    key8: RegistryKey<Context8>,
    key9: RegistryKey<Context9>,
    key10: RegistryKey<Context10>,
    key11: RegistryKey<Context11>,
    key12: RegistryKey<Context12>,
    key13: RegistryKey<Context13>,
    key14: RegistryKey<Context14>,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Context11, Context12, Context13, Context14) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry[key1],
        koneContextRegistry[key2],
        koneContextRegistry[key3],
        koneContextRegistry[key4],
        koneContextRegistry[key5],
        koneContextRegistry[key6],
        koneContextRegistry[key7],
        koneContextRegistry[key8],
        koneContextRegistry[key9],
        koneContextRegistry[key10],
        koneContextRegistry[key11],
        koneContextRegistry[key12],
        koneContextRegistry[key13],
        koneContextRegistry[key14],
    )
}

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
public annotation class KoneContextRegistryBuilderDsl

/**
 * Builder function for [KoneContextRegistry].
 */
public inline fun KoneContextRegistry.Companion.build(block: (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return KoneContextRegistry(OwnedProviderRegistry.build { this.block() })
}

public inline fun KoneContextRegistry.Companion.buildWithProvider(
    block: context(KoneContextRegistry.Provider) (@KoneContextRegistryBuilderDsl MutableOwnedProviderRegistry<KoneContextRegistry>).() -> Unit
): KoneContextRegistry {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val provider = object : KoneContextRegistry.Provider {
        var result: KoneContextRegistry? = null
        override fun get(): KoneContextRegistry =
            result ?: error("KoneContextRegistry is not yet initialized but was requested by its properties.")
    }
    val result = KoneContextRegistry(OwnedProviderRegistry.build { block(provider, this) })
    provider.result = result
    return result
}