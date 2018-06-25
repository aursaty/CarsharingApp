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
import ua.alex.carsharingapp.data.Insurance


class InsuranceListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insurance_list, container, false)
    }

    companion object {
        const val INSURANCE_SERIES_BUNDLE_KEY = "INSURANCE_SERIES_BUNDLE_KEY"

        const val INSURANCE_LIST_REQUEST_URL = "/api/insurances/getAllInsurances"
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

        view!!.findViewById<FloatingActionButton>(R.id.add_insurance_fab).setOnClickListener {
            val insuranceFragment = InsuranceFragment()
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, insuranceFragment, "InsuranceFragment")
                    .addToBackStack("InsuranceFragment")
                    .commit()
        }

        view.findViewById<ListView>(R.id.insurance_list_view).setOnItemClickListener { parent, itemView, position, id ->
            val insuranceFragment = InsuranceFragment()
            val series = itemView.findViewById<TextView>(R.id.series).text as String
            val bundle = Bundle()
            bundle.putString(INSURANCE_SERIES_BUNDLE_KEY, series)
            insuranceFragment.arguments = bundle
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, insuranceFragment, "InsuranceFragment")
                    .addToBackStack("InsuranceFragment")
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, INSURANCE_LIST_REQUEST_URL)
        bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
        loaderManager.initLoader<List<Insurance>>(0, bundle, loaderCallback).forceLoad()
    }

    //
//
    private fun updateUi(list: List<Insurance>) {
        val listView = view.findViewById<ListView>(R.id.insurance_list_view)

        val adapter = InsuranceAdapter(activity, list)

        listView.adapter = adapter
    }

    private val loaderCallback: LoaderManager.LoaderCallbacks<List<Insurance>> = object : LoaderManager.LoaderCallbacks<List<Insurance>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Insurance>> {
            return InsurancesLoader(activity, p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Insurance>>?, p1: List<Insurance>?) {
            updateUi(p1!!)
        }

        override fun onLoaderReset(p0: Loader<List<Insurance>>?) {
        }
    }

    private class InsurancesLoader(context: Context, val stringUrl: String) : AsyncTaskLoader<List<Insurance>>(context) {
        override fun loadInBackground(): List<Insurance> {
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Insurance::class.java)
            val json = QueryUtils.fetchData(stringUrl, "GET", "")
            return ObjectMapper().readValue(json, type)
        }

    }

    private class InsuranceAdapter(context: Context, objects: List<Insurance>) :
            ArrayAdapter<Insurance>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var listItemView = convertView
            if (listItemView == null)
                listItemView = LayoutInflater.from(context).inflate(R.layout.insurance_list_item_view, parent, false)

            val insurance = getItem(position)

            listItemView!!.findViewById<TextView>(R.id.series).text = insurance.series
            listItemView.findViewById<TextView>(R.id.creating_date).text = insurance.creatingDate
            listItemView.findViewById<TextView>(R.id.company_name).text = insurance.companyName
            listItemView.findViewById<TextView>(R.id.ending_date).text = insurance.endingDate

            return listItemView
        }
    }


}
