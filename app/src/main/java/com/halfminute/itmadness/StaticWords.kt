import com.halfminute.itmadness.Word
import com.halfminute.itmadness.Words

enum class Difficulty {
    EASY, MEDIUM, HARD
}

fun getStaticWordsData(): Words {
    val easyWords = listOf(
        Word("Dit is test 1", "Dit is test 2", "Dit is test 3", "Dit is test 4"),
    )
    val mediumWords = listOf(
        Word("Dit is test 1", "Dit is test 2", "Dit is test 3", "Dit is test 4"),
    )
    val hardWords = listOf(
        Word("Dit is test 1", "Dit is test 2", "Dit is test 3", "Dit is test 4"),
    )
    return Words(easyWords, mediumWords, hardWords)
}
