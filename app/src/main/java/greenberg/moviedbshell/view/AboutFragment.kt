package greenberg.moviedbshell.view

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import greenberg.moviedbshell.R
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import greenberg.moviedbshell.BuildConfig
import java.util.Calendar

class AboutFragment : Fragment() {

    private var privacyPolicyLink: TextView? = null
    private var termsAndConditionsLink: TextView? = null
    private var aboutMeTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        privacyPolicyLink = view.findViewById(R.id.privacy_policy_link)
        termsAndConditionsLink = view.findViewById(R.id.terms_and_conditions_link)
        aboutMeTextView = view.findViewById(R.id.about_me_text)
        show()
    }

    private fun show() {
        setUpLinks()
        aboutMeTextView?.text = resources.getString(R.string.about_me,
                Calendar.getInstance().get(Calendar.YEAR).toString(),
                BuildConfig.VERSION_NAME)
    }

    private fun setUpLinks() {
        val privacyPolicyUrl = resources.getString(R.string.privacy_policy_url)
        val privacySpannableString = SpannableString(privacyPolicyUrl)
        privacySpannableString.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(browserIntent)
            }
        }, 0, privacyPolicyUrl.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        privacyPolicyLink?.text = privacySpannableString
        privacyPolicyLink?.movementMethod = LinkMovementMethod.getInstance()

        val termsAndConditionsUrl = resources.getString(R.string.terms_and_conditions_url)
        val termsSpannableString = SpannableString(termsAndConditionsUrl)
        termsSpannableString.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsAndConditionsUrl))
                startActivity(browserIntent)
            }
        }, 0, termsAndConditionsUrl.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        termsAndConditionsLink?.text = termsSpannableString
        termsAndConditionsLink?.movementMethod = LinkMovementMethod.getInstance()
    }
}
