package com.example.brandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

private const val ARG_PARAM1 = "searchValue"

private var searchResults = listOf<City>()
private var cityList = ArrayList<String>()
private var cityListView : ListView? = null
private var searchEdit : EditText? = null
private var statusText : TextView? = null


class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var searchValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchValue = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onResume() {
        super.onResume()

        getView()?.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener(
            object: View.OnClickListener
            {
                override fun onClick(v: View?) {
                    activity?.getSupportFragmentManager()?.popBackStackImmediate()
                }
            }
        )

        statusText = getView()?.findViewById<TextView>(R.id.statusText)

        searchEdit = getView()?.findViewById<EditText>(R.id.searchValue)
        searchEdit?.setText(searchValue)
        searchEdit?.addTextChangedListener(
                object: TextWatcher
                {
                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        searchCities(s.toString())
                    }

                }
        )

        cityListView = getView()?.findViewById<ListView>(R.id.searchList)
        cityListView?.adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1,
                cityList)
        cityListView?.onItemClickListener =
                object: AdapterView.OnItemClickListener
                {
                    override fun onItemClick(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        selectFinish(position)
                    }
                }

        searchCities(searchValue ?: "")

    }

    companion object {
        @JvmStatic
        fun newInstance(searchValue: String) =
                SearchFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, searchValue)
                    }
                }
    }

    fun selectFinish(position: Int) {

        (activity as MainActivity).newCityWeather(searchResults[position].geonameid)
        activity?.getSupportFragmentManager()?.popBackStackImmediate()

    }

    fun searchCities(searchValue: String) {

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
            val response = service.searchCities(searchValue)

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

                    val searchResult = gson.fromJson(prettyJson, WeatherSearchResult::class.java)

                    searchResults = searchResult.cities

                    if(searchResult.totalCitiesFound > 0) {
                        cityList.clear()
                        for (city in searchResult.cities) {
                            cityList.add(String.format("%s, %s", city.name, city.admin1code))
                        }

                        (cityListView?.adapter as ArrayAdapter<String>).notifyDataSetChanged()

                        statusText?.visibility = View.GONE
                    }
                    else
                    {
                        statusText?.text = "No results found"
                    }

                } else {
                    statusText?.text = "Error finding server"

                }
            }
        }
    }

}