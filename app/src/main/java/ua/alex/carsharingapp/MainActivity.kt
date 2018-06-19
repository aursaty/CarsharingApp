package ua.alex.carsharingapp

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    companion object {
        const val JSON_BUNDLE_KEY = "JSON_BUNDLE_KEY"
        const val REQUEST_METHOD_BUNDLE_KEY = "REQUEST_METHOD_BUNDLE_KEY"
        const val REQUEST_URL_BUNDLE_KEY = "REQUEST_URL_BUNDLE_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_drawer)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                findViewById(R.id.drawer_layout),
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

        }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nvView.setNavigationItemSelectedListener {
            var navFragment: Fragment? = null
            when (it.itemId) {
                R.id.nav_cars -> navFragment = CarListFragment()
                R.id.nav_models -> navFragment = ModelListFragment()
                R.id.nav_insurances -> navFragment = InsuranceListFragment()
                R.id.nav_clients -> navFragment = ClientListFragment()
                R.id.nav_operators -> navFragment = OperatorListFragment()
                R.id.nav_contracts -> navFragment = ContractListFragment()
            }
            if (navFragment != null)
                fragmentManager.beginTransaction().replace(R.id.content, navFragment).commit()
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val fragment: Fragment = CarListFragment()
        fragmentManager.beginTransaction().add(R.id.content, fragment).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
