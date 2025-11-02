import dev.lounres.kone.concurrentCollections.KoneConcurrentSundellTsigasNoddedDequeue


fun main() {
    val dequeue = KoneConcurrentSundellTsigasNoddedDequeue<Int>()
    
    dequeue.addLast(1)
    dequeue.addFirst(-1)
}