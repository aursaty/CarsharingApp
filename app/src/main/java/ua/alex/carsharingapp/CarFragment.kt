package ua.alex.carsharingapp


import android.app.Activity
import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.CarListFragment.Companion.CAR_NUMBER_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.JSON_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_METHOD_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_URL_BUNDLE_KEY
import ua.alex.carsharingapp.data.Car
import ua.alex.carsharingapp.data.Model
import yuku.ambilwarna.AmbilWarnaDialog

/**
 * A simple [Fragment] subclass.
 *
 */
class CarFragment : Fragment() {

    companion object {
        private const val CAR_REQUEST_URL = "/api/cars/car_number="
        private const val PUT_CAR_REQUEST_URL = "/api/cars"
    }

    lateinit var car: Car
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
        bundle.putString(REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
        bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundle.putString(JSON_BUNDLE_KEY, "")
        loaderManager.initLoader<Car>(0, bundle, loaderCallbackCar).forceLoad()
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
                bundle.putString(JSON_BUNDLE_KEY, "")
                bundle.putString(REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
                if (loaderManager.getLoader<Car>(0) == null)
                    loaderManager.initLoader<Car>(0, bundle, loaderCallbackCar).forceLoad()
                else
                    loaderManager.restartLoader<Car>(0, bundle, loaderCallbackCar).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
                val carNumber: String = view.findViewById<TextInputEditText>(R.id.card_number_edit_text).text.toString()
                val fuelCardNumber: String = view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).text.toString()
                val address: String = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
                val color: String = (view.findViewById<Button>(R.id.color_button).background as ColorDrawable).color.toString()
                val status = view.findViewById<Switch>(R.id.status_switch).isChecked
                val date = view.findViewById<TextView>(R.id.date_text_view).text.toString()
                val model = view.findViewById<TextView>(R.id.model_text_view).text
                val insurance = view.findViewById<TextView>(R.id.insurance_text_view).text

                val json = ObjectMapper().writeValueAsString(
                        Car(carNumber, fuelCardNumber, address, color, status, car.creatingDate, car.model, car.insurance)
                )

                val bundle = Bundle()
                bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "PUT")
                bundle.putString(JSON_BUNDLE_KEY, json)
                bundle.putString(REQUEST_URL_BUNDLE_KEY, PUT_CAR_REQUEST_URL)

                if (loaderManager.getLoader<Car>(0) == null)
                    loaderManager.initLoader<Car>(0, bundle, loaderCallbackCar).forceLoad()
                else
                    loaderManager.restartLoader<Car>(0, bundle, loaderCallbackCar).forceLoad()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(car: Car) {
        view.findViewById<TextInputEditText>(R.id.card_number_edit_text).setText(car.number)
        view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).setText(car.fuelCardNumber)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(car.address)
        view.findViewById<Button>(R.id.color_button).setBackgroundColor(Integer.parseInt(car.color))
    }

//    private val loaderCallbackCarList: LoaderManager.LoaderCallbacks<List<Car>> = object : LoaderManager.LoaderCallbacks<List<Car>> {
//    }

    private val loaderCallbackCar: LoaderManager.LoaderCallbacks<Car> = object : LoaderManager.LoaderCallbacks<Car> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Car> {
            return GetCarLoader(activity,
                    p1!!.getString(REQUEST_URL_BUNDLE_KEY),
                    p1.getString(REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Car>?, p1: Car?) {
            if (p1 != null) {
                car = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Car>?) {
        }
    }

//    private val loaderCallbackCarList: LoaderManager.LoaderCallbacks<List<Model>> = object : LoaderManager.LoaderCallbacks<List<Model>> {
//        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Model>> {
//            return GetCarLoader(activity,
//                    p1!!.getString(REQUEST_URL_BUNDLE_KEY),
//                    p1.getString(REQUEST_METHOD_BUNDLE_KEY),
//                    p1.getString(JSON_BUNDLE_KEY))
//        }
//
//        override fun onLoadFinished(p0: Loader<List<Model>>?, p1: List<Model>?) {
//            if (p1 != null) {
//                car = p1
//                updateUi(p1)
//            } else {
//                MyHandle(activity).sendEmptyMessage(1)
//            }
//        }
//
//        override fun onLoaderReset(p0: Loader<List<Model>>?) {
//        }
//    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetCarLoader(context: Context,
                               val stringUrl: String,
                               val requestMethod: String,
                               val stringJson: String) : AsyncTaskLoader<Car>(context) {
        override fun loadInBackground(): Car? {
            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (carJson == "")
                return null
            else
                ObjectMapper().readValue(carJson, Car::class.java)
        }
    }

//    private class GetModelListLoader(context: Context,
//                               val stringUrl: String,
//                               val requestMethod: String,
//                               val stringJson: String) : AsyncTaskLoader<List<Model>>(context) {
//        override fun loadInBackground(): List<Model> {
//            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
//            return if (carJson == "")
//                return null
//            else
//                ObjectMapper().readValue(carJson, Car::class.java)
//        }
//    }
}
