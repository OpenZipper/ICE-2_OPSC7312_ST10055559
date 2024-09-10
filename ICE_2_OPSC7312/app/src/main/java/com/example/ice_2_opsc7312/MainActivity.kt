package com.example.ice_2_opsc7312

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ice_2_opsc7312.ui.theme.ICE_2_OPSC7312Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: CurrencyApiService
    private val apiKey = "https://currency.getgeoapi.com/"

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("https://currency.getgeoapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Initialize API Service
        apiService = retrofit.create(CurrencyApiService::class.java)

        // Example conversion call
        convertCurrency("USD", "EUR", "100")
    }

    private fun convertCurrency(fromCurrency: String, toCurrency: String, amount: String) {
        apiService.convertCurrency(apiKey, fromCurrency, toCurrency, amount).enqueue(object :
            Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    val currencyResponse = response.body()
                    // Handle the successful response here
                    Log.d("Currency Conversion", "Conversion Rate: ${currencyResponse?.rates?.get(toCurrency)?.rate_for_amount}")
                } else {
                    Log.e("Currency Conversion", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                Log.e("Currency Conversion", "Failed to get response: ${t.message}")
            }
        })
    }

    data class CurrencyResponse(
        val base_currency_code: String,
        val base_currency_name: String,
        val amount: String,
        val updated_date: String,
        val rates: Map<String, CurrencyRate>
    )

    data class CurrencyRate(
        val currency_name: String,
        val rate: String,
        val rate_for_amount: String
    )

    interface CurrencyApiService {
        @GET("v2/currency/convert")
        fun convertCurrency(
            @Query("api_key") apiKey: String,
            @Query("from") fromCurrency: String,
            @Query("to") toCurrency: String,
            @Query("amount") amount: String
        ): Call<CurrencyResponse>
    }
}