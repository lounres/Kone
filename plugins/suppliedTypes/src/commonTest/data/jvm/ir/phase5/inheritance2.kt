import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


@Suppliable
interface Foo<@Supply T>

@Suppliable
class Bar<@Supply U> : Foo<Map<out U, *>>