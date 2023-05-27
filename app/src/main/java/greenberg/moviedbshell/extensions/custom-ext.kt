package greenberg.moviedbshell.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.rootView.windowToken, 0)
}

fun View.flipVisibility() = if (this.isVisible) {
    this.visibility = View.GONE
} else {
    this.visibility = View.VISIBLE
}
