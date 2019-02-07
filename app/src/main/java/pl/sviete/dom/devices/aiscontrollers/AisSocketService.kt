package pl.sviete.dom.devices.aiscontrollers

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import pl.sviete.dom.devices.aiscontrollers.models.*
import retrofit2.http.*

interface AisSocketService {
    @GET("/cm?cmnd=Power")
    fun getPowerStatus(): Deferred<Power>

    @GET("/cm?cmnd=Power Toggle")
    fun toggleStatus(): Deferred<Power>

    @GET("/cm")
    fun setup(@Query("cmnd") cmnd: String): Deferred<ResponseBody>

    @GET("/cm?cmnd=Status")
    fun getStatus(): Deferred<Status>
}