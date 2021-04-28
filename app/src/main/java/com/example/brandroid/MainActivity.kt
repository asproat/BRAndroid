package com.example.brandroid

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2


class MainActivity : FragmentActivity() {
    private var cityPager : ViewPager2? = null
    private var cityWeatherList = ArrayList<CityWeatherFragment>()
    private var dotsCount = 0 //No of tabs or images
    private var dots = ArrayList<ImageView>(0)
    var linearLayout: LinearLayout? = null // public so it can be refreshed on adding a page
    private var selectedItem : Drawable? = null
    private var unselectedItem : Drawable? = null
    private var searchBar : androidx.appcompat.widget.SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityPager = findViewById<ViewPager2>(R.id.weatherPager)

        cityPager?.adapter = CityWeatherPagerAdapter(this)
        linearLayout = this.findViewById<View>(R.id.viewPagerCountDots) as LinearLayout
        selectedItem = resources.getDrawable(R.drawable.item_selected)
        unselectedItem = resources.getDrawable(R.drawable.item_unselected)

        searchBar = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchValue)
        searchBar?.setOnQueryTextListener(
                object : androidx.appcompat.widget.SearchView.OnQueryTextListener
                {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        openSearch(query)
                        searchBar?.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }

                }
        )

        findViewById<ImageButton>(R.id.deleteImage).setOnClickListener(
                object: View.OnClickListener
                {
                    override fun onClick(v: View?) {
                        if (cityWeatherList.size > 0) {
                            val removeCity = cityWeatherList[cityPager?.currentItem ?: 0]
                            cityWeatherList?.remove(removeCity)
                            cityPager?.adapter = CityWeatherPagerAdapter(this@MainActivity)

                            dotsCount = cityWeatherList?.size
                            drawPageSelectionIndicators(cityPager?.currentItem ?: 0)
                        }
                    }

                }
        )

        findViewById<ImageButton>(R.id.radarImage).setOnClickListener(
            object: View.OnClickListener
            {
                override fun onClick(v: View?) {
                    if (cityWeatherList.size > 0) {
                        val radarCity = cityWeatherList[cityPager?.currentItem ?: 0]

                        val radarFragment = RadarFragment.newInstance(radarCity.fragmentCityId,
                                radarCity.fragmentCityData?.city?.name ?: "")
                        supportFragmentManager.beginTransaction()
                            .add(R.id.searchLayout, radarFragment).addToBackStack("radar").commit()
                    }
                }
            }
        )

        cityPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                drawPageSelectionIndicators(position)
                super.onPageSelected(position)
            }
        })
    }


    override fun onResume() {
        super.onResume()

    }


    fun openSearch(searchValue: String?)
    {
        val searchFragment = SearchFragment.newInstance(searchValue ?: "")
        supportFragmentManager.beginTransaction().add(R.id.searchLayout, searchFragment).addToBackStack("search").commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000)
        {

            if (data != null) {
                val cityId = data!!.getIntExtra("cityId", 0)
                if (cityId > 0) {
                    newCityWeather(cityId)

                }
            }
        }
    }

    fun newCityWeather(cityId: Int)
    {
        var foundCity = false

        for (city in cityWeatherList)
        {
            if (city.fragmentCityId == cityId)
            {
                foundCity = true
                break
            }
        }

        if(!foundCity) {
            dotsCount++
            cityWeatherList.add(CityWeatherFragment.newInstance(cityId, dotsCount))
            cityPager?.adapter = CityWeatherPagerAdapter(this@MainActivity)
            cityPager?.setCurrentItem(cityWeatherList.size + 1)
            drawPageSelectionIndicators(dotsCount - 1)
            searchBar?.setQuery("", false)
            searchBar?.clearFocus()
        }
    }

    private inner class CityWeatherPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa)
    {

        override fun createFragment(position: Int): Fragment {
            return cityWeatherList[position]
        }

        override fun getItemCount(): Int {
            return cityWeatherList.size
        }
    }

    private fun drawPageSelectionIndicators(mPosition: Int) {
        if (linearLayout != null) {
            linearLayout!!.removeAllViews()

            dots = ArrayList<ImageView>()
            for (i in 0 until dotsCount) {
                dots.add(ImageView(this))
                if (i == mPosition)
                {
                    dots[i]?.setImageDrawable(selectedItem)
                } else
                {
                    dots[i]?.setImageDrawable(unselectedItem)
                }
                dots[i].setPadding(10)
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(4, 0, 4, 0)
                linearLayout!!.addView(dots[i], params)
            }
            linearLayout!!.requestLayout()
        }
    }


}