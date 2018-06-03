package ua.alex.carsharingapp.data

data class Model(
        var name: String = "",
        var brand: String = "",
        var cost: Double = 0.0,
        var waitingCost: Double = 0.0,
        var type: String = ""
)