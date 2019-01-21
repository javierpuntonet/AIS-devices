package pl.sviete.dom.devices.aiscontrollers

import kotlinx.coroutines.Deferred
import pl.sviete.dom.devices.aiscontrollers.models.Power
import retrofit2.http.GET

interface AisSocketService {
    @GET("/cm?cmnd=Power")
    fun getPowerStatus(): Deferred<Power>
}