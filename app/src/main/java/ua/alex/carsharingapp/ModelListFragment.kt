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
import ua.alex.carsharingapp.data.Model

class ModelListFragment : Fragment() {

    companion object {
        const val MODEL_NUMBER_BUNDLE_KEY = "MODEL_NUMBER_BUNDLE_KEY"

        private const val MODEL_LIST_REQUEST_URL = "/api/models/getAllModels"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_model_list, container, false)
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

        view!!.findViewById<FloatingActionButton>(R.id.add_model_fab).setOnClickListener {
            val modelFragment = ModelFragment()
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, modelFragment, "ModelFragment")
                    .addToBackStack("ModelFragment")
                    .commit()
        }

        view.findViewById<ListView>(R.id.model_list_view).setOnItemClickListener { parent, itemView, position, id ->
            val modelFragment = ModelFragment()
            val model = itemView.findViewById<TextView>(R.id.model).text as String
            val bundle = Bundle()
            bundle.putString(MODEL_NUMBER_BUNDLE_KEY, model)
            modelFragment.arguments = bundle
            activity.fragmentManager.beginTransaction()
                    .replace(R.id.content, modelFragment, "ModelFragment")
                    .addToBackStack("ModelFragment")
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        val bundle = Bundle()
        bundle.putString(MainActivity.REQUEST_METHOD_BUNDLE_KEY, "GET")
        bundle.putString(MainActivity.REQUEST_URL_BUNDLE_KEY, MODEL_LIST_REQUEST_URL)
        bundle.putString(MainActivity.JSON_BUNDLE_KEY, "")
        loaderManager.initLoader<List<Model>>(0, bundle, loaderCallbackModelList).forceLoad()
    }

    //
//
    private fun updateUi(list: List<Model>) {
        val listView = view.findViewById<ListView>(R.id.model_list_view)

        val adapter = ModelAdapter(activity, list)

        listView.adapter = adapter
    }

    private val loaderCallbackModelList: LoaderManager.LoaderCallbacks<List<Model>> = object : LoaderManager.LoaderCallbacks<List<Model>> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Model>> {
            return ModelsLoader(activity, p1!!.getString(MainActivity.REQUEST_URL_BUNDLE_KEY))
        }

        override fun onLoadFinished(p0: Loader<List<Model>>?, p1: List<Model>?) {
            updateUi(p1!!)
        }

        override fun onLoaderReset(p0: Loader<List<Model>>?) {
        }
    }

    private class ModelsLoader(context: Context, val stringUrl: String) : AsyncTaskLoader<List<Model>>(context) {
        override fun loadInBackground(): List<Model> {
            val type: JavaType = ObjectMapper().typeFactory.constructParametricType(List::class.java, Model::class.java)
            val json = QueryUtils.fetchData(stringUrl, "GET", "")
            return ObjectMapper().readValue(json, type)
        }

    }

    private class ModelAdapter(context: Context, objects: List<Model>) :
            ArrayAdapter<Model>(context, 0, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var listItemView = convertView
            if (listItemView == null)
                listItemView = LayoutInflater.from(context).inflate(R.layout.model_list_item_view, parent, false)

            val model = getItem(position)

            listItemView!!.findViewById<TextView>(R.id.brand).text = model.brand
            listItemView.findViewById<TextView>(R.id.cost).text = model.cost.toString()
            listItemView.findViewById<TextView>(R.id.model).text = model.name
            listItemView.findViewById<TextView>(R.id.waiting_cost).text = model.waitingCost.toString()

            return listItemView
        }
    }

}
