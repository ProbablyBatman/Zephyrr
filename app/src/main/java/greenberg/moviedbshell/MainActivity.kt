package greenberg.moviedbshell

import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import greenberg.moviedbshell.base.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    //TODO: probably only inherit from this.  For now, this isn't an issue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_layout)

        /*if (findViewById<FrameLayout>(R.id.fragment_container) != null) {
            if (supportFragmentManager.findFragmentByTag(PopularMoviesFragment.TAG) == null) {
                val popularMoviesFragment = PopularMoviesFragment()

                supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, popularMoviesFragment, PopularMoviesFragment.TAG)
                        .commit()
            }
        }*/

        navController = findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Timber.d("onCreateOptionsMenu")
        menu?.clear()
        menuInflater?.inflate(R.menu.base_menu, menu)
        menu?.findItem(R.id.searchResultsFragment)?.let {
            setUpSearchListener(it)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Timber.d("onOptionsItemSelected")
        when (item?.itemId) {
            R.id.searchResultsFragment -> {
                setUpSearchListener(item)
            }
        }
        return false
    }

    private fun setUpSearchListener(item: MenuItem) {
        val searchView = item.actionView as? SearchView
        searchView?.apply {
            queryHint = resources.getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Timber.d("onQueryTextSubmit: $query")
                    //searchView.isIconified = true
                    item.collapseActionView()
                    searchView.hideKeyboard()
                    searchView.clearFocus()
                    navController.navigate(R.id.searchResultsFragment, Bundle().apply {
                        putString("Query", query)
                    })
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Timber.d("onQueryTextChange: $newText")
                    return false
                }

            })
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}