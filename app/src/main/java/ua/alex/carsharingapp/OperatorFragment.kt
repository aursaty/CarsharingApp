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
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.OperatorListFragment.Companion.OPERATOR_NUMBER_BUNDLE_KEY
import ua.alex.carsharingapp.data.Model
import ua.alex.carsharingapp.data.Operator

/**
 * A simple [Fragment] subclass.
 *
 */
class OperatorFragment : Fragment() {

    companion object {
        private const val LOADER_ID = 1
        private const val EDIT_LOADER_ID = 2
        private const val OPERATOR_REQUEST_URL = "/api/operators/operator_id="
        private const val PUT_OPERATOR_REQUEST_URL = "/api/operators"
    }

    private lateinit var operator: Operator

    private var operatorId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        operatorId = try {
            arguments.getString(OPERATOR_NUMBER_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

        return inflater.inflate(R.layout.fragment_operator, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (operatorId != null) {
            val bundle = Bundle()
            bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + operatorId)
            bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")

            if (loaderManager.getLoader<Operator>(LOADER_ID) == null)
                loaderManager.initLoader<Operator>(LOADER_ID, bundle, loaderCallbackOperator).forceLoad()
            else
                loaderManager.restartLoader<Operator>(LOADER_ID, bundle, loaderCallbackOperator).forceLoad()
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
                deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + operatorId)
                if (loaderManager.getLoader<Operator>(LOADER_ID) == null)
                    loaderManager.initLoader<Operator>(LOADER_ID, deleteBundle, loaderCallbackOperator).forceLoad()
                else
                    loaderManager.restartLoader<Operator>(LOADER_ID, deleteBundle, loaderCallbackOperator).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
                val id = view.findViewById<TextInputEditText>(R.id.id_edit_text).text.toString()
                val name = view.findViewById<TextInputEditText>(R.id.full_name_edit_text).text.toString()
                val address = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
                val phone = view.findViewById<TextInputEditText>(R.id.phone_number_edit_text).text.toString()

                val json = ObjectMapper().writeValueAsString(
                        Operator(id, name, address, phone)
                )

                val bundle = Bundle()
                bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "PUT")
                bundle.putString(MainActivity.JSON_BUNDLE_KEY, json)
                bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, PUT_OPERATOR_REQUEST_URL)

                if (loaderManager.getLoader<Operator>(LOADER_ID) == null)
                    loaderManager.initLoader<Operator>(LOADER_ID, bundle, loaderCallbackOperator).forceLoad()
                else
                    loaderManager.restartLoader<Operator>(LOADER_ID, bundle, loaderCallbackOperator).forceLoad()

                if (operatorId != null && operatorId != id) {
                    val deleteBundle = Bundle()
                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, OPERATOR_REQUEST_URL + operatorId)

                    if (loaderManager.getLoader<Operator>(EDIT_LOADER_ID) == null)
                        loaderManager.initLoader<Operator>(EDIT_LOADER_ID, deleteBundle, loaderCallbackOperator).forceLoad()
                    else
                        loaderManager.restartLoader<Operator>(EDIT_LOADER_ID, deleteBundle, loaderCallbackOperator).forceLoad()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(operator: Operator) {
        view.findViewById<TextInputEditText>(R.id.id_edit_text).setText(operator.id)
        view.findViewById<TextInputEditText>(R.id.full_name_edit_text).setText(operator.fullName)
        view.findViewById<TextInputEditText>(R.id.address_edit_text).setText(operator.address)
        view.findViewById<TextInputEditText>(R.id.phone_number_edit_text).setText(operator.phoneNumber)
    }

    private val loaderCallbackOperator: LoaderManager.LoaderCallbacks<Operator> = object : LoaderManager.LoaderCallbacks<Operator> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Operator> {
            return GetOperatorLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Operator>?, p1: Operator?) {
            if (p1 != null) {
                operator = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Operator>?) {
        }
    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetOperatorLoader(context: Context,
                                    val stringUrl: String,
                                    val requestMethod: String,
                                    val stringJson: String) : AsyncTaskLoader<Operator>(context) {
        override fun loadInBackground(): Operator? {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (json == "")
                return null
            else
                ObjectMapper().readValue(json, Operator::class.java)
        }
    }

}
