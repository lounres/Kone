import java.io.RandomAccessFile
import java.io.File

fun loadData(fileName: String, size: Int): List<List<ULong>> = RandomAccessFile(File(fileName), "r").use { file -> List(size) { List(size) { file.readLong().toULong() } } }
fun summarise(data: List<List<ULong>>) {
    val readyCells = data.sumOf { it.count { it != 0uL } }
	val cells = data.size.let { it * it }
	println("$readyCells/$cells, ${readyCells.toDouble()/cells * 100}%")
    for (line in data) {
	    for (v in line) print(if (v != 0uL) "\u25A0" else "\u00B7")
		println()
	}
}
val data = loadData("C:\\Programming\\Kone\\kone-gameTheory\\src\\commonMain\\resources\\prod9-table-size-64-at-2022-09-28-16-05-48", 64)
summarise(data)