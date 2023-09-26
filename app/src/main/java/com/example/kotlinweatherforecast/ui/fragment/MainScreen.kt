package com.example.kotlinweatherforecast.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlinweatherforecast.ui.adapter.ViewPagerAdapter
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.example.kotlinweatherforecast.anim.ZoomOutPageTransformer
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.*
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinweatherforecast.R
import com.example.kotlinweatherforecast.databinding.FragmentMainScreenBinding
import com.example.kotlinweatherforecast.common.OnHymnClickListener
import com.example.kotlinweatherforecast.ui.adapter.CitiesRecyclerViewAdapter
import com.example.kotlinweatherforecast.ui.adapter.TempeturesRecyclerViewAdapter
import com.example.kotlinweatherforecast.common.onCliclLongRecyclerView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.InputStream
import java.util.Locale
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MainScreen : Fragment() , OnHymnClickListener, onCliclLongRecyclerView {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private val fragmentTagArg = "tag"
    private lateinit var mCustomPagerAdapter: ViewPagerAdapter
    private lateinit var  context: Context
    private lateinit var  toggle : ActionBarDrawerToggle
    private var arrayList = arrayListOf<String>()
    private var place = ArrayList<String>()
    private var recyclerViewAdapter1 : TempeturesRecyclerViewAdapter? = null
    private var recyclerViewAdapter2 : CitiesRecyclerViewAdapter? = null
    private var sharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        context = requireActivity().applicationContext
        return binding.root
    }

    @SuppressLint("CommitPrefEdits", "CutPasteId", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupViewPager()
        setupSearchView()
        setupToggle()
        observeLiveData()
    }

    private fun initViews() {
        mCustomPagerAdapter = ViewPagerAdapter(parentFragmentManager, fragmentTagArg)
        binding.pager.adapter = mCustomPagerAdapter
        binding.pager.setPageTransformer(true, ZoomOutPageTransformer())
        binding.pager.offscreenPageLimit = 5

        val layoutManager = LinearLayoutManager(requireContext())
        val layoutManager2 = LinearLayoutManager(requireContext())

        binding.navView.getHeaderView(0).apply {
            findViewById<RecyclerView>(R.id.places_recyclerview).layoutManager = layoutManager
            findViewById<RecyclerView>(R.id.filtered_place_recyclerview).layoutManager = layoutManager2
            findViewById<SearchView>(R.id.placeName).isIconified = false
        }

        sharedPreferences = requireActivity().getSharedPreferences(
            "com.example.kotlinforecast.view",
            Context.MODE_PRIVATE
        )

        getSavedPlaces()
    }

    private fun setupViewPager() {
        binding.pager.addOnPageChangeListener(object : ViewPager2.OnPageChangeCallback(),
            OnPageChangeListener {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mCustomPagerAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setupSearchView() {
        binding.navView.getHeaderView(0).findViewById<SearchView>(R.id.placeName)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        showRecyclerViews()
                    } else {
                        hideRecyclerViews()
                        filterCityName(newText.toString().uppercase())
                    }
                    return true
                }
            })
    }

    private fun setupToggle() {
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.myDrawerLayout,
            binding.myToolbar,
            R.string.open,
            R.string.close
        )
        binding.myDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun observeLiveData() {
        val cityNames = extractCityNames()

        place= cityNames

        val adapter = CitiesRecyclerViewAdapter(place)
        adapter.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).adapter = adapter

        recyclerViewAdapter2 = CitiesRecyclerViewAdapter(place)
        recyclerViewAdapter2!!.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).adapter = recyclerViewAdapter2
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideRecyclerViews() {
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).visibility = View.GONE
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showRecyclerViews() {
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).visibility = View.VISIBLE
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onHymnClick(citiesModel: String?) {
        if (citiesModel != null) {
            if (mCustomPagerAdapter.count < 10) {
                if (!arrayList.contains(citiesModel.uppercase())) {
                    addCity(citiesModel.uppercase())
                    hideNavDrawer()
                } else {
                    showSnackbar("Bu şehri zaten eklediniz.")
                }
            } else {
                showSnackbar("Çok fazla şehir eklediniz.\n Lütfen şehir silip tekrar deneyiniz.")
            }
        }
    }

    private fun addCity(cityName: String) {
        mCustomPagerAdapter.addPage(CityWeatherData.newInstance(cityName))
        binding.pager.currentItem = mCustomPagerAdapter.count
        arrayList.add(cityName)

        val gson = Gson()
        val json = gson.toJson(arrayList)
        sharedPreferences?.edit()?.putString("TAG", json)?.apply()

        updateRecyclerViewAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerViewAdapter() {
        binding.navView.getHeaderView(0).findViewById<SearchView>(R.id.placeName).setQuery("", false)
        recyclerViewAdapter1 = TempeturesRecyclerViewAdapter(arrayList)
        recyclerViewAdapter1!!.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).adapter = recyclerViewAdapter1
        recyclerViewAdapter1!!.notifyDataSetChanged()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root.rootView, message, Snackbar.LENGTH_LONG).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getSavedPlaces() {
        val gson1 = Gson()
        val json1 = sharedPreferences?.getString("TAG", "")
        val type: Type = object : TypeToken<List<String?>?>() {}.type
        if (json1 != "") {
            arrayList.addAll(gson1.fromJson(json1, type))
            for (a in arrayList) {
                mCustomPagerAdapter.addPage(CityWeatherData.newInstance(a))
                binding.pager.currentItem = 0
                mCustomPagerAdapter.notifyDataSetChanged()
            }
        }
        recyclerViewAdapter1 = TempeturesRecyclerViewAdapter(arrayList)
        recyclerViewAdapter1!!.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).adapter = recyclerViewAdapter1
        recyclerViewAdapter1!!.notifyDataSetChanged()
    }

    private fun hideNavDrawer(){
        binding.myDrawerLayout.closeDrawer(GravityCompat.START)
    }


    override fun onLongClick(currentPage: Int?) {
        if (currentPage != null) {
            mCustomPagerAdapter.notifyDataSetChanged()

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Şehri sil")
            alertDialogBuilder.setMessage("Kayıtlı şehri silmek istediğinizden emin misiniz?")


            alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
                val sharedPreferences = requireActivity().getSharedPreferences("com.example.kotlinforecast.view", Context.MODE_PRIVATE)
                val gson = Gson()
                val json = sharedPreferences.getString("TAG", "")
                val type = object : TypeToken<MutableList<String>?>() {}.type

                val arrayList: MutableList<String> = gson.fromJson(json, type) ?: mutableListOf()

                if (currentPage < arrayList.size) {
                    arrayList.removeAt(currentPage)
                    val editor = sharedPreferences.edit()
                    editor.putString("TAG", gson.toJson(arrayList))
                    editor.apply()
                }

                recyclerViewAdapter1?.removeItem(currentPage)
                mCustomPagerAdapter.removePage(currentPage)
            }

            alertDialogBuilder.setNegativeButton("Hayır") { _, _ ->

            }

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }
    }
    private fun extractCityNames(): ArrayList<String> {
        val assetManager: AssetManager = context.assets
        val inputStream: InputStream = assetManager.open("sehirIsimleri.json")
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val jsonString = String(buffer, Charsets.UTF_8)

        val jsonObject = JSONObject(jsonString)
        val cityNames = ArrayList<String>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val cityName = jsonObject.getString(key)
            cityNames.add(cityName)
        }

        return cityNames
    }
    private fun filterCityName(filter: String) {
        val filterers: ArrayList<String> = ArrayList()
        for (item in place) {
            if (item.toLowerCase(Locale("tr")).contains(filter.toLowerCase(Locale("tr"))) ||
                item.replace("İ", "i", ignoreCase = true).contains(filter.replace("İ", "i", ignoreCase = true))
            ) {
                filterers.add(item)
            }
        }
        if (filterers.isNotEmpty()) {
            recyclerViewAdapter2?.filterList(filterers)
        }
    }
}