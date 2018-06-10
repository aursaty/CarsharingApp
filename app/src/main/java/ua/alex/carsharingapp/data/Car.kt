package ua.alex.carsharingapp.data

class Car(
        var number: String = "",
        var fuelCardNumber: String = "",
        var address: String = "",
        var color: String = "",
        var status: Boolean = false,
        var creatingDate: String = "",
        var model: Model = Model("", "", 0.0, 0.0, ""),
        var insurance: Insurance = Insurance("", "", "", "", "", "")
)