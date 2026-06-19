// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


@Suppliable
fun <@Supply T> foo() = suppliedTypeOf<List<T>>().toString()

fun <T> bar(arg: SuppliedType) = withSupplied<T, _>(arg) { foo<Map<out T, T?>>() }

fun box(): String = if (bar<String>(suppliedTypeOf<String>()) == "kotlin.collections.List<out kotlin.collections.Map<out kotlin.String, out kotlin.String?>>") "OK" else "INCORRECT"