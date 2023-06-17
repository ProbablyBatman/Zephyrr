package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PosterItem(
    val aspectRatio: Double,
    val filePath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val width: Int,
    val height: Int,
) : Parcelable
