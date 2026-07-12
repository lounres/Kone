// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

package foo.bar

import dev.lounres.kone.contexts.*


fun interface CoolContext<X> {
    fun makeItCool(): X
}

object Foo : KoneContextHolder {
    @KoneContextHolderInclude
    val bar = CoolContext { 57 }
    val baz = CoolContext { 179 }
}

fun box(): String {
    KoneContextHolder.unwrapLocallyAsExtensionReceivers(Foo)
    return if (makeItCool() == 57) "OK" else "INCORRECT"
}