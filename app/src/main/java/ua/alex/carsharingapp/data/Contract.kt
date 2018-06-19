package ua.alex.carsharingapp.data

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.util.*

data class Contract (

        val id: String = "",

//        val startDateTime: LocalDateTime = LocalDateTime.ofInstant((Calendar.getInstance() as Calendar).toInstant(), Calendar.getInstance().timeZone.toZoneId()),

        @JsonIgnore
        val startDateTime: Any? = null,

        @JsonIgnore
        val endDateTime: Any? = null,

        @JsonIgnore
        val realDateTime: Any? = null,

        val returnAddress: String? = "",

        val type: String = "",

        val car: Car = Car(),

        val client: Client = Client(),

        val operator: Operator = Operator()

)