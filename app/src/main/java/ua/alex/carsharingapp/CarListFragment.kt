package ua.alex.carsharingapp


import android.app.Fragment
import android.app.LoaderManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import ua.alex.carsharingapp.MainActivity.Companion.JSON_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_METHOD_BUNDLE_KEY
import ua.alex.carsharingapp.MainActivity.Companion.REQUEST_URL_BUNDLE_KEY
import ua.alex.carsharingapp.data.Car

/**
 * A simple [Fragment] subclass.
 *
 */
class CarListFragment : Fragment() {
    companion object {
        const val CAR_NUMBER_BUNDLE_KEY = "CAR_NUMBER_BUNDLE_KEY"
        //        private val CAR_REQUEST_URL = "http://localhost:8080/api/cars/getAllCars"
//        private val CAR_REQUEST_URL = "http://192.168.1.138:8080/api/cars/getAllCars"
//        private val CAR_REQUEST_URL = "http://172.16.11.66:8080/api/cars/getAllCars"
        private const val CAR_LIST_REQUEST_URL = "/api/cars/getAllCars"
//        private val CAR_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=s&maxResults=10"
    }

    private var carNumber = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        (activity as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return inflater.inflate(R.layout.fragment_car_list, container, false)
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

        view!!.findViewById<FloatingActionButton>(R.id.add_car_fab).setOnClickListener {
            val carFragment = CarFragment()
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, carFragment, "CarFragment")
                    .addToBackStack("CarFragment")
                    .commit()
        }

        view.findViewById<ListView>(R.id.car_list_view).setOnItemClickListener { parent, itemView, position, id ->
            val carFragment = CarFragment()
            val carNumber = itemView.findViewById<TextView>(R.id.car_number).text as String
            val bundle = Bundle()
            bundle.putString(CAR_NUMBER_BUNDLE_KEY, carNumber)
            carFragment.arguments = bundle
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, carFragment, "CarFragment")
                    .addToBackStack("CarFragment")
                    .commit()
        }
    }

    // ???
    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString(REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundle.putString(REQUEST_URL_BUNDLE_KEY, CAR_LIST_REQUEST_URL)
        bundle.putString(JSON_BUNDLE_KEY, "")
        loaderManager.initLoader<List<Car>>(0, bundle, loaderCallbackCarList).forceLoad()
    }

    //
//
    private fun updateUi(cars: List<Car>) {
        val carListView = view.findViewById<ListView>(R.id.car_list_view)

        val carAdapter = CarAdapter(activity, cars)

        carListView.adapter = carAdapter
    }

    private val loaderCallbackCarList: LoaderManager.LoaderCallbacks<List<Car>> = object : LoaderManager.LoaderCallbacks<List<Car>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Car>> {
            return CarsLoader(activity, p1!!.getString(REQUEST_URL_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Car>>?, p1: List<Car>?) {
            updateUi(p1!!)
        }

        override fun onLoaderReset(p0: Loader<List<Car>>?) {
        }
    }

    private class CarsLoader(context: Context, val stringUrl: String) : AsyncTaskLoader<List<Car>>(context) {
        override fun loadInBackground(): List<Car> {
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Car::class.java)
            val carsJson = QueryUtils.fetchData(stringUrl, "GET", "")
            return ObjectMapper().readValue(carsJson, type)
        }

    }

    private class CarAdapter(context: Context, objects: List<Car>) :
            ArrayAdapter<Car>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var listItemView = convertView
            if (listItemView == null)
                listItemView = LayoutInflater.from(context).inflate(R.layout.car_list_item_view, parent, false)

            val car = getItem(position)

            listItemView!!.findViewById<TextView>(R.id.brand).text = car.model.brand
            listItemView.findViewById<TextView>(R.id.cost).text = car.model.cost.toString()
            listItemView.findViewById<TextView>(R.id.car_number).text = car.number
            listItemView.findViewById<TextView>(R.id.status).text = car.status.toString()

            return listItemView
        }
    }
}
