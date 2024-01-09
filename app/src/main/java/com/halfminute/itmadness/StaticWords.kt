import com.halfminute.itmadness.Word
import com.halfminute.itmadness.Words

enum class Difficulty {
    EASY, MEDIUM, HARD
}

fun getStaticWordsData(): Words {
    val easyWords = listOf(
        Word("Bonjour", "Goeiedag", "Hello", "Hallo"),
    )
    val mediumWords = listOf(
        Word("Bonjour", "Goeiedag", "Hello", "Hallo"),
    )
    val hardWords = listOf(
        Word("Bonjour", "Goeiedag", "Hello", "Hallo"),
    )
    return Words(easyWords, mediumWords, hardWords)
}
