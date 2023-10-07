package com.example.kotlinweatherforecast.ui.fragment

import LocationHelper
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.*
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlinweatherforecast.R
import com.example.kotlinweatherforecast.anim.ZoomOutPageTransformer
import com.example.kotlinweatherforecast.databinding.FragmentMainScreenBinding
import com.example.kotlinweatherforecast.ui.adapter.CitiesRecyclerViewAdapter
import com.example.kotlinweatherforecast.ui.adapter.TempeturesRecyclerViewAdapter
import com.example.kotlinweatherforecast.ui.adapter.ViewPagerAdapter
import com.example.kotlinweatherforecast.utils.OnItemClickListener
import com.example.kotlinweatherforecast.utils.OnCliclLongRecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.InputStream
import java.lang.reflect.Type
import java.util.Locale

@Suppress("DEPRECATION")
class MainScreen : Fragment() , OnItemClickListener, OnCliclLongRecyclerView {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private val fragmentTagArg = "tag"
    private lateinit var pagerAdapter: ViewPagerAdapter
    private lateinit var  appContext: Context
    private lateinit var  toggle : ActionBarDrawerToggle
    private var selectedCities = arrayListOf<String>()
    private var allCities = ArrayList<String>()
    private var temperatureRecyclerViewAdapter : TempeturesRecyclerViewAdapter? = null
    private var citiesRecyclerViewAdapter : CitiesRecyclerViewAdapter? = null
    private var sharedPreferences : SharedPreferences? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

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
        appContext = requireActivity().applicationContext
        return binding.root
    }

    @SuppressLint("CommitPrefEdits", "CutPasteId", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        laodSavedPlaces()
        registerPermissionLauncher()
        requestLocationPermission()
        setupViewPager()
        setupSearchView()
        setupToggle()
        observeLiveData()
    }

    private fun initializeViews() {
        pagerAdapter = ViewPagerAdapter(parentFragmentManager, fragmentTagArg)
        binding.pager.adapter = pagerAdapter
        binding.pager.offscreenPageLimit = 3
        binding.pager.setPageTransformer(true, ZoomOutPageTransformer())

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

    }

    private fun setupViewPager() {
        binding.pager.addOnPageChangeListener(object : ViewPager2.OnPageChangeCallback(),
            OnPageChangeListener {
            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    pagerAdapter.notifyDataSetChanged()
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

        allCities = cityNames

        val adapter = CitiesRecyclerViewAdapter(allCities)
        adapter.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).adapter = adapter

        citiesRecyclerViewAdapter = CitiesRecyclerViewAdapter(allCities)
        citiesRecyclerViewAdapter!!.setListener(this)

        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.filtered_place_recyclerview).adapter = citiesRecyclerViewAdapter
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



    private fun addCity(cityName: String) {

        if (selectedCities.size>5){
            Toast.makeText(requireContext(),"Çok fazla şehir eklediniz. Lütfen şehir silip tekrar edeneyiniz.",Toast.LENGTH_SHORT).show()
        }
        else{
            pagerAdapter.addPage(CityWeatherData.newInstance(cityName))
            binding.pager.currentItem = pagerAdapter.count
            selectedCities.add(cityName)

            val gson = Gson()
            val json = gson.toJson(selectedCities)
            sharedPreferences?.edit()?.putString("TAG", json)?.apply()

            updateRecyclerViewAdapter()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerViewAdapter() {
        binding.navView.getHeaderView(0).findViewById<SearchView>(R.id.placeName).setQuery("", false)
        temperatureRecyclerViewAdapter = TempeturesRecyclerViewAdapter(selectedCities)
        temperatureRecyclerViewAdapter!!.setListener(this)
        binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).adapter = temperatureRecyclerViewAdapter
        temperatureRecyclerViewAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun laodSavedPlaces() {
        val gson1 = Gson()
        val json1 = sharedPreferences?.getString("TAG", "")
        val type: Type = object : TypeToken<List<String?>?>() {}.type

            if (json1 != "") {
                selectedCities.addAll(gson1.fromJson(json1, type))
                for (a in selectedCities) {
                    pagerAdapter.addPage(CityWeatherData.newInstance(a))
                    pagerAdapter.notifyDataSetChanged()

                }
            }
            temperatureRecyclerViewAdapter = TempeturesRecyclerViewAdapter(selectedCities)
            temperatureRecyclerViewAdapter!!.setListener(this)
            binding.navView.getHeaderView(0).findViewById<RecyclerView>(R.id.places_recyclerview).adapter = temperatureRecyclerViewAdapter
            temperatureRecyclerViewAdapter!!.notifyDataSetChanged()
    }


    private fun extractCityNames(): ArrayList<String> {
        val assetManager: AssetManager = appContext.assets
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
        val filteredCities: ArrayList<String> = ArrayList()
        for (item in allCities) {
            if (item.toLowerCase(Locale("tr")).contains(filter.toLowerCase(Locale("tr"))) ||
                item.replace("İ", "i", ignoreCase = true).contains(filter.replace("İ", "i", ignoreCase = true))
            ) {
                filteredCities.add(item)
            }
        }
        if (filteredCities.isNotEmpty()) {
            citiesRecyclerViewAdapter?.filterCities(filteredCities)
        }
    }

    private fun receiveCurrentLocation(callback: (String?) -> Unit) {
        val locationHelper = LocationHelper(requireActivity())
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationHelper.getLastLocationWithPermission { _, cityName ->
                callback(cityName)
            }
        }
    }

    private fun requestLocationPermission() {
       if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
            Snackbar.make(binding.root,"Lokasyon bilgisini almak için izniniz gerekli",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }.show()
        }
           else{
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
           }
       }
        else{
           loadCurrentLocation()
        }
    }

    private fun registerPermissionLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
             if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                 loadCurrentLocation()
             }
            }
            else{
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCurrentLocation(){
        receiveCurrentLocation { currentCityName ->
            pagerAdapter.addPageFirstPlace(CityWeatherData.newInstance(currentCityName),0)
            binding.pager.currentItem = 0
            pagerAdapter.notifyDataSetChanged()
            println("2")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(citiesModel: String?) {
        if (citiesModel != null) {
            if (pagerAdapter.count < 10) {
                if (!selectedCities.contains(citiesModel.uppercase())) {
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
    override fun onLongItemClick(currentPage: Int?) {
        if (currentPage != null) {
            pagerAdapter.notifyDataSetChanged()

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Şehri sil")
            alertDialogBuilder.setMessage("Kayıtlı şehri silmek istediğinizden emin misiniz?")


            alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
                val sharedPreferences = requireActivity().getSharedPreferences("com.example.kotlinforecast.view", Context.MODE_PRIVATE)
                val gson = Gson()
                val json = sharedPreferences.getString("TAG", "")
                val type = object : TypeToken<MutableList<String>?>() {}.type

                val selectedCities: MutableList<String> = gson.fromJson(json, type) ?: mutableListOf()

                if (currentPage < selectedCities.size) {
                    selectedCities.removeAt(currentPage)
                    val editor = sharedPreferences.edit()
                    editor.putString("TAG", gson.toJson(selectedCities))
                    editor.apply()
                }

                temperatureRecyclerViewAdapter?.removeItem(currentPage)
                pagerAdapter.removePage(currentPage)
            }

            alertDialogBuilder.setNegativeButton("Hayır") { _, _ ->

            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root.rootView, message, Snackbar.LENGTH_LONG).show()
    }
    private fun hideNavDrawer(){
        binding.myDrawerLayout.closeDrawer(GravityCompat.START)
    }
}