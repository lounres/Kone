// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T> foo(arg: UInt) = suppliedTypeOf<List<T>>().toString()

fun box(): String = if (foo<String>(57u) == "kotlin.collections.List<out kotlin.String>") "OK" else "INCORRECT"