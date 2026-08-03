package foo.bar

import dev.lounres.kone.contexts.*
import dev.lounres.kone.contextsKeys.*
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.*


@GenerateKoneContextKey
interface Equality<Element, Container : List<Element>> : KoneContext

val koneContextRegistry = KoneContextRegistry.build {
    Equality.Key<Int, MutableList<Int>>() correspondsTo { TODO() }
}

fun box(): String = if (koneContextRegistry.asProvidingRegistrationIterable().toList().size == 1) "OK" else "INVALID"