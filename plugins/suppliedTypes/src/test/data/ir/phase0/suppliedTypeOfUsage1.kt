// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T> foo() = suppliedTypeOf<List<T>>()

@Suppliable
fun <@Supply T> bar() = foo<Map<out T, T?>>()

fun box(): String {
    println(bar<String>())
    return "OK"
}