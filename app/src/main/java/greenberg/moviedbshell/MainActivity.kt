package greenberg.moviedbshell

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.base.BaseActivity
import greenberg.moviedbshell.base.BaseFragment.Companion.PAGE_ARGS
import greenberg.moviedbshell.extensions.hideKeyboard
import greenberg.moviedbshell.state.SearchResultsArgs
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_layout)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // navController = findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.d("onCreateOptionsMenu")
        menu.clear()
        menuInflater.inflate(R.menu.base_menu, menu)
        menu.findItem(R.id.search_results_fragment)?.let { setUpSearchListener(it) }
        menu.findItem(R.id.toggleDarkMode)?.let { setUpDarkModeToggle(it) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutFragment -> {
                navController.navigate(R.id.action_global_aboutFragment)
                true
            }
            R.id.multi_search_menu_option -> {
                navController.navigate(R.id.action_global_multiSearchFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpSearchListener(item: MenuItem) {
        val searchView = item.actionView as? SearchView
        searchView?.apply {
            queryHint = resources.getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Timber.d("onQueryTextSubmit: $query")
                    if (query != null) {
                        item.collapseActionView()
                        searchView.hideKeyboard()
                        searchView.clearFocus()
                        navController.navigate(
                            R.id.action_global_searchResultsFragment,
                            Bundle().apply {
                                putParcelable(PAGE_ARGS, SearchResultsArgs(query))
                            },
                            NavOptions.Builder().setPopUpTo(R.id.searchResultsFragment, true)
                                .build()
                        )
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Timber.d("onQueryTextChange: $newText")
                    return false
                }
            })
        }
    }

    private fun setUpDarkModeToggle(item: MenuItem) {
        item.setOnMenuItemClickListener {
            (application as ZephyrrApplication).toggleNightMode()
            true
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun log(message: String) {
        Timber.d(message)
    }
}
