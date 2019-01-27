package pl.sviete.dom.devices.aiscontrollers

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import java.lang.Exception

class AisSocketController {
    /*fun getPowerStatus(ip: String) : Deferred<PowerStatus> {
        var status = PowerStatus.UNKNOWN
        val service = AisFactory.makeSocketService(ip)
        GlobalScope.launch(Dispatchers.Main) {
            val request = service.getPowerStatus()
            try {
                val response = request.await()
                return response.POWER
            } catch (e: Exception) {

            }
        }
    }*/
}