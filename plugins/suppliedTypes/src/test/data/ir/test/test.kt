// SUPPRESS_WARNINGS: PRE_RELEASE_CLASS


fun bar(arg: Any?, block: () -> Unit) {}

fun foo() {
    println("1")
    with(println("2")) {
        println("3")
    }
    println("4")
    bar(println("5")) {
        println("6")
    }
    println("7")
}