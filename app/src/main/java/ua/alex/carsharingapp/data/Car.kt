package ua.alex.carsharingapp.data

data class Car(
        var number: String,
        var fuelCardNumber: String,
        var address: String,
        var color: String,
        var status: String,
        var creatingDate: String,
        var model: Model,
        var insurance: Insurance
)