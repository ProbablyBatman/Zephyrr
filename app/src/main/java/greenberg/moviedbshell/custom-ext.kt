package greenberg.moviedbshell

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.Locale

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.rootView.windowToken, 0)
}

// Investigate adding this to a presenter extension function file instead of a generic one
fun processReleaseDate(releaseDate: String): String =
        if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            outputFormat.format(date)
        } else {
            ""
        }