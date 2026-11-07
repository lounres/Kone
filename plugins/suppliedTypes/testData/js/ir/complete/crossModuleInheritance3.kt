// MODULE: foo
import dev.lounres.kone.suppliedTypes.*


@Suppliable
open class Foo<@Supply T>

// MODULE: bar(foo)
import dev.lounres.kone.suppliedTypes.*


@Suppliable
class Bar<@Supply U> : Foo<Map<out U, *>>()