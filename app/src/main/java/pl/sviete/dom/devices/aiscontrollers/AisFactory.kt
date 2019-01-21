package pl.sviete.dom.devices.aiscontrollers

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AisFactory {

    fun makeSocketService(ip: String): AisSocketService {
        return Retrofit.Builder()
            .baseUrl("http://" + ip)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(AisSocketService::class.java)
    }
}