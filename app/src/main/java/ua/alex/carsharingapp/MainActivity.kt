package ua.alex.carsharingapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment : Fragment = CarListFragment()
        fragmentManager.beginTransaction().add(R.id.content, fragment).commit()
    }
}
