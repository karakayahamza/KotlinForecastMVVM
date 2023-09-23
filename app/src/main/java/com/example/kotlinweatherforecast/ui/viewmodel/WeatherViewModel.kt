package com.example.kotlinweatherforecast.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinweatherforecast.data.api.WeatherApi
import com.example.kotlinweatherforecast.data.model.WeatherModel
import com.example.kotlinweatherforecast.data.weatherRepository.WeatherRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private var compositeDisposable: CompositeDisposable? = null
    private val weatherRepository: WeatherRepository

    val weathers = MutableLiveData<WeatherModel?>()
    val error = MutableLiveData<Boolean?>()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)
        weatherRepository = WeatherRepository(weatherApi)
    }

    fun loadData(name: String, appId: String) {
        compositeDisposable = CompositeDisposable()

        compositeDisposable?.add(
            weatherRepository.getData(name, appId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResults(it)
                }, {
                    handleError(it)
                })!!
        )
    }

    private fun handleResults(weatherModel: WeatherModel) {
        weathers.value = weatherModel
        error.value = false
    }

    private fun handleError(throwable: Throwable) {
        error.value = true
        println(throwable.toString())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.clear()
    }
}
