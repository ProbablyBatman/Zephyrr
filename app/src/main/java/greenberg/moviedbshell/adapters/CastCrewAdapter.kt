package greenberg.moviedbshell.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.airbnb.mvrx.MvRx
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.view.CastFragment

class CastCrewAdapter(
        fragment: Fragment,
        private val castStateArgs: CastStateArgs
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 1)
            return CastFragment()
        return CastFragment().apply { arguments = Bundle().apply { putParcelable(MvRx.KEY_ARG, castStateArgs) } }
    }
}