package pl.sviete.dom.devices.aiscontrollers

import kotlinx.coroutines.Deferred
import pl.sviete.dom.devices.aiscontrollers.models.BoxInfo
import retrofit2.http.*

interface BoxService {
    @GET("")
    fun getInfo(): Deferred<BoxInfo>
}