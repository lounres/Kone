import dev.lounres.kone.suppliedTypes.*


interface Raf {
    @Suppliable
    interface Foo<@Supply T>
}

class Hou {
    class Kie {
        @Suppliable
        interface Bar<@Supply U> : Raf.Foo<Map<out U, *>>
    }
}