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
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.data.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ContractFragment : Fragment() {

    companion object {
        private const val CONTRACT_EDIT_LOADER_ID = -1
        private const val CONTRACT_LOADER_ID = 1
        private const val CAR_LIST_LOADER_ID = 2
        private const val CLIENT_LIST_LOADER_ID = 3
        private const val OPERATOR_LIST_LOADER_ID = 4
        private const val CONTRACT_REQUEST_URL = "/api/contracts/contract_id="
        private const val CAR_LIST_REQUEST_URL = "/api/cars/getAllCars"
        private const val CLIENT_LIST_REQUEST_URL = "/api/clients/getAllClients"
        private const val OPERATOR_LIST_REQUEST_URL = "/api/operators/getAllOperators"
        private const val PUT_CONTRACT_REQUEST_URL = "/api/contracts"
    }

    lateinit var carList: List<Car>
    lateinit var clientList: List<Client>
    lateinit var operatorList: List<Operator>
    lateinit var contract: Contract

    private var contractId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        contractId = try {
            arguments.getString(ContractListFragment.CONTRACT_NUMBER_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

        return inflater.inflate(R.layout.fragment_contract, container, false)
    }

    override fun onStart() {
        super.onStart()

        val bundleCarList = Bundle()
        bundleCarList.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CAR_LIST_REQUEST_URL)
        bundleCarList.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundleCarList.putString(MainActivity.JSON_BUNDLE_KEY, "")

        val bundleClientList = Bundle()
        bundleClientList.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CLIENT_LIST_REQUEST_URL)
        bundleClientList.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundleClientList.putString(MainActivity.JSON_BUNDLE_KEY, "")

        val bundleOperatorList = Bundle()
        bundleOperatorList.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_LIST_REQUEST_URL)
        bundleOperatorList.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundleOperatorList.putString(MainActivity.JSON_BUNDLE_KEY, "")

        loaderManager.initLoader<List<Car>>(CAR_LIST_LOADER_ID, bundleCarList, loaderCallbackCarList).forceLoad()
        loaderManager.initLoader<List<Client>>(CLIENT_LIST_LOADER_ID, bundleClientList, loaderCallbackClientList).forceLoad()
        loaderManager.initLoader<List<Operator>>(OPERATOR_LIST_LOADER_ID, bundleOperatorList, loaderCallbackOperatorList).forceLoad()

        if (contractId != null) {
            val bundle = Bundle()
            bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CONTRACT_REQUEST_URL + contractId)
            bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")

            loaderManager.initLoader<Contract>(CONTRACT_LOADER_ID, bundle, loaderCallbackContract).forceLoad()
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
//                val deleteBundle = Bundle()
//                deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
//                deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
//                deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + contractId)
//                if (loaderManager.getLoader<Car>(CAR_LOADER_ID) == null)
//                    loaderManager.initLoader<Car>(CAR_LOADER_ID, deleteBundle, loaderCallbackContract).forceLoad()
//                else
//                    loaderManager.restartLoader<Car>(CAR_LOADER_ID, deleteBundle, loaderCallbackContract).forceLoad()
//                true
//            }
//            R.id.save_car_item_menu -> {
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
//                if (loaderManager.getLoader<Car>(CAR_LOADER_ID) == null)
//                    loaderManager.initLoader<Car>(CAR_LOADER_ID, bundle, loaderCallbackContract).forceLoad()
//                else
//                    loaderManager.restartLoader<Car>(CAR_LOADER_ID, bundle, loaderCallbackContract).forceLoad()
//
//                if (contractId != null && contractId != number) {
//                    val deleteBundle = Bundle()
//                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
//                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
//                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CAR_REQUEST_URL + contractId)
//                    if (loaderManager.getLoader<Car>(CAR_EDIT_LOADER_ID) == null)
//                        loaderManager.initLoader<Car>(CAR_EDIT_LOADER_ID, deleteBundle, loaderCallbackContract).forceLoad()
//                    else
//                        loaderManager.restartLoader<Car>(CAR_EDIT_LOADER_ID, deleteBundle, loaderCallbackContract).forceLoad()
//                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(contract: Contract) {
        view.findViewById<TextInputEditText>(R.id.id_edit_text).setText(contract.id)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(contract.returnAddress)
        view.findViewById<TextInputEditText>(R.id.type_edit_text).setText(contract.type)
        view.findViewById<TextView>(R.id.start_date_time_text_view).text = contract.startDateTime
        view.findViewById<TextView>(R.id.end_date_time_text_view).text = contract.endDateTime
        view.findViewById<TextView>(R.id.real_date_time_text_view).text = contract.realDateTime
        for (i in 0 until view.findViewById<Spinner>(R.id.car_spinner).count) {
            if (view.findViewById<Spinner>(R.id.car_spinner).getItemAtPosition(i) == contract.car.number)
                view.findViewById<Spinner>(R.id.car_spinner).setSelection(i)
        }
        for (j in 0 until view.findViewById<Spinner>(R.id.client_spinner).count) {
            if (view.findViewById<Spinner>(R.id.client_spinner).getItemAtPosition(j) == contract.client.licenseNumber)
                view.findViewById<Spinner>(R.id.client_spinner).setSelection(j)
        }
        for (j in 0 until view.findViewById<Spinner>(R.id.operator_spinner).count) {
            if (view.findViewById<Spinner>(R.id.operator_spinner).getItemAtPosition(j) == contract.operator.id)
                view.findViewById<Spinner>(R.id.operator_spinner).setSelection(j)
        }
    }

    private fun updateCarSpinnerUi(carList: List<Car>) {
        view.findViewById<Spinner>(R.id.car_spinner).adapter =
                ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line,
                        carList.map { it.number })
    }

    private fun updateClientSpinnerUi(clientList: List<Client>) {
        view.findViewById<Spinner>(R.id.car_spinner).adapter =
                ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line,
                        clientList.map { it.licenseNumber })
    }

    private fun updateOperatorSpinnerUi(operatorList: List<Operator>) {
        view.findViewById<Spinner>(R.id.operator_spinner).adapter =
                ArrayAdapter<String>(activity,
                        android.R.layout.simple_dropdown_item_1line,
                        operatorList.map { it.id })
    }

    private val loaderCallbackContract: LoaderManager.LoaderCallbacks<Contract> = object : LoaderManager.LoaderCallbacks<Contract> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Contract> {
            return GetContractLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Contract>?, p1: Contract?) {
            if (p1 != null) {
                contract = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Contract>?) {
        }
    }

    private val loaderCallbackCarList: LoaderManager.LoaderCallbacks<List<Car>> = object : LoaderManager.LoaderCallbacks<List<Car>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Car>> {
            return GetCarListLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Car>>?, p1: List<Car>?) {
            if (p1 != null) {
                carList = p1
                updateCarSpinnerUi(p1)
            }
        }

        override fun onLoaderReset(p0: Loader<List<Car>>?) {
        }
    }

    private val loaderCallbackClientList: LoaderManager.LoaderCallbacks<List<Client>> = object : LoaderManager.LoaderCallbacks<List<Client>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Client>> {
            return GetClientListLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Client>>?, p1: List<Client>?) {
            if (p1 != null) {
                clientList = p1
                updateClientSpinnerUi(p1)
            }
        }

        override fun onLoaderReset(p0: Loader<List<Client>>?) {
        }
    }

    private val loaderCallbackOperatorList: LoaderManager.LoaderCallbacks<List<Operator>> = object : LoaderManager.LoaderCallbacks<List<Operator>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Operator>> {
            return GetOperatorListLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Operator>>?, p1: List<Operator>?) {
            if (p1 != null) {
                operatorList = p1
                updateOperatorSpinnerUi(p1)
            }
        }

        override fun onLoaderReset(p0: Loader<List<Operator>>?) {
        }
    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetContractLoader(context: Context,
                                    val stringUrl: String,
                                    val requestMethod: String,
                                    val stringJson: String) : AsyncTaskLoader<Contract>(context) {
        override fun loadInBackground(): Contract? {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (json == "" || json == null)
                return null
            else
                ObjectMapper().readValue(json, Contract::class.java)
        }
    }

    private class GetCarListLoader(context: Context,
                                   val stringUrl: String,
                                   val requestMethod: String,
                                   val stringJson: String) : AsyncTaskLoader<List<Car>>(context) {
        override fun loadInBackground(): List<Car> {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Car::class.java)
            return ObjectMapper().readValue(json, type)
        }
    }

    private class GetClientListLoader(context: Context,
                                      val stringUrl: String,
                                      val requestMethod: String,
                                      val stringJson: String) : AsyncTaskLoader<List<Client>>(context) {
        override fun loadInBackground(): List<Client> {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Client::class.java)
            return ObjectMapper().readValue(json, type)
        }
    }

    private class GetOperatorListLoader(context: Context,
                                      val stringUrl: String,
                                      val requestMethod: String,
                                      val stringJson: String) : AsyncTaskLoader<List<Operator>>(context) {
        override fun loadInBackground(): List<Operator> {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Operator::class.java)
            return ObjectMapper().readValue(json, type)
        }
    }
}
