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
import android.view.*
import android.widget.*
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.CarListFragment.Companion.CAR_NUMBER_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.JSON_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_METHOD_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_URL_BUNDLE_KEY
import ua.alex.carsharingapp.data.Car
import ua.alex.carsharingapp.data.Insurance
import ua.alex.carsharingapp.data.Model
import yuku.ambilwarna.AmbilWarnaDialog

/**
 * A simple [Fragment] subclass.
 *
 */
class CarFragment : Fragment() {

    companion object {
        private const val CAR_EDIT_LOADER_ID = -1
        private const val CAR_LOADER_ID = 1
        private const val MODEL_LIST_LOADER_ID = 2
        private const val INSURANCE_LIST_LOADER_ID = 3
        private const val CAR_REQUEST_URL = "/api/cars/car_number="
        private const val MODEL_LIST_REQUEST_URL = "/api/models/getAllModels"
        private const val INSURANCE_LIST_REQUEST_URL = "/api/insurances/getAllInsurances"
        private const val PUT_CAR_REQUEST_URL = "/api/cars"
    }

    lateinit var modelList: List<Model>
    lateinit var insuranceList: List<Insurance>
    lateinit var car: Car
    lateinit var colorButton: Button

    var carNumber: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        carNumber = try {
            arguments.getString(CAR_NUMBER_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

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

        val bundleModelList = Bundle()
        bundleModelList.putString(REQUEST_URL_BUNDLE_KEY, MODEL_LIST_REQUEST_URL)
        bundleModelList.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundleModelList.putString(JSON_BUNDLE_KEY, "")

        val bundleInsuranceList = Bundle()
        bundleInsuranceList.putString(REQUEST_URL_BUNDLE_KEY, INSURANCE_LIST_REQUEST_URL)
        bundleInsuranceList.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundleInsuranceList.putString(JSON_BUNDLE_KEY, "")

        loaderManager.initLoader<List<Model>>(MODEL_LIST_LOADER_ID, bundleModelList, loaderCallbackModelList).forceLoad()
        loaderManager.initLoader<List<Insurance>>(INSURANCE_LIST_LOADER_ID, bundleInsuranceList, loaderCallbackInsuranceList).forceLoad()

        if (carNumber != null) {
            val bundleCar = Bundle()
            bundleCar.putString(REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
            bundleCar.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundleCar.putString(JSON_BUNDLE_KEY, "")

            loaderManager.initLoader<Car>(CAR_LOADER_ID, bundleCar, loaderCallbackCar).forceLoad()
        }
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
                val deleteBundle = Bundle()
                deleteBundle.putString(REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                deleteBundle.putString(JSON_BUNDLE_KEY, "")
                deleteBundle.putString(REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
                if (loaderManager.getLoader<Car>(CAR_LOADER_ID) == null)
                    loaderManager.initLoader<Car>(CAR_LOADER_ID, deleteBundle, loaderCallbackCar).forceLoad()
                else
                    loaderManager.restartLoader<Car>(CAR_LOADER_ID, deleteBundle, loaderCallbackCar).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
                val number: String = view.findViewById<TextInputEditText>(R.id.card_number_edit_text).text.toString()
                val fuelCardNumber: String = view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).text.toString()
                val address: String = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
                val color: String = (view.findViewById<Button>(R.id.color_button).background as ColorDrawable).color.toString()
                val status = view.findViewById<Switch>(R.id.status_switch).isChecked
                val date = view.findViewById<TextView>(R.id.date_text_view).text.toString()
                val model = modelList.find { it.name == (view.findViewById<Spinner>(R.id.model_spinner).selectedItem as String) }
                val insurance = insuranceList.find { it.series == (view.findViewById<Spinner>(R.id.insurance_spinner).selectedItem as String) }

                val json = ObjectMapper().writeValueAsString(
                        try {
                            Car(number, fuelCardNumber, address, color, status, date, model!!, insurance!!)
                        } catch (e: UninitializedPropertyAccessException) {
                            Car(number, fuelCardNumber, address, color, status, "2000-03-02", model!!, insurance!!)
                        }
                )

                val bundle = Bundle()
                bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "PUT")
                bundle.putString(JSON_BUNDLE_KEY, json)
                bundle.putString(REQUEST_URL_BUNDLE_KEY, PUT_CAR_REQUEST_URL)

                if (loaderManager.getLoader<Car>(CAR_LOADER_ID) == null)
                    loaderManager.initLoader<Car>(CAR_LOADER_ID, bundle, loaderCallbackCar).forceLoad()
                else
                    loaderManager.restartLoader<Car>(CAR_LOADER_ID, bundle, loaderCallbackCar).forceLoad()

                if (carNumber != null && carNumber != number) {
                    val deleteBundle = Bundle()
                    deleteBundle.putString(REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                    deleteBundle.putString(JSON_BUNDLE_KEY, "")
                    deleteBundle.putString(REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + carNumber)
                    if (loaderManager.getLoader<Car>(CAR_EDIT_LOADER_ID) == null)
                        loaderManager.initLoader<Car>(CAR_EDIT_LOADER_ID, deleteBundle, loaderCallbackCar).forceLoad()
                    else
                        loaderManager.restartLoader<Car>(CAR_EDIT_LOADER_ID, deleteBundle, loaderCallbackCar).forceLoad()
                }
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
        view.findViewById<Switch>(R.id.status_switch).isChecked = car.status
        view.findViewById<TextView>(R.id.date_text_view).text = car.creatingDate
        for (i in 0 until view.findViewById<Spinner>(R.id.model_spinner).count) {
            if (view.findViewById<Spinner>(R.id.model_spinner).getItemAtPosition(i) == car.model.name)
                view.findViewById<Spinner>(R.id.model_spinner).setSelection(i)
        }
        for (j in 0 until view.findViewById<Spinner>(R.id.insurance_spinner).count) {
            if (view.findViewById<Spinner>(R.id.insurance_spinner).getItemAtPosition(j) == car.insurance.series)
                view.findViewById<Spinner>(R.id.insurance_spinner).setSelection(j)
        }
    }

    private fun updateModelSpinnerUi(modelList: List<Model>) {
        view.findViewById<Spinner>(R.id.model_spinner).adapter =
                ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line,
                        modelList.map { it.name })
    }

    private fun updateInsuranceSpinnerUi(insuranceList: List<Insurance>) {
        view.findViewById<Spinner>(R.id.insurance_spinner).adapter =
                ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line,
                        insuranceList.map { it.series })
    }

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

    private val loaderCallbackModelList: LoaderManager.LoaderCallbacks<List<Model>> = object : LoaderManager.LoaderCallbacks<List<Model>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Model>> {
            return GetModelListLoader(activity,
                    p1!!.getString(REQUEST_URL_BUNDLE_KEY),
                    p1.getString(REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Model>>?, p1: List<Model>?) {
            if (p1 != null) {
                modelList = p1
                updateModelSpinnerUi(p1)
            }
        }

        override fun onLoaderReset(p0: Loader<List<Model>>?) {
        }
    }

    private val loaderCallbackInsuranceList: LoaderManager.LoaderCallbacks<List<Insurance>> = object : LoaderManager.LoaderCallbacks<List<Insurance>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Insurance>> {
            return GetInsuranceListLoader(activity,
                    p1!!.getString(REQUEST_URL_BUNDLE_KEY),
                    p1.getString(REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Insurance>>?, p1: List<Insurance>?) {
            if (p1 != null) {
                insuranceList = p1
                updateInsuranceSpinnerUi(p1)
            }
        }

        override fun onLoaderReset(p0: Loader<List<Insurance>>?) {
        }
    }

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

    private class GetModelListLoader(context: Context,
                                     val stringUrl: String,
                                     val requestMethod: String,
                                     val stringJson: String) : AsyncTaskLoader<List<Model>>(context) {
        override fun loadInBackground(): List<Model> {
            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Model::class.java)
            return ObjectMapper().readValue(carJson, type)
        }
    }

    private class GetInsuranceListLoader(context: Context,
                                         val stringUrl: String,
                                         val requestMethod: String,
                                         val stringJson: String) : AsyncTaskLoader<List<Insurance>>(context) {
        override fun loadInBackground(): List<Insurance> {
            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Insurance::class.java)
            return ObjectMapper().readValue(carJson, type)
        }
    }
}
