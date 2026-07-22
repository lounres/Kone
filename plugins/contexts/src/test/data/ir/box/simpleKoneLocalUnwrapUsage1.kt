// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*
import dev.lounres.kone.registry.*


class Foo : KoneContext {
    val bar = 57
    
    data object Key : RegistryKey<Foo>
}

context(foo: Foo)
val baz: Int get() = foo.bar

val koneContextRegsitry = KoneContextRegistry.build {
    Foo.Key correspondsTo Foo()
}

fun box(): String {
    koneContextRegsitry.koneLocalUnwrap(Foo.Key)
    return if (baz == 57) "OK" else "INCORRECT"
}