fun isVowel(char: Char): Boolean {
    val vowels = listOf('a', 'e', 'i', 'o', 'u', 'y')
    return vowels.contains(char)
}

fun createEuphonious(word: String): Int {
    var vowelStreak = 0
    var consonantStreak = 0
    var charsToAdd = 0
    for (char in word) {

        if (isVowel(char)) {
            vowelStreak += 1
            consonantStreak = 0
        } else {
            vowelStreak = 0
            consonantStreak += 1
        }
        if (consonantStreak == 3 || vowelStreak == 3) {
            charsToAdd += 1
            vowelStreak = 1
            consonantStreak = 1
        }
    }
//    println("$vowelStreak $consonantStreak")
    return charsToAdd
}

fun main(args: Array<String>) {
    val word = readln().lowercase()
    println(createEuphonious(word))
}