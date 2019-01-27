package pl.sviete.dom.devices.aiscontrollers

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import java.lang.Exception

class AisDeviceController {

    companion object {
        suspend fun getPowerStatus(ip: String) : PowerStatus? {
            val service = AisFactory.makeSocketService(ip)
            val request = service.getPowerStatus()
            try {
                val response = request.await()
                return response.Power
            } catch (e: Exception) {

            }
            return null
        }

        suspend fun toggleStatus(ip: String) : PowerStatus? {
            val service = AisFactory.makeSocketService(ip)
            val request = service.toggleStatus()
            try {
                val response = request.await()
                return response.Power
            } catch (e: Exception) {

            }
            return null
        }
    }


}