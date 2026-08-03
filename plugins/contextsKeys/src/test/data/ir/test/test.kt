package foo.bar

import dev.lounres.kone.contexts.*
import dev.lounres.kone.contextsKeys.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.*


interface Equality<in Element> : KoneContext {
    @Suppliable
    class Key<@Supply Element> : SuppliedTypeRegistryKey<Equality<Element>>() {
        override val impliedKeys: ImpliedKeysRegistry<Equality<Element>> by lazy {
            ImpliedKeysRegistry {
            
            }
        }
        
        override fun toString(): String = "foo.bar.Equality.Key<${suppliedTypesStorage}>"
    }
}

val koneContextRegistry = KoneContextRegistry.build {
    Equality.Key<Int>() correspondsTo { TODO() }
}

fun box(): String = if (koneContextRegistry.asProvidingRegistrationIterable().toList().size == 1) "OK" else "INVALID"