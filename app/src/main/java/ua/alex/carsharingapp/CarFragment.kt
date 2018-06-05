package ua.alex.carsharingapp


import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.*
import android.widget.Button
import com.fasterxml.jackson.databind.ObjectMapper
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import ua.alex.carsharingapp.CarListFragment.Companion.CAR_NUMBER_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_METHOD_BUNDLE_KEY
import ua.alex.carsharingapp.data.Car
import yuku.ambilwarna.AmbilWarnaDialog

/**
 * A simple [Fragment] subclass.
 *
 */
class CarFragment : Fragment(), ColorPickerDialogListener, LoaderManager.LoaderCallbacks<Car> {

    companion object {
        private const val CAR_REQUEST_URL = "/api/cars/car_number="
    }

    lateinit var colorButton: Button

    var carNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        carNumber = arguments.getString(CAR_NUMBER_BUNDLE_KEY)

        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorButton = view!!.findViewById(R.id.color_button)
        colorButton.setOnClickListener {
            //            ColorPickerDialog.newBuilder().setColor(0).show(activity)
            val drawable = (colorButton.background)
            val colorDrawable = if (drawable is ColorDrawable)
                drawable.color
            else 0
            AmbilWarnaDialog(activity, colorDrawable, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
                }

            }).show()
        }
    }

    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString(CAR_NUMBER_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
        bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
        loaderManager.initLoader<Car>(0, bundle, this@CarFragment).forceLoad()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.car_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            android.R.id.home -> {
                activity!!.fragmentManager.popBackStack()
                true
            }
            R.id.delete_car_item_menu -> {
                val bundle = Bundle()
                bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                bundle.putString(CAR_NUMBER_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
                if (loaderManager.getLoader<Car>(0) == null)
                    loaderManager.initLoader<Car>(0, bundle, this@CarFragment).forceLoad()
                else
                    loaderManager.restartLoader<Car>(0, bundle, this@CarFragment).forceLoad()
                activity!!.fragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Car> {
        return GetCarLoader(activity,
                p1!!.getString(CAR_NUMBER_BUNDLE_KEY),
                p1.getString(REQUEST_METHOD_BUNDLE_KEY))
    }

    override fun onLoadFinished(p0: Loader<Car>?, p1: Car?) {
        if (p1 != null) {
            updateUi(p1)
        }
    }

    override fun onLoaderReset(p0: Loader<Car>?) {
    }

    private fun updateUi(car: Car) {
        view.findViewById<TextInputEditText>(R.id.card_number_edit_text).setText(car.number)
        view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).setText(car.fuelCardNumber)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(car.address)
    }

    private class GetCarLoader(context: Context, val stringUrl: String, val requestMethod: String) : AsyncTaskLoader<Car>(context) {
        override fun loadInBackground(): Car? {
            val carJson = QueryUtils.fetchData(stringUrl, requestMethod)
            return if (carJson == "")
                return null
            else
                ObjectMapper().readValue(carJson, Car::class.java)
        }
    }
}
