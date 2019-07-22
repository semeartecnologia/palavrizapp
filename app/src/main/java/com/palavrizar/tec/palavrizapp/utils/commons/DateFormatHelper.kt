package com.palavrizar.tec.palavrizapp.utils.commons

import android.annotation.SuppressLint
import android.content.Context
import com.palavrizar.tec.palavrizapp.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatHelper {

    val currentTimeDate: String
        @SuppressLint("SimpleDateFormat")
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().time)

    fun stringDate(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }


    fun formatTimeComment(context: Context?, timeMillis: Long?): String{
        if (timeMillis == null) return ""
        val secs = timeMillis/1000
        return when{
            secs < 120 -> context?.getString(R.string.comment_time_now) ?: ""
            secs < 3600 -> context?.getString(R.string.comment_time_minutes, secs/60) ?: ""
            secs < 86400 -> context?.getString(R.string.comment_time_hours, secs/3600) ?: ""
            secs < 30*86400 -> formatDays(context, secs) //< 1 mes
            secs < 30*86400*12 -> formatMonths(context, secs) // < 1 ano
            else -> formatYears(context,secs)
        }
    }

    private fun formatYears(context: Context?, secs: Long): String {
        var timeFormat = ""
        val years = secs/30*86400*12
        val months = secs%30*86400*12

        timeFormat += context?.getString(R.string.comment_time_years, years.toInt())
        if (months > 30*86400) {
            timeFormat += ", "
            timeFormat +=  context?.getString(R.string.comment_time_months, months.toInt())
        }
        return timeFormat
    }

    private fun formatMonths(context: Context?, secs: Long): String{
        var timeFormat = ""
        val months = secs/30*86400
        val days = secs%30*86400

        timeFormat += context?.getString(R.string.comment_time_months, months.toInt())

        if (days > 86400){
            timeFormat += ", "
            timeFormat += context?.getString(R.string.comment_time_days, days.toInt())
        }

        return timeFormat
    }

    private fun formatDays(context: Context?, secs: Long): String{
        var timeFormat = ""
        val days = secs/86400
        val hours = secs%86400

        timeFormat += context?.getString(R.string.comment_time_days, days.toInt())

        if (hours > 3600){
            timeFormat += ", "
            timeFormat += context?.getString(R.string.comment_time_hours, hours.toInt())
        }
        return timeFormat
    }

}