import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


@Suppliable
interface Foo<@Supply T> {
    fun foo() {
        SuppliedType.of<List<T>>()
    }
}