// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS


class Foo {
    val gee: Int
    
    fun bar() {
        ::gee
    }
    
    init {
        gee = 57
    }
}