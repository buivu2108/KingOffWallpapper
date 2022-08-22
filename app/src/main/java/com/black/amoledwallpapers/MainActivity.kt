package com.black.amoledwallpapers

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.black.amoledwallpapers.fragment.PurchaseFragment
import com.black.amoledwallpapers.rate.AppRater
import com.black.amoledwallpapers.screens.main.FavoriteFragment
import com.black.amoledwallpapers.screens.main.PopularFragment
import com.black.amoledwallpapers.screens.search.SearchActivity
import com.wyt.searchbox.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, Toolbar.OnMenuItemClickListener {
    private val TABS = App.get().resources.getStringArray(R.array.home_tabs)
    private lateinit var viewModel: MainViewModel
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        vpMain.adapter = MainAdapter(supportFragmentManager)
        vpMain.addOnPageChangeListener(this)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_hot -> vpMain.currentItem = 0
                R.id.nav_favorite -> vpMain.currentItem = 1
            }
            true
        }
        toolbar.setOnMenuItemClickListener(this)
        searchFragment = SearchFragment.newInstance()
        searchFragment.setOnSearchClickListener {
            SearchActivity.search(this, it)
        }
        icPurchase.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, PurchaseFragment())
                .addToBackStack("purchase_fragment")
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        searchFragment.showFragment(supportFragmentManager, "")
        return true
    }


    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        bottomNavigationView.menu.getItem(position).isChecked = true
        title = TABS[position]
    }

    override fun onBackPressed() {
        if (!AppRater.StarBuilder(this@MainActivity, packageName)
                .showDefault()
                .minimumNumberOfStars(4)
                .timesToLaunch(0)
                .daysToWait(0)
                .neverButton(null, null)
                .notNowButton(
                    getString(R.string.star_not_now_button_text)
                ) { _, _ -> }
                .rateButton(getString(R.string.star_rate_button_text))
                .message(getString(R.string.default_message))
                .appLaunched()
        )
            super.onBackPressed()
    }

    class MainAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PopularFragment()
                else -> FavoriteFragment()
            }
        }

        override fun getCount(): Int = 2
    }
}
