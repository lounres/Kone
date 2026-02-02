// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*
import kotlin.reflect.KVariance


open class Foo<@Supply T>

class Bar<@Supply U> : Foo<Map<out U, *>>()