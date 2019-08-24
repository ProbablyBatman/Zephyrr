package greenberg.moviedbshell

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import greenberg.moviedbshell.base.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_layout)

        navController = findNavController(this, R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Timber.d("onCreateOptionsMenu")
        menu?.clear()
        menuInflater.inflate(R.menu.base_menu, menu)
        menu?.findItem(R.id.searchResultsFragment)?.let { setUpSearchListener(it) }
        menu?.findItem(R.id.toggleDarkMode)?.let { setUpDarkModeToggle(it) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.aboutFragment -> {
                navController.navigate(R.id.action_global_aboutFragment)
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
                    item.collapseActionView()
                    searchView.hideKeyboard()
                    searchView.clearFocus()
                    navController.navigate(R.id.searchResultsFragment, Bundle().apply {
                        putString("Query", query)
                    }, NavOptions.Builder().setPopUpTo(R.id.searchResultsFragment, true).build())
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