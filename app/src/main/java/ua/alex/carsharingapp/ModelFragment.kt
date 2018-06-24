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
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.ModelListFragment.Companion.MODEL_NUMBER_BUNDLE_KEY
import ua.alex.carsharingapp.data.Client
import ua.alex.carsharingapp.data.Model


/**
 * A simple [Fragment] subclass.
 *
 */
class ModelFragment : Fragment() {

    companion object {
        private const val LOADER_ID = 1
        private const val EDIT_LOADER_ID = 2
        private const val MODEL_REQUEST_URL = "/api/models/model_name="
        private const val PUT_CAR_REQUEST_URL = "/api/models"
    }

    private lateinit var model: Model

    private var modelName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

//        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        modelName = try {
            arguments.getString(MODEL_NUMBER_BUNDLE_KEY, "")
        } catch (e: NullPointerException) {
            null
        }

        return inflater.inflate(R.layout.fragment_model, container, false)
    }

    override fun onStart() {
        super.onStart()

        if (modelName != null) {
            val bundleClient = Bundle()
            bundleClient.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, MODEL_REQUEST_URL + modelName)
            bundleClient.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
            bundleClient.putString(MainActivity.JSON_BUNDLE_KEY, "")

            if (loaderManager.getLoader<Model>(LOADER_ID) == null)
                loaderManager.initLoader<Model>(LOADER_ID, bundleClient, loaderCallbackModel).forceLoad()
            else
                loaderManager.restartLoader<Model>(LOADER_ID, bundleClient, loaderCallbackModel).forceLoad()
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
                deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, MODEL_REQUEST_URL + modelName)
                if (loaderManager.getLoader<Model>(LOADER_ID) == null)
                    loaderManager.initLoader<Model>(LOADER_ID, deleteBundle, loaderCallbackModel).forceLoad()
                else
                    loaderManager.restartLoader<Model>(LOADER_ID, deleteBundle, loaderCallbackModel).forceLoad()
                true
            }
            R.id.save_car_item_menu -> {
//                val licence = view.findViewById<TextInputEditText>(R.id.licence_number_edit_text).text.toString()
//                val fullName = view.findViewById<TextInputEditText>(R.id.full_name_edit_text).text.toString()
//                val creditCardNumber = view.findViewById<TextInputEditText>(R.id.credit_card_edit_text).text.toString()
//                val address = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
//                val phoneNumber = view.findViewById<TextInputEditText>(R.id.phone_edit_text).text.toString()
//                val birthday = view.findViewById<TextView>(R.id.birthday_text_view).text.toString()
//                val registrationDate = view.findViewById<TextView>(R.id.date_text_view).text.toString()
//
//                val json = ObjectMapper().writeValueAsString(
//                        Client(licence, fullName, creditCardNumber, birthday, address, registrationDate, phoneNumber)
//                )
//
//                val bundle = Bundle()
//                bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "PUT")
//                bundle.putString(MainActivity.JSON_BUNDLE_KEY, json)
//                bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, PUT_CAR_REQUEST_URL)
//
//                if (loaderManager.getLoader<Client>(LOADER_ID) == null)
//                    loaderManager.initLoader<Client>(LOADER_ID, bundle, loaderCallbackModel).forceLoad()
//                else
//                    loaderManager.restartLoader<Client>(LOADER_ID, bundle, loaderCallbackModel).forceLoad()
//
//                if (modelName != null && modelName != licence) {
//                    val deleteBundle = Bundle()
//                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
//                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
//                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, MODEL_REQUEST_URL + modelName)
//
//                    if (loaderManager.getLoader<Client>(EDIT_LOADER_ID) == null)
//                        loaderManager.initLoader<Client>(EDIT_LOADER_ID, deleteBundle, loaderCallbackModel).forceLoad()
//                    else
//                        loaderManager.restartLoader<Client>(EDIT_LOADER_ID, deleteBundle, loaderCallbackModel).forceLoad()
//                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUi(model: Model) {
        view.findViewById<TextInputEditText>(R.id.model_name_edit_text).setText(model.name)
        view.findViewById<TextInputEditText>(R.id.brand_edit_text).setText(model.brand)
        view.findViewById<TextInputEditText>(R.id.cost_edit_text).setText(model.cost.toString())
        view.findViewById<TextInputEditText>(R.id.waiting_cost_edit_text).setText(model.waitingCost.toString())
        view.findViewById<TextInputEditText>(R.id.type_edit_text).setText(model.type)
    }

    private val loaderCallbackModel: LoaderManager.LoaderCallbacks<Model> = object : LoaderManager.LoaderCallbacks<Model> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Model> {
            return GetClientLoader(activity,
                    p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY),
                    p1.getString(MainActivity.REQUEST_METHOD_BUNDLE_KEY),
                    p1.getString(MainActivity.JSON_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<Model>?, p1: Model?) {
            if (p1 != null) {
                model = p1
                updateUi(p1)
            } else {
                MyHandle(activity).sendEmptyMessage(1)
            }
        }

        override fun onLoaderReset(p0: Loader<Model>?) {
        }
    }

    private class MyHandle(val context: Activity) : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1)
                context.fragmentManager.popBackStack()
        }
    }

    private class GetClientLoader(context: Context,
                                  val stringUrl: String,
                                  val requestMethod: String,
                                  val stringJson: String) : AsyncTaskLoader<Model>(context) {
        override fun loadInBackground(): Model? {
            val json = QueryUtils.fetchData(stringUrl, requestMethod, stringJson)
            return if (json == "")
                return null
            else
                ObjectMapper().readValue(json, Model::class.java)
        }
    }

}
