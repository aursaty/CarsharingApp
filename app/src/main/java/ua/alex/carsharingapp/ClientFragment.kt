package ua.alex.carsharingapp


import android.app.Activity
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.app.Fragment
import android.view.*
import android.widget.*
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.data.Client


/**
 * A simple [Fragment] subclass.
 *
 */
class ClientFragment : Fragment() {

    companion object {
        private const val EDIT_LOADER_ID = -1
        private const val LOADER_ID = 1
//        private const val MODEL_LIST_LOADER_ID = 2
//        private const val INSURANCE_LIST_LOADER_ID = 3
        private const val CLIENT_REQUEST_URL = "/api/clients/licenceNumber="
//        private const val MODEL_LIST_REQUEST_URL = "/api/models/getAllModels"
//        private const val INSURANCE_LIST_REQUEST_URL = "/api/insurances/getAllInsurances"
        private const val PUT_CAR_REQUEST_URL = "/api/clients"
    }

//    lateinit var modelList: List<Model>
//    lateinit var insuranceList: List<Insurance>
    lateinit var client: Client
//    lateinit var colorButton: Button

    var licenceNumber: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        licenceNumber = try {
            arguments.getString(ClientListFragment.CLIENT_NUMBER_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

        return inflater.inflate(R.layout.fragment_client, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        colorButton = view!!.findViewById(R.id.color_button)
//        colorButton.setOnClickListener {
//                        ColorPickerDialog.newBuilder().setColor(0).show(activity)
//            val drawable = (colorButton.background)
//            val colorDrawable = if (drawable is ColorDrawable)
//                drawable.color
//            else 0
//            AmbilWarnaDialog(activity, colorDrawable, object : AmbilWarnaDialog.OnAmbilWarnaListener {
//                override fun onCancel(dialog: AmbilWarnaDialog?) {
//                }
//
//                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
//                    view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
//                }
//            }).show()
//        }
    }

    override fun onStart() {
        super.onStart()

//        val bundleModelList = Bundle()
//        bundleModelList.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, MODEL_LIST_REQUEST_URL)
//        bundleModelList.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
//        bundleModelList.putString(MainActivity.JSON_BUNDLE_KEY, "")
//
//        val bundleInsuranceList = Bundle()
//        bundleInsuranceList.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, INSURANCE_LIST_REQUEST_URL)
//        bundleInsuranceList.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
//        bundleInsuranceList.putString(MainActivity.JSON_BUNDLE_KEY, "")

//        loaderManager.initLoader<List<Model>>(MODEL_LIST_LOADER_ID, bundleModelList, loaderCallbackModelList).forceLoad()
//        loaderManager.initLoader<List<Insurance>>(INSURANCE_LIST_LOADER_ID, bundleInsuranceList, loaderCallbackInsuranceList).forceLoad()

        if (licenceNumber != null) {
            val bundleClient = Bundle()
            bundleClient.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CLIENT_REQUEST_URL + licenceNumber)
            bundleClient.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundleClient.putString(MainActivity.JSON_BUNDLE_KEY, "")

            if (loaderManager.getLoader<Client>(LOADER_ID) == null)
                loaderManager.initLoader<Client>(LOADER_ID, bundleClient, loaderCallbackClient).forceLoad()
            else
                loaderManager.restartLoader<Client>(LOADER_ID, bundleClient, loaderCallbackClient).forceLoad()
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
                deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
                deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CLIENT_REQUEST_URL + licenceNumber)
                if (loaderManager.getLoader<Client>(LOADER_ID) == null)
                    loaderManager.initLoader<Client>(LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
                else
                    loaderManager.restartLoader<Client>(LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
//                val number: String = view.findViewById<TextInputEditText>(R.id.card_number_edit_text).text.toString()
//                val fuelCardNumber: String = view.findViewById<TextInputEditText>(R.id.fuel_card_number_edit_text).text.toString()
//                val address: String = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
//                val color: String = (view.findViewById<Button>(R.id.color_button).background as ColorDrawable).color.toString()
//                val status = view.findViewById<Switch>(R.id.status_switch).isChecked
//                val date = view.findViewById<TextView>(R.id.date_text_view).text.toString()
//                val model = modelList.find { it.name == (view.findViewById<Spinner>(R.id.model_spinner).selectedItem as String) }
//                val insurance = insuranceList.find { it.series == (view.findViewById<Spinner>(R.id.insurance_spinner).selectedItem as String) }
//
//                val json = ObjectMapper().writeValueAsString(
//                        try {
//                            Car(number, fuelCardNumber, address, color, status, date, model!!, insurance!!)
//                        } catch (e: UninitializedPropertyAccessException) {
//                            Car(number, fuelCardNumber, address, color, status, "2000-03-02", model!!, insurance!!)
//                        }
//                )
//
//                val bundle = Bundle()
//                bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "PUT")
//                bundle.putString(MainActivity.JSON_BUNDLE_KEY, json)
//                bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, PUT_CAR_REQUEST_URL)
//
//                if (loaderManager.getLoader<Car>(LOADER_ID) == null)
//                    loaderManager.initLoader<Car>(LOADER_ID, bundle, loaderCallbackClient).forceLoad()
//                else
//                    loaderManager.restartLoader<Car>(LOADER_ID, bundle, loaderCallbackClient).forceLoad()
//
//                if (licenceNumber != null && licenceNumber != number) {
//                    val deleteBundle = Bundle()
//                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
//                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
//                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CLIENT_REQUEST_URL + licenceNumber)
//                    if (loaderManager.getLoader<Car>(EDIT_LOADER_ID) == null)
//                        loaderManager.initLoader<Car>(EDIT_LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
//                    else
//                        loaderManager.restartLoader<Car>(EDIT_LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
//                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(client: Client) {
        view.findViewById<TextInputEditText>(R.id.licence_number_edit_text).setText(client.licenseNumber)
        view.findViewById<TextInputEditText>(R.id.full_name_edit_text).setText(client.fullName)
        view.findViewById<TextInputEditText>(R.id.credit_card_edit_text).setText(client.creditCardNumber)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(client.address)
        view.findViewById<TextInputEditText>(R.id.phone_edit_text).setText(client.phoneNumber)
        view.findViewById<TextView>(R.id.birthday_text_view).text = client.birthday
        view.findViewById<TextView>(R.id.date_text_view).text = client.registrationDate
//        for (i in 0 until view.findViewById<Spinner>(R.id.model_spinner).count) {
//            if (view.findViewById<Spinner>(R.id.model_spinner).getItemAtPosition(i) == client.model.name)
//                view.findViewById<Spinner>(R.id.model_spinner).setSelection(i)
//        }
//        for (j in 0 until view.findViewById<Spinner>(R.id.insurance_spinner).count) {
//            if (view.findViewById<Spinner>(R.id.insurance_spinner).getItemAtPosition(j) == client.insurance.series)
//                view.findViewById<Spinner>(R.id.insurance_spinner).setSelection(j)
//        }
    }

//    private fun updateModelSpinnerUi(modelList: List<Model>) {
//        view.findViewById<Spinner>(R.id.model_spinner).adapter =
//                ArrayAdapter<String>(activity,
//                        android.R.layout.simple_dropdown_item_1line,
//                        modelList.map { it.name })
//    }

//    private fun updateInsuranceSpinnerUi(insuranceList: List<Insurance>) {
//        view.findViewById<Spinner>(R.id.insurance_spinner).adapter =
//                ArrayAdapter<String>(activity,
//                        android.R.layout.simple_dropdown_item_1line,
//                        insuranceList.map { it.series })
//    }

    private val loaderCallbackClient: LoaderManager.LoaderCallbacks<Client> = object : LoaderManager.LoaderCallbacks<Client> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Client> {
            return GetClientLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Client>?, p1: Client?) {
            if (p1 != null) {
                client = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Client>?) {
        }
    }

//    private val loaderCallbackModelList: LoaderManager.LoaderCallbacks<List<Model>> = object : LoaderManager.LoaderCallbacks<List<Model>> {
//        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Model>> {
//            return GetModelListLoader(activity,
//                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
//                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
//                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
//        }
//
//        override fun onLoadFinished(p0: Loader<List<Model>>?, p1: List<Model>?) {
//            if (p1 != null) {
//                modelList = p1
//                updateModelSpinnerUi(p1)
//            }
//        }
//
//        override fun onLoaderReset(p0: Loader<List<Model>>?) {
//        }
//    }

//    private val loaderCallbackInsuranceList: LoaderManager.LoaderCallbacks<List<Insurance>> = object : LoaderManager.LoaderCallbacks<List<Insurance>> {
//        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Insurance>> {
//            return GetInsuranceListLoader(activity,
//                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
//                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
//                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
//        }
//
//        override fun onLoadFinished(p0: Loader<List<Insurance>>?, p1: List<Insurance>?) {
//            if (p1 != null) {
//                insuranceList = p1
//                updateInsuranceSpinnerUi(p1)
//            }
//        }
//
//        override fun onLoaderReset(p0: Loader<List<Insurance>>?) {
//        }
//    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetClientLoader(context: Context,
                                  val stringUrl: String,
                                  val requestMethod: String,
                                  val stringJson: String) : AsyncTaskLoader<Client>(context) {
        override fun loadInBackground(): Client? {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (json == "")
                return null
            else
                ObjectMapper().readValue(json, Client::class.java)
        }
    }

//    private class GetModelListLoader(context: Context,
//                                     val stringUrl: String,
//                                     val requestMethod: String,
//                                     val stringJson: String) : AsyncTaskLoader<List<Model>>(context) {
//        override fun loadInBackground(): List<Model> {
//            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
//            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Model::class.java)
//            return ObjectMapper().readValue(carJson, type)
//        }
//    }

//    private class GetInsuranceListLoader(context: Context,
//                                         val stringUrl: String,
//                                         val requestMethod: String,
//                                         val stringJson: String) : AsyncTaskLoader<List<Insurance>>(context) {
//        override fun loadInBackground(): List<Insurance> {
//            val carJson = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
//            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Insurance::class.java)
//            return ObjectMapper().readValue(carJson, type)
//        }
//    }
}
