package com.example.kotlinweatherforecast.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import android.widget.TextView
import com.example.kotlinweatherforecast.R
import com.example.kotlinweatherforecast.databinding.FragmentCityWeatherDataBinding
import com.example.kotlinweatherforecast.data.model.WeatherModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.kotlinweatherforecast.ui.viewmodel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class CityWeatherData : Fragment() {
    private var _binding: FragmentCityWeatherDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WeatherViewModel
    companion object {
        fun newInstance(cityName: String?): CityWeatherData {
            val fragment = CityWeatherData()
            val args = Bundle()
            args.putString("cityName", cityName)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityWeatherDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this)[WeatherViewModel::class.java]
        arguments?.getString("cityName")
            ?.let { viewModel.loadData(it, "61e8b0259c092b1b9a15474cd800ee25") }
        observeLiveData()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateCurrentWeather(weather: WeatherModel) {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val outputDateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale("tr"))

        val convertedTime = weather.weatherList[0].dt_txt
        val time: Date = inputDateFormat.parse(convertedTime.toString()) ?: Date()
        val formattedTime = outputDateFormat.format(time)

        binding.textDay.text = formattedTime
        binding.cityName.text =  weather.city.name.toString().replace(Regex(" Province$"), "")
        val tempValue = String.format("%.1f", weather.weatherList[0].main?.temp).toDouble()
        binding.textViewTemperature.text = tempValue.toString()
        binding.textHumidity.text = weather.weatherList[0].main?.humidity.toString() + "%"
        binding.textPressure.text = weather.weatherList[0].main?.pressure.toString() + "hPa"
        binding.textWind.text = ((weather.weatherList[0].wind?.speed)?.times(3.6)?.roundToInt()).toString() +"km/h"
        uploadWeatherIcon(0, weather.weatherList[0].weather!![0].icon.toString())
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun updateHourlyWeather(weather: WeatherModel) {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val outputDateFormat = SimpleDateFormat("HH:mm")

        for (i in 1 until 9) {
            val tempTextView: TextView = requireView().findViewById(textViewIds[i - 1]) as TextView
            val timeTextView: TextView = requireView().findViewById(textTimeIds[i - 1]) as TextView

            val convertedTime = weather.weatherList[i].dt_txt
            val time: Date = inputDateFormat.parse(convertedTime.toString()) ?: Date()
            val formattedTime = outputDateFormat.format(time)

            tempTextView.text = weather.weatherList[i].main?.temp.toString().substringBefore(".") + "°C"
            timeTextView.text = formattedTime
            uploadWeatherIcon(i, weather.weatherList[i].weather!![0].icon.toString())
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun updateDailyWeather(weather: WeatherModel) {
        var dayIndex = 0
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        for (x in 0 until weather.weatherList.size) {
            val convertedTime = weather.weatherList[x].dt_txt
            val time: Date = inputDateFormat.parse(convertedTime.toString()) ?: Date()
            val formattedTime = SimpleDateFormat("HH:mm").format(time)

            val convertedDay = weather.weatherList[x].dt_txt
            val day: Date = inputDateFormat.parse(convertedDay.toString()) ?: Date()
            val formattedDay = SimpleDateFormat("EEEE",Locale("tr")).format(day)

            if (formattedTime == "12:00") {
                val dayTextView: TextView = requireView().findViewById(textDayIds[dayIndex]) as TextView
                val tempTextView: TextView = requireView().findViewById(textMinTempIds[dayIndex]) as TextView

                dayTextView.text = formattedDay
                tempTextView.text = weather.weatherList[x].main?.temp.toString().substringBefore(".") + "°C"
                uploadWeatherIcon(dayIndex + 10, weather.weatherList[x].weather!![0].icon.toString())

                dayIndex++
            }
        }
    }

    private fun observeLiveData() {
        viewModel.weathers.observe(viewLifecycleOwner) { weather ->
            weather?.let {
                updateCurrentWeather(weather)
                updateHourlyWeather(weather)
                updateDailyWeather(weather)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                viewModel.weathers.removeObservers(viewLifecycleOwner)
                Snackbar.make(binding.root.rootView, "Lütfen internet bağlantınızı kontrol edin.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadWeatherIcon(index: Int, iconId: String) {
        val imageResource: ImageView? = when (index) {
            0 -> binding.mainTempImage
            1 -> binding.imageViewTemp1
            2 -> binding.imageViewTemp2
            3 -> binding.imageViewTemp3
            4 -> binding.imageViewTemp4
            5 -> binding.imageViewTemp5
            6 -> binding.imageViewTemp6
            7 -> binding.imageViewTemp7
            8 -> binding.imageViewTemp8
            10 -> binding.imageViewTemp10
            11 -> binding.imageViewTemp11
            12 -> binding.imageViewTemp12
            13 -> binding.imageViewTemp13
            14 -> binding.imageViewTemp14
            else -> null
        }

        when (iconId) {
            "01d" -> imageResource?.setImageResource(R.drawable.sunny)
            "01n" -> imageResource?.setImageResource(R.drawable.night)
            "02d" -> imageResource?.setImageResource(R.drawable.partlycloudyday)
            "02n" -> imageResource?.setImageResource(R.drawable.partlycloudydaynight)
            "03d", "03n" -> {
                imageResource?.setImageResource(R.drawable.cloud)
                imageResource?.setImageResource(R.drawable.cloudy)
            }
            "04d", "04n" -> imageResource?.setImageResource(R.drawable.cloudy)
            "09d", "09n" -> imageResource?.setImageResource(R.drawable.freezingrain)
            "10d" -> imageResource?.setImageResource(R.drawable.heavyrainswrsday)
            "10n" -> imageResource?.setImageResource(R.drawable.heavyrainswrsdaynight)
            "11d", "11n" -> imageResource?.setImageResource(R.drawable.cloudrainthunder)
            "13d", "13n" -> imageResource?.setImageResource(R.drawable.occlightsnow)
            "50d", "50n" -> imageResource?.setImageResource(R.drawable.freezingfog)
            else -> imageResource?.setImageResource(R.drawable.notfound)
        }
    }

    // XML id's
    private val textViewIds = intArrayOf(
        R.id.tempeture2,
        R.id.tempeture3,
        R.id.tempeture4,
        R.id.tempeture5,
        R.id.tempeture6,
        R.id.tempeture7,
        R.id.tempeture8,
        R.id.tempeture9
    )

    private val textTimeIds = intArrayOf(
        R.id.time2,
        R.id.time3,
        R.id.time4,
        R.id.time5,
        R.id.time6,
        R.id.time7,
        R.id.time8,
        R.id.time9
    )

    private val textDayIds = intArrayOf(
        R.id.firstDay1,
        R.id.firstDay2,
        R.id.firstDay3,
        R.id.firstDay4,
        R.id.firstDay5
    )

    private val textMinTempIds = intArrayOf(
        R.id.firstMinTemp1,
        R.id.firstMinTemp2,
        R.id.firstMinTemp3,
        R.id.firstMinTemp4,
        R.id.firstMinTemp5
    )

    private val tempDaysImages = intArrayOf(
        R.id.imageViewTemp10,
        R.id.imageViewTemp11,
        R.id.imageViewTemp12,
        R.id.imageViewTemp13,
        R.id.imageViewTemp14
    )
}