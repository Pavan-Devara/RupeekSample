package com.sms.rupeek

import com.sms.rupeek.data.WeatherData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


object WeatherDataRepository {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job) // initialize the coroutine scope


    fun weatherSummaryRepoonResult( onResult: (isSuccess: Boolean, response: WeatherData?) -> Unit){
        scope.launch {
            //call the interface to build the http call and execute
            WeatherApi.instance.weatherSummary().enqueue(object : retrofit2.Callback<WeatherData> {
                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    onResult(false, null)//return the response boolean and null response to viewmodel on failure
                }

                override fun onResponse(
                    call: Call<WeatherData>,
                    response: Response<WeatherData>
                ) {
                    if (response != null && response.isSuccessful)
                        onResult(true, response.body()!!)//return the response boolean and response to viewmodel on success
                    else
                        onResult(false, null)//return the response boolean and null response to viewmodel on failure
                }


            })
        }
    }

}