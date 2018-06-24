package ua.alex.carsharingapp.data

import android.os.Build
import android.support.annotation.RequiresApi
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
data class Contract
(

        val id: String = "",

//        val startDateTime: LocalDateTime = LocalDateTime.ofInstant((Calendar.getInstance() as Calendar).toInstant(), Calendar.getInstance().timeZone.toZoneId()),

        val startDateTime: String = "",

        val endDateTime: String = "",

        val realDateTime: String = "",

        val returnAddress: String? = "",

        val type: String = "",

        val car: Car = Car(),

        val client: Client = Client(),

        val operator: Operator = Operator()

)