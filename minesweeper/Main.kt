import kotlin.random.Random.Default.nextInt

const val SAFE_CELL = "."
const val MINE = "X"
const val MARK = "*"
const val EXPLORED_CELL = "/"

fun printMineField(mineField: MutableList<MutableList<String>>, repString: String = "X") {
    print(" |")
    repeat(mineField[0].size) { index -> print(index + 1) }
    println("|")
    print("-|")
    print("-".repeat(mineField[0].size))
    println("|")
    for (i in 0 until mineField.size) {
        println("${i + 1}|${mineField[i].joinToString("").replace(repString, ".")}|")
    }
    print("-|")
    print("-".repeat(mineField[0].size))
    println("|")
}

fun checkMineAround(row: Int, col: Int, mf: MutableList<MutableList<String>>): Int {
    var result = 0

    if (mf[row][col] == MINE) return -1

    var (up, down, left, right) = listOf(1, 1, 1, 1)

    when (row) {
        0 -> up = 0
        mf.size - 1 -> down = 0
    }
    when (col) {
        0 -> left = 0
        mf[0].size - 1 -> right = 0
    }

    for (i in (row - up)..(row + down)) {
        for (j in (col - left)..(col + right)) {
            if (mf[i][j] == MINE) result += 1
        }
    }
    return result
}

fun getNeighbours(row: Int, col: Int, mf: MutableList<MutableList<String>>): Int {
    var result = 0
    if (mf[row][col] == MINE) return -1

    var (up, down, left, right) = listOf(1, 1, 1, 1)

    when (row) {
        0 -> up = 0
        mf.size - 1 -> down = 0
    }
    when (col) {
        0 -> left = 0
        mf[0].size - 1 -> right = 0
    }

    val indices = MutableList(2*row+down - up) { MutableList(2*col+right-left) { 0 } }
    for (i in (row - up)..(row + down)) {
        for (j in (col - left)..(col + right)) {
            if (mf[i][j] == MINE) result += 1
        }
    }
    return result
}

fun floodFill(row: Int, col: Int, mf: MutableList<MutableList<String>>, playerCopy: MutableList<MutableList<String>>) {
    var (up, down, left, right) = listOf(1, 1, 1, 1)

    when (row) {
        0 -> up = 0
        mf.size - 1 -> down = 0
    }
    when (col) {
        0 -> left = 0
        mf[0].size - 1 -> right = 0
    }

    for (i in (row - up)..(row + down)) {
        for (j in (col - left)..(col + right)) {
            val cell = mf[i][j]
            if (cell != MINE && cell != SAFE_CELL) {
                playerCopy[i][j] = cell
            } else {
                playerCopy[i][j] = EXPLORED_CELL
            }

        }
    }

}

fun generateMineField(mines: Int, rows: Int, columns: Int): MutableList<MutableList<String>> {
    var minesRemaining = mines
    val mineField = MutableList(columns) { MutableList(rows) { SAFE_CELL } }
    while (minesRemaining != 0) {
        val randRow = nextInt(rows)
        val randCol = nextInt(columns)
        while (mineField[randRow][randCol] == SAFE_CELL) {
            println("$randRow $randCol |")
            mineField[randRow][randCol] = MINE
            minesRemaining -= 1
        }

    }
    for (row in 0 until rows) {
        for (col in 0 until columns) {
            val minesAround = checkMineAround(row, col, mineField)
            if (minesAround != -1 && minesAround != 0) mineField[row][col] = minesAround.toString()
        }
    }

    return mineField
}

fun startGame(mf: MutableList<MutableList<String>>, playerCopy: MutableList<MutableList<String>>, mines: Int) {
    var minesRemaining = mines
    while (minesRemaining != 0) {
        println("Set/delete mines marks (x and y coordinates):")
        val (sy, sx, command) = readln().split(" ")
        val y = sy.toInt()
        val x = sx.toInt()
        if (!(x in 1 .. mf.size && y in 1 .. mf[0].size)) continue
        val cell = mf[x - 1][y - 1]
        val playerCell = playerCopy[x - 1][y - 1]
        if (command == "mine") {
            if ((playerCell != MARK && playerCell != EXPLORED_CELL)) {
                playerCopy[x - 1][y - 1] = MARK
                if (cell == MINE) minesRemaining -= 1
            } else {
                playerCopy[x - 1][y - 1] = SAFE_CELL
                if(cell == MINE) minesRemaining += 1
            }
        }

        if (command == "free") {
            println("value at $x $y : $cell")
            if (cell == MINE) {
                playerCopy[x - 1][y - 1] = MINE
                printMineField(playerCopy, repString = ".")
                println("You stepped on a mine and failed!")
                return
            } else if (cell.first().isDigit()) playerCopy[x - 1][y - 1] = cell
            else if (cell == SAFE_CELL) {
                floodFill(x - 1, y - 1, mf, playerCopy)
            }
        }
        printMineField(playerCopy)
    }
    println("Congratulations! You found all the mines!")
}

fun main() {
    println("How many mines do you want on the field?")
    val mines = readln().toInt()
    val mineField = generateMineField(mines, 9, 9)
    val playerCopy = MutableList(9) { MutableList(9) { SAFE_CELL } }
    printMineField(mineField)
    printMineField(playerCopy)
    startGame(mineField, playerCopy, mines)
}