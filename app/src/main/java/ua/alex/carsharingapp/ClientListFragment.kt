package ua.alex.carsharingapp


import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.data.Client
import ua.alex.carsharingapp.data.Insurance

class ClientListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client_list, container, false)
    }

    companion object {
        const val CLIENT_NUMBER_BUNDLE_KEY = "CLIENT_NUMBER_BUNDLE_KEY"

        private const val INSURANCE_LIST_REQUEST_URL = "/api/clients/getAllClients"
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val model = Model("F150", "Ford", 100.0, 10.0, "jeep")
//        val insurance = Insurance("A1",
//                "10-10-2010",
//                "10-10-2020",
//                "Weed Street, 420",
//                "1",
//                "OMEGA")
//        updateUi(listOf(Car("AA0000AA", "1", "Green Street, 1",
//                "green", "true", "11-01-2001", model, insurance),
//                Car("AA0001AA", "1", "Green Street, 1",
//                        "green", "true", "11-01-2001", model, insurance),
//                Car("AA0002AA", "1", "Green Street, 1",
//                        "green", "true", "11-01-2001", model, insurance),
//                Car("AA0003AA", "1", "Green Street, 1",
//                        "green", "true", "11-01-2001", model, insurance),
//                Car("AA0004AA", "1", "Green Street, 1",
//                        "green", "true", "11-01-2001", model, insurance)))

        view!!.findViewById<FloatingActionButton>(R.id.add_client_fab).setOnClickListener {
            //TODO
//            val carFragment = CarFragment()
//            activity.fragmentManager.beginTransaction()
//                    .replace(R.id.content, carFragment, "CarFragment")
//                    .addToBackStack("CarFragment")
//                    .commit()
        }

        view.findViewById<ListView>(R.id.client_list_view).setOnItemClickListener { parent, itemView, position, id ->
            //TODO
//            val carFragment = CarFragment()
//            val carNumber = itemView.findViewById<TextView>(R.id.car_number).text as String
//            val bundle = Bundle()
//            bundle.putString(MODEL_NUMBER_BUNDLE_KEY, carNumber)
//            carFragment.arguments = bundle
//            activity.fragmentManager.beginTransaction()
//                    .replace(R.id.content, carFragment, "CarFragment")
//                    .addToBackStack("CarFragment")
//                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, INSURANCE_LIST_REQUEST_URL)
        bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
        loaderManager.initLoader<List<Client>>(0, bundle, loaderCallback).forceLoad()
    }

    //
//
    private fun updateUi(list: List<Client>) {
        val listView = view.findViewById<ListView>(R.id.client_list_view)

        val adapter = ClientAdapter(activity, list)

        listView.adapter = adapter
    }

    private val loaderCallback: LoaderManager.LoaderCallbacks<List<Client>> = object : LoaderManager.LoaderCallbacks<List<Client>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Client>> {
            return ClientsLoader(activity, p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Client>>?, p1: List<Client>?) {
            updateUi(p1!!)
        }

        override fun onLoaderReset(p0: Loader<List<Client>>?) {
        }
    }

    private class ClientsLoader(context: Context, val stringUrl: String) : AsyncTaskLoader<List<Client>>(context) {
        override fun loadInBackground(): List<Client> {
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Client::class.java)
            val json = QueryUtils.fetchData(stringUrl, "GET", "")
            return ObjectMapper().readValue(json, type)
        }

    }

    private class ClientAdapter(context: Context, objects: List<Client>) :
            ArrayAdapter<Client>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var listItemView = convertView
            if (listItemView == null)
                listItemView = LayoutInflater.from(context).inflate(R.layout.client_list_item_view, parent, false)

            val client = getItem(position)

            listItemView!!.findViewById<TextView>(R.id.licence).text = client.licenseNumber
            listItemView.findViewById<TextView>(R.id.number).text = client.phoneNumber
            listItemView.findViewById<TextView>(R.id.name).text = client.fullName
            listItemView.findViewById<TextView>(R.id.address).text = client.address

            return listItemView
        }


    }


}
