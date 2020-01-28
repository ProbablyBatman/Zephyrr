package greenberg.moviedbshell.extensions

import android.content.Context
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvDetailItem
import greenberg.moviedbshell.models.ui.TvItem
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Context.processRatingInfo(voteAverage: Double, voteCount: Int): String {
    val formattedAverage: String = formatRatings(voteAverage)
    return this.getString(R.string.user_rating_substitution, formattedAverage, voteCount)
}

// TODO: it's probably still worth looking into if there's a better way to do this
private fun formatRatings(voteAverage: Double?): String {
    val doubleFormat: NumberFormat = DecimalFormat("##.##")
    return doubleFormat.format(voteAverage)
}

fun Context.processRuntime(runtime: Int): String = this.getString(R.string.runtime_substitution, runtime.toString())

fun Context.processRuntimes(runtime: List<Int>): String {
    val allRuntimes = runtime.joinToString(", ")
    return this.getString(R.string.runtime_substitution, allRuntimes)
}

fun Context.processGenreTitle(genresListSize: Int): String = this.resources.getQuantityString(R.plurals.genres_title, genresListSize)

// TODO: I have future plans for this. For now it stays stupid.
fun Context.processGenres(genres: List<String?>): String = genres.joinToString(", ")

// TODO: Investigate if there's a less dumb way to do this
fun String.processAsReleaseDate(): String =
        if (this.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(this)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }

// TODO: Investigate if there's a less way to do this, it's identical to processDate for now
// it should be different
fun String.processDate(): String =
        if (this.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = inputFormat.parse(this)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(formattedDate)
        } else {
            ""
        }

// TODO: probably make sure every date is like this?
// there has to be a better way to do this
fun processAge(birthday: String, deathday: String = ""): Int {
    var age: Int
    when {
        birthday.isNotEmpty() && deathday.isNotEmpty() -> {
            val splitBirthday = birthday.split("-").map { it.toInt() }
            val formattedBirthday = Calendar.getInstance().apply { set(splitBirthday[0], splitBirthday[1], splitBirthday[2]) }
            val splitDeathday = deathday.split("-").map { it.toInt() }
            val formattedDeathday = Calendar.getInstance().apply { set(splitDeathday[0], splitDeathday[1], splitDeathday[2]) }
            age = formattedDeathday.get(Calendar.YEAR) - formattedBirthday.get(Calendar.YEAR)
            if (formattedDeathday.get(Calendar.DAY_OF_YEAR) < formattedBirthday.get(Calendar.DAY_OF_YEAR)) {
                age -= 1
            }
        }
        birthday.isNotEmpty() -> {
            val splitDate = birthday.split("-").map { it.toInt() }
            val formattedBirthday = Calendar.getInstance().apply { set(splitDate[0], splitDate[1], splitDate[2]) }
            val today = Calendar.getInstance()
            age = today.get(Calendar.YEAR) - formattedBirthday.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < formattedBirthday.get(Calendar.DAY_OF_YEAR)) {
                age -= 1
            }
        }
        else -> {
            age = -1
        }
    }
    return age
}

fun Context.processLastOrNextAirDateTitle(tvDetailItem: TvDetailItem): String? {
    return when {
        tvDetailItem.nextAirDate != null -> this.getString(R.string.next_air_date_title)
        tvDetailItem.lastAirDate != null -> this.getString(R.string.last_aired_title)
        else -> null
    }
}

fun TvDetailItem.processLastOrNextAirDate(): String? {
    return when {
        this.nextAirDate != null -> this.nextAirDate.processDate()
        this.lastAirDate != null -> this.lastAirDate.processDate()
        else -> null
    }
}

fun List<PreviewItem>.processKnownForItems(): String {
    return "Most Known for: \n" +
            this.joinToString(separator = "\n") { item ->
                when (item) {
                    is MovieItem -> {
                        "Movie: ${item.movieTitle}, ${item.releaseDate.processAsReleaseDate()}"
                    }
                    is TvItem -> {
                        "TV show: ${item.name}, ${item.firstAirDate.processAsReleaseDate()}"
                    }
                    else -> ""
                }
            }
}
