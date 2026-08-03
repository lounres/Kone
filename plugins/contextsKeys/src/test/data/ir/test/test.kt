package foo.bar

import dev.lounres.kone.contexts.*
import dev.lounres.kone.contextsKeys.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.*


interface Context1<in Element> : KoneContext {
    @Suppliable
    class Key<@Supply Element> : SuppliedTypeRegistryKey<Context1<Element>>() {
        override val impliedKeys: ImpliedKeysRegistry<Context1<Element>> by lazy {
            ImpliedKeysRegistry {
            
            }
        }
        
        override fun toString(): String = "foo.bar.Context1.Key<${suppliedTypesStorage}>"
    }
}

interface Context2<in Element> : Context1<Element> {
    @Suppliable
    class Key<@Supply Element> : SuppliedTypeRegistryKey<Context2<Element>>() {
        override val impliedKeys: ImpliedKeysRegistry<Context2<Element>> by lazy {
            ImpliedKeysRegistry {
                Context1.Key<Element>().impliesSame()
            }
        }
        
        override fun toString(): String = "foo.bar.Context2.Key<${suppliedTypesStorage}>"
    }
}

val koneContextRegistry = KoneContextRegistry.build {
    Context2.Key<Int>() correspondsTo { TODO() }
}

fun box(): String = if (koneContextRegistry.asProvidingRegistrationIterable().toList().size == 1) "OK" else "INVALID"