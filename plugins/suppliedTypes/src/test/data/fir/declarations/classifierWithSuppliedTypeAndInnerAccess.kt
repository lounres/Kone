// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


@Suppliable
interface Foo<@Supply T> {
    fun foo() {
        suppliedTypeOf<List<T>>()
    }
}