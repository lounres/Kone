package foo.bar

import dev.lounres.kone.contexts.*
import dev.lounres.kone.contextsKeys.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.*


@GenerateKoneContextKey
interface Context1<in Element> : KoneContext

@GenerateKoneContextKey
interface Context2<in Element> : Context1<Element>

val koneContextRegistry = KoneContextRegistry.build {
    Context2.Key<Int>().withImpliedUsingFirst correspondsTo { TODO() }
}

fun box(): String = if (koneContextRegistry.asProvidingRegistrationIterable().toList().size == 2) "OK" else "INVALID"