// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS

import dev.lounres.kone.suppliedTypes.*


interface Raf {
    interface Foo<@Supply T>
}

class Hou {
    class Kie {
        class Bar<@Supply U> : Raf.Foo<Map<out U, *>>
    }
}