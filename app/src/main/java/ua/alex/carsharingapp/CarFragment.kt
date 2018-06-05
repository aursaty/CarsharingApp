package ua.alex.carsharingapp


import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import ua.alex.carsharingapp.CarListFragment.Companion.CAR_NUMBER_BUNDLE_KEY
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

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
        loaderManager.initLoader<Car>(0, bundle, this@CarFragment).forceLoad()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Car> {
        return CarLoader(activity, p1!!.getString(CAR_NUMBER_BUNDLE_KEY))
    }

    override fun onLoadFinished(p0: Loader<Car>?, p1: Car?) {
        updateUi(p1!!)
    }

    override fun onLoaderReset(p0: Loader<Car>?) {
    }

    private fun updateUi(car: Car) {
        view.findViewById<TextInputEditText>(R.id.card_number_edit_text).setText(car.number)
        view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).setText(car.fuelCardNumber)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(car.address)
    }

    private class CarLoader(context: Context, val stringUrl: String) : AsyncTaskLoader<Car>(context) {
        override fun loadInBackground(): Car {
            val carJson = QueryUtils.fetchData(stringUrl)
            return ObjectMapper().readValue(carJson, Car::class.java)
        }

    }
}
