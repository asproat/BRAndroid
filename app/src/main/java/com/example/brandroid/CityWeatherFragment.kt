package com.example.brandroid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Retrofit
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [CityWeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CityWeatherFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var fragmentCityId = 0
    var fragmentCityData : CityWeather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        arguments?.let {
            val cityId = it.getInt("cityId")
            fragmentCityId = cityId
            getCityWeather(cityId)
        }

        return inflater.inflate(R.layout.fragment_city_weather, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CityWeatherFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cityId: Int, numCities: Int) =
            CityWeatherFragment().apply {
                arguments = Bundle().apply {
                    putInt("cityId", cityId)
                    putInt("numCities", numCities)
                }
            }
    }

    fun getCityWeather(cityId: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://weather.exam.bottlerocketservices.com")
            .build()

        // Create Service
        val service = retrofit.create(WeatherAPIInterface::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            /*
             * For @Query: You need to replace the following line with val response = service.getEmployees(2)
             * For @Path: You need to replace the following line with val response = service.getEmployee(53)
             */

            // Do the GET request and get response
            val response = service.getCityWeather(cityId)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                    response.body()
                                            ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                    )

                    fragmentCityData = gson.fromJson(prettyJson, CityWeather::class.java)

                    if (fragmentCityData != null) {
                        val thisCity = fragmentCityData!!

                        runBlocking {
                            getView()?.findViewById<ImageView>(R.id.cityImage)?.setImageBitmap(
                                fillImage(
                                    thisCity.city.imageURLs.androidImageURLs.xhdpiImageURL ?: ""
                                )
                            )
                        }

                        getView()?.findViewById<TextView>(R.id.cityName)?.text =
                            String.format("%s, %s", thisCity.city.name, thisCity.city.admin1code)

                        val dateString =
                            SimpleDateFormat("EEE MM/dd/yy   hh:mm a").format(Date()).toString()
                                .toUpperCase()
                        getView()?.findViewById<TextView>(R.id.dateString)?.text = dateString

                        getView()?.findViewById<TextView>(R.id.temperature)?.text = String.format(
                            "%d°",
                            thisCity.weather.days[0].hourlyWeather[0].temperature
                        )

                        val cal = Calendar.getInstance()
                        cal.time = Date()
                        cal.add(
                            Calendar.DAY_OF_MONTH,
                            -1
                        ) // so when we add one the first time, it's the current date
                        for (day in 0..6) {
                            val thisDate = cal.add(Calendar.DAY_OF_MONTH, 1)
                            val dow = getView()?.findViewById<TextView>(
                                resources.getIdentifier(
                                    String.format(
                                        "day%ddow",
                                        day
                                    ), "id", context?.packageName
                                )
                            )
                            dow?.text = SimpleDateFormat("EEE").format(cal.time)
                            val weatherDayImage = getView()?.findViewById<ImageView>(
                                resources.getIdentifier(
                                    String.format(
                                        "day%dimage",
                                        day
                                    ), "id", context?.packageName
                                )
                            )
                            when (thisCity.weather.days[day].weatherType) {
                                "sunny" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_sunny_active)
                                "cloudy" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_cloudy_active)
                                "heavyRain" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_heavy_rain_active)
                                "lightRain" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_light_rain_active)
                                "snowSleet" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_snow_sleet_active)
                                "partlyCloudy" -> weatherDayImage?.setImageResource(R.drawable.ic_icon_weather_active_ic_partly_cloudy_active)
                            }

                            val temp = getView()?.findViewById<TextView>(
                                resources.getIdentifier(
                                    String.format(
                                        "day%dtemp",
                                        day
                                    ), "id", context?.packageName
                                )
                            )
                            temp?.text = String.format("%d°", thisCity.weather.days[day].high)

                            if (day > 0) {
                                dow?.setTextColor(context!!.getColor(R.color.darkWeather))
                                weatherDayImage?.drawable?.setTint(context!!.getColor(R.color.darkWeather))
                                temp?.setTextColor(context!!.getColor(R.color.darkWeather))
                            }
                        }

                        val hourlyListView = getView()?.findViewById<ListView>(R.id.hourlyWeather)

                        if (hourlyListView != null) {
                            hourlyListView?.adapter = HourlyAdapter(
                                context!!, R.layout.hourly_weather,
                                thisCity.weather.days[0].hourlyWeather.toMutableList()
                            )
                        }
                        getView()?.findViewById<ProgressBar>(R.id.waitIndicator)?.visibility =
                            View.GONE
                    }
                }
                else {
                    val error = ""

                }
            }
        }
    }

    suspend fun fillImage(
            imageURL: String
    ) : Bitmap {

        var bm = Bitmap.createBitmap(10,10, Bitmap.Config.ALPHA_8)
        // Move the execution of the coroutine to the I/O dispatcher
        withContext(Dispatchers.IO) {
            // Blocking network request code

            try {
                val aURL = URL(imageURL)
                val conn: URLConnection = aURL.openConnection()
                conn.connect()
                val stream: InputStream = conn.getInputStream()
                val bis = BufferedInputStream(stream)
                bm = BitmapFactory.decodeStream(bis)
                bis.close()
                stream.close()

            } catch (e: IOException) {
            }
        }

        return bm
    }

}