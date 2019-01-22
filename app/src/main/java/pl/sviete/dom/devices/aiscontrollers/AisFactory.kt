package pl.sviete.dom.devices.aiscontrollers

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object AisFactory {

    fun makeSocketService(ip: String): AisSocketService {
        val gson = GsonBuilder()
            //.registerTypeAdapter(Id::class.java, IdTypeAdapter())
            //.enableComplexMapKeySerialization()
            //.serializeNulls()
            //.setDateFormat(DateFormat.LONG)
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            //.setPrettyPrinting()
            //.setVersion(1.0)
            .create()

        return Retrofit.Builder()
            .baseUrl("http://" + ip)
            //.addConverterFactory(MoshiConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build().create(AisSocketService::class.java)
    }
}