package com.example.brandroid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Retrofit
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_cityId = "cityId"
private const val ARG_cityName = "cityName"

/**
 * A simple [Fragment] subclass.
 * Use the [RadarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RadarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var cityId = 0
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cityId = it.getInt(ARG_cityId)
            cityName = it.getString(ARG_cityName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_radar, container, false)
    }

    override fun onResume() {
        super.onResume()

        getView()?.findViewById<ImageButton>(R.id.backButton)?.setOnClickListener(
            object: View.OnClickListener
            {
                override fun onClick(v: View?) {
                    activity?.getSupportFragmentManager()?.popBackStackImmediate()
                }
            }
        )

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
            val response = service.getCityRadar(cityId)

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

                    val imageURLs = gson.fromJson(prettyJson, ImageURLs::class.java)

                    runBlocking {
                        getView()?.findViewById<ImageView>(R.id.radarImage)?.setImageBitmap(
                            fillImage(
                                imageURLs.androidImageURLs.xhdpiImageURL ?: ""
                            )
                        )
                        getView()?.findViewById<TextView>(R.id.cityName)?.text = cityName
                    }

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
                val err = e
                cityName = "Error loading radar image"
            }
        }

        return bm
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param cityId Parameter 1.
         * @param cityName Parameter 2.
         * @return A new instance of fragment RadarFragment.
         */
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param cityId Parameter 1.
         * @param cityName Parameter 2.
         * @return A new instance of fragment RadarFragment.
         */

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cityId: Int, cityName: String) =
            RadarFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_cityId, cityId)
                    putString(ARG_cityName, cityName)
                }
            }
    }
}