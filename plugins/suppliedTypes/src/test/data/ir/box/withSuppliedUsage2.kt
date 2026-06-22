// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T, X> foo(): List<String> = buildList {
    add(suppliedTypeOf<Map<T, String>>().toString())
    withSupplied<X, _>(suppliedTypeOf<Long>()) {
        add(suppliedTypeOf<Map<T, X>>().toString())
    }
}

fun box(): String {
    val list = foo<Int, Unit>()
    if (list.size != 2) return "INCORRECT"
    if (list[0] != "kotlin.collections.Map<kotlin.Int, out kotlin.String>") return "INCORRECT"
    if (list[1] != "kotlin.collections.Map<kotlin.Int, out kotlin.Long>") return "INCORRECT"
    return "OK"
}