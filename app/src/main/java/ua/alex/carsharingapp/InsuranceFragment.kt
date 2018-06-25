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
import android.widget.EditText
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.InsuranceListFragment.Companion.INSURANCE_SERIES_BUNDLE_KEY
import ua.alex.carsharingapp.data.Insurance

/**
 * A simple [Fragment] subclass.
 *
 */
class InsuranceFragment : Fragment() {

    companion object {
        private const val LOADER_ID = 1
        private const val EDIT_LOADER_ID = 2
        private const val OPERATOR_REQUEST_URL = "/api/insurances/insurance_series="
        private const val PUT_OPERATOR_REQUEST_URL = "/api/insurances"
    }

    private lateinit var insurance: Insurance

    private var insuranceSeries: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        insuranceSeries = try {
            arguments.getString(INSURANCE_SERIES_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

        return inflater.inflate(R.layout.fragment_insurance, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (insuranceSeries != null) {
            val bundle = Bundle()
            bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + insuranceSeries)
            bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")

            if (loaderManager.getLoader<Insurance>(LOADER_ID) == null)
                loaderManager.initLoader<Insurance>(LOADER_ID, bundle, loaderCallbackInsurance).forceLoad()
            else
                loaderManager.restartLoader<Insurance>(LOADER_ID, bundle, loaderCallbackInsurance).forceLoad()
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
                deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + insuranceSeries)
                if (loaderManager.getLoader<Insurance>(LOADER_ID) == null)
                    loaderManager.initLoader<Insurance>(LOADER_ID, deleteBundle, loaderCallbackInsurance).forceLoad()
                else
                    loaderManager.restartLoader<Insurance>(LOADER_ID, deleteBundle, loaderCallbackInsurance).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
//                val id = view.findViewById<TextInputEditText>(R.id.id_edit_text).text.toString()
//                val name = view.findViewById<TextInputEditText>(R.id.full_name_edit_text).text.toString()
//                val address = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
//                val phone = view.findViewById<TextInputEditText>(R.id.phone_number_edit_text).text.toString()
//
//                val json = ObjectMapper().writeValueAsString(
//                        Operator(id, name, address, phone)
//                )
//
//                val bundle = Bundle()
//                bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "PUT")
//                bundle.putString(MainActivity.JSON_BUNDLE_KEY, json)
//                bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, PUT_OPERATOR_REQUEST_URL)
//
//                if (loaderManager.getLoader<Insurance>(LOADER_ID) == null)
//                    loaderManager.initLoader<Insurance>(LOADER_ID, bundle, loaderCallbackInsurance).forceLoad()
//                else
//                    loaderManager.restartLoader<Insurance>(LOADER_ID, bundle, loaderCallbackInsurance).forceLoad()
//
//                if (insuranceSeries != null && insuranceSeries != id) {
//                    val deleteBundle = Bundle()
//                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
//                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
//                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + insuranceSeries)
//
//                    if (loaderManager.getLoader<Insurance>(EDIT_LOADER_ID) == null)
//                        loaderManager.initLoader<Insurance>(EDIT_LOADER_ID, deleteBundle, loaderCallbackInsurance).forceLoad()
//                    else
//                        loaderManager.restartLoader<Insurance>(EDIT_LOADER_ID, deleteBundle, loaderCallbackInsurance).forceLoad()
//                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(insurance: Insurance) {
        view.findViewById<TextInputEditText>(R.id.series_edit_text).setText(insurance.series)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(insurance.address)
        view.findViewById<TextInputEditText>(R.id.identification_number_edit_text).setText(insurance.identificationNumber)
        view.findViewById<TextInputEditText>(R.id.company_name_edit_text).setText(insurance.companyName)
        view.findViewById<TextView>(R.id.creating_date_text_view).text = insurance.creatingDate
        view.findViewById<TextView>(R.id.ending_date_text_view).text = insurance.endingDate
    }

    private val loaderCallbackInsurance: LoaderManager.LoaderCallbacks<Insurance> = object : LoaderManager.LoaderCallbacks<Insurance> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Insurance> {
            return GetInsuranceLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Insurance>?, p1: Insurance?) {
            if (p1 != null) {
                insurance = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Insurance>?) {
        }
    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetInsuranceLoader(context: Context,
                                     val stringUrl: String,
                                     val requestMethod: String,
                                     val stringJson: String) : AsyncTaskLoader<Insurance>(context) {
        override fun loadInBackground(): Insurance? {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (json == "")
                return null
            else
                ObjectMapper().readValue(json, Insurance::class.java)
        }
    }
}
