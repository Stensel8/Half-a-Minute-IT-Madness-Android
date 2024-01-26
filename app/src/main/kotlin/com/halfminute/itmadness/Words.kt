import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.halfminute.itmadness.R

enum class Difficulty {
    EASY, MEDIUM, HARD
}

data class Word(
    @SerializedName("frWord") val frWord: String,
    @SerializedName("nlWord") val nlWord: String,
    @SerializedName("enWord") val enWord: String,
    @SerializedName("deWord") val deWord: String
)

data class Words(
    @SerializedName("easyWords") val easyWords: List<Word>,
    @SerializedName("mediumWords") val mediumWords: List<Word>,
    @SerializedName("hardWords") val hardWords: List<Word>
) {
    fun getWordsByDifficulty(difficulty: Difficulty): List<Word> {
        return when (difficulty) {
            Difficulty.EASY -> easyWords
            Difficulty.MEDIUM -> mediumWords
            Difficulty.HARD -> hardWords
        }
    }

    companion object {
        fun getInstance(context: Context): Words? {
            return try {
                val inputStream = context.resources.openRawResource(R.raw.wordjsondata)
                val jsonContent = inputStream.bufferedReader().use { it.readText() }
                Gson().fromJson(jsonContent, Words::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class LanguageWord(
    @SerializedName("frWord") val frWord: String,
    @SerializedName("nlWord") val nlWord: String,
    @SerializedName("enWord") val enWord: String,
    @SerializedName("deWord") val deWord: String
)

data class LanguageWords(
    @SerializedName("easyWords") val easyWords: List<LanguageWord>,
    @SerializedName("mediumWords") val mediumWords: List<LanguageWord>,
    @SerializedName("hardWords") val hardWords: List<LanguageWord>
) {
    fun getWordsByDifficulty(difficulty: Difficulty): List<LanguageWord> {
        return when (difficulty) {
            Difficulty.EASY -> easyWords
            Difficulty.MEDIUM -> mediumWords
            Difficulty.HARD -> hardWords
        }
    }

    companion object {
        fun getInstance(context: Context): LanguageWords? {
            return try {
                val inputStream = context.resources.openRawResource(R.raw.language_wordjsondata)
                val jsonContent = inputStream.bufferedReader().use { it.readText() }
                Gson().fromJson(jsonContent, LanguageWords::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
