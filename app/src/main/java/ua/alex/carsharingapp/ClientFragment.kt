package ua.alex.carsharingapp


import android.app.Activity
import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.TextInputEditText
import android.view.*
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.data.Client


/**
 * A simple [Fragment] subclass.
 *
 */
class ClientFragment : Fragment() {

    companion object {
        private const val LOADER_ID = 1
        private const val EDIT_LOADER_ID = 2
        private const val CLIENT_REQUEST_URL = "/api/clients/licenceNumber="
        private const val PUT_CAR_REQUEST_URL = "/api/clients"
    }

    lateinit var client: Client

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

    override fun onStart() {
        super.onStart()

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
                val licence = view.findViewById<TextInputEditText>(R.id.licence_number_edit_text).text.toString()
                val fullName = view.findViewById<TextInputEditText>(R.id.full_name_edit_text).text.toString()
                val creditCardNumber = view.findViewById<TextInputEditText>(R.id.credit_card_edit_text).text.toString()
                val address = view.findViewById<TextInputEditText>(R.id.address_edit_text).text.toString()
                val phoneNumber = view.findViewById<TextInputEditText>(R.id.phone_edit_text).text.toString()
                val birthday = view.findViewById<TextView>(R.id.birthday_text_view).text.toString()
                val registrationDate = view.findViewById<TextView>(R.id.date_text_view).text.toString()

                val json = ObjectMapper().writeValueAsString(
//                        try {
                        Client(licence, fullName, creditCardNumber, birthday, address, registrationDate, phoneNumber)
//                        } catch (e: UninitializedPropertyAccessException) {
//                            Client(licence, fullName, creditCardNumber, address, phoneNumber, birthday, registrationDate)
//                        }
                )

                val bundle = Bundle()
                bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "PUT")
                bundle.putString(MainActivity.JSON_BUNDLE_KEY, json)
                bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, PUT_CAR_REQUEST_URL)

                if (loaderManager.getLoader<Client>(LOADER_ID) == null)
                    loaderManager.initLoader<Client>(LOADER_ID, bundle, loaderCallbackClient).forceLoad()
                else
                    loaderManager.restartLoader<Client>(LOADER_ID, bundle, loaderCallbackClient).forceLoad()

                if (licenceNumber != null && licenceNumber != licence) {
                    val deleteBundle = Bundle()
                    deleteBundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "DELETE")
                    deleteBundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
                    deleteBundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, CLIENT_REQUEST_URL + licenceNumber)

                    if (loaderManager.getLoader<Client>(EDIT_LOADER_ID) == null)
                        loaderManager.initLoader<Client>(EDIT_LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
                    else
                        loaderManager.restartLoader<Client>(EDIT_LOADER_ID, deleteBundle, loaderCallbackClient).forceLoad()
                }
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
    }

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
}
