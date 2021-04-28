package com.example.brandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class HourlyAdapter(context: Context, resource: Int, private val hourlyWeathers: MutableList<HourlyWeather>) :
        ArrayAdapter<HourlyWeather>(context, resource, hourlyWeathers) {


    override fun getCount(): Int {
        return hourlyWeathers.size
    }

    override fun getItem(position: Int): HourlyWeather? {
        return hourlyWeathers[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var myView = convertView
        if (myView == null) {
            myView = LayoutInflater.from(getContext()).inflate(R.layout.hourly_weather, parent, false);
        }

        var thisHour = hourlyWeathers[position]

        val imageView = myView!!.findViewById<ImageView>(R.id.hourlyImage)
        when(thisHour.weatherType)
        {
            "sunny" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_sunny_active)
            "cloudy" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_cloudy_active)
            "heavyRain" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_heavy_rain_active)
            "lightRain" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_light_rain_active)
            "snowSleet" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_snow_sleet_active)
            "partlyCloudy" -> imageView?.setImageResource(R.drawable.ic_icon_weather_active_ic_partly_cloudy_active)
        }
        imageView?.drawable?.setTint(context!!.getColor(R.color.white))


        var currentHour = position
        if(currentHour > 11)
        {
            currentHour-=12
        }
        if(currentHour == 0)
        {
            currentHour = 12
        }

        myView!!.findViewById<TextView>(R.id.hourlyTime).text = String.format("%d%s",
                currentHour,
                if (position > 11) "PM" else "AM")

        myView!!.findViewById<TextView>(R.id.hourlyTemp).text = String.format("%d°", thisHour.temperature)

        myView!!.findViewById<TextView>(R.id.hourlyRain).text = String.format("%.0f%%", thisHour.rainChance)

        myView!!.findViewById<TextView>(R.id.hourlyWind).text = String.format("%.1f", thisHour.windSpeed)

        myView!!.findViewById<TextView>(R.id.hourlyHumidity).text = String.format("%.0f%%", thisHour.humidity)

        return myView!!
    }
}