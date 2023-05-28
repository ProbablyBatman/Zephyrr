package greenberg.moviedbshell.extensions

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.MovieDetailArgs

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.rootView.windowToken, 0)
}

fun View.flipVisibility() = if (this.isVisible) {
    this.visibility = View.GONE
} else {
    this.visibility = View.VISIBLE
}

inline fun <reified T> Bundle?.extractArguments(key: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this?.getParcelable(key, T::class.java)
    } else {
        (this?.getParcelable(BaseFragment.PAGE_ARGS) as? T)
    }
