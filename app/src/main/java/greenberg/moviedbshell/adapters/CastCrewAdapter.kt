package greenberg.moviedbshell.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.state.CrewStateArgs
import greenberg.moviedbshell.view.CastFragment
import greenberg.moviedbshell.view.CrewFragment

class CastCrewAdapter(
    fragment: Fragment,
    private val castStateArgs: CastStateArgs,
    private val crewStateArgs: CrewStateArgs
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CastFragment().apply {
                arguments = Bundle().apply {
//                    putParcelable(Mavericks.KEY_ARG, castStateArgs)
                }
            }
            1 -> CrewFragment().apply {
                arguments = Bundle().apply {
//                    putParcelable(Mavericks.KEY_ARG, crewStateArgs)
                }
            }
            // TODO: probably make a null fragment for this, but we won't hit it for now.
            else -> Fragment()
        }
    }
}
