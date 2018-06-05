package ua.alex.carsharingapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Fragment

class MainActivity : AppCompatActivity() {

    companion object {
        const val JSON_BUNDLE_KEY = "JSON_BUNDLE_KEY"
        const val REQUEST_METHOD_BUNDLE_KEY = "REQUEST_METHOD_BUNDLE_KEY"
        const val REQUEST_URL_BUNDLE_KEY = "REQUEST_URL_BUNDLE_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment : Fragment = CarListFragment()
        fragmentManager.beginTransaction().add(R.id.content, fragment).commit()
    }
}
