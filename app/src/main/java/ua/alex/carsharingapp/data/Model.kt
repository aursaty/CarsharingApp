package ua.alex.carsharingapp.data

data class Model(
        var name: String,
        val brand: String,
        var cost: Double,
        var waitingCost: Double,
        var type: String
)