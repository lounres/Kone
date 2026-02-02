// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


fun <@Supply T> foo(): SuppliedType = suppliedTypeOf<List<T>>()

fun box() {
    val st1 = foo<Map<out Int, String>>()
    
    println(st1)
}
