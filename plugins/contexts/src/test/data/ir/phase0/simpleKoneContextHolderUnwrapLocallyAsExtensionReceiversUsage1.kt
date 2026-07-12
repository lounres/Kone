// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


class Foo<X> : KoneContextHolder {
    @KoneContextHolderInclude
    val bar: List<X> get() = emptyList()
    val baz: Set<X> get() = emptySet()
}

fun box(): String {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(Foo<Int>())
    return if (contextOf<List<Int>>().isEmpty()) "OK" else "INCORRECT"
}