// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


open class Foo<@Supplied T>

class Bar<@Supplied U> : Foo<Map<out U, *>>()