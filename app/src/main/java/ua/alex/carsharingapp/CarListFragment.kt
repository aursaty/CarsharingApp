package ua.alex.carsharingapp


import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import ua.alex.carsharingapp.data.Car
import ua.alex.carsharingapp.data.Insurance
import ua.alex.carsharingapp.data.Model

/**
 * A simple [Fragment] subclass.
 *
 */
class CarListFragment : Fragment() {


    companion object {
        private val CAR_REQUEST_URL = "http://localhost:8080/api/cars/getAllCars"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_car_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = Model("F150", "Ford", 100.0, 10.0, "jeep")
        val insurance = Insurance("A1",
                "10-10-2010",
                "10-10-2020",
                "Weed Street, 420",
                "1",
                "OMEGA")
        updateUi(listOf(Car("AA0000AA", "1", "Green Street, 1",
                "green", "true", "11-01-2001", model, insurance),
                Car("AA0001AA", "1", "Green Street, 1",
                        "green", "true", "11-01-2001", model, insurance),
                Car("AA0002AA", "1", "Green Street, 1",
                        "green", "true", "11-01-2001", model, insurance),
                Car("AA0003AA", "1", "Green Street, 1",
                        "green", "true", "11-01-2001", model, insurance),
                Car("AA0004AA", "1", "Green Street, 1",
                        "green", "true", "11-01-2001", model, insurance)))
    }

    private fun updateUi(cars: List<Car>) {
        val carListView = view.findViewById<ListView>(R.id.car_list_view)

        val carAdapter = CarAdapter(activity, cars)

        carListView.adapter = carAdapter
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
            listItemView.findViewById<TextView>(R.id.status).text = car.status

            return listItemView
        }
    }
}
