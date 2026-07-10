// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Foo<X> : KoneContextHolder {
    @KoneContextHolderContext
    val bar: List<X> get() = emptyList()
    val baz: Set<X> get() = emptySet()
}

fun box(): String {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(Foo<Int>())
    return if (contextOf<List<Int>>().single() == 57) "OK" else "INCORRECT"
}