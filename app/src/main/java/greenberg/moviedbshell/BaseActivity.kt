package greenberg.moviedbshell

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import greenberg.moviedbshell.MosbyImpl.PopularMoviesFragment
import greenberg.moviedbshell.MosbyImpl.SearchResultsFragment

class BaseActivity : AppCompatActivity() {
    //TODO: probably only inherit from this.  For now, this isn't an issue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_layout)

        if (findViewById<FrameLayout>(R.id.fragment_container) != null) {
            val popularMoviesFragment = PopularMoviesFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, popularMoviesFragment)
                    .addToBackStack(PopularMoviesFragment.TAG)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.w("Testing", "Oncreateoptionsmenu")
        menu?.clear()
        menuInflater?.inflate(R.menu.base_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.w("Testing", "onOptionsItemSelected")
        when (item?.itemId) {
            R.id.base_search -> {
                val searchView = item.actionView as? SearchView
                searchView?.apply {
                    queryHint = resources.getString(R.string.search_hint)
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            if (query?.isNotBlank() == true) {
                                val searchFragment = SearchResultsFragment()
                                val bundle = Bundle().apply {
                                    putString("Query", query.toString())
                                }
                                searchFragment.arguments = bundle
                                supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, searchFragment)
                                        .addToBackStack(SearchResultsFragment.TAG)
                                        .commit()
                            }
                            //Close keyboard and collapse search
                            //TODO: figure out how to just close keyboard and stay in search view?
                            this@apply.clearFocus()
                            item.collapseActionView()
                            return true
                        }
                    })
                }
            }
        }
        return true
    }
}