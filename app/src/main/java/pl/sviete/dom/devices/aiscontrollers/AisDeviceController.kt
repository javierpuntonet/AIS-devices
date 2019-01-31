package pl.sviete.dom.devices.aiscontrollers

import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import java.lang.Exception
import java.net.URLEncoder

class AisDeviceController {

    companion object {
        suspend fun getPowerStatus(ip: String): PowerStatus? {
            val service = AisFactory.makeSocketService(ip)
            val request = service.getPowerStatus()
            try {
                val response = request.await()
                return response.Power
            } catch (e: Exception) {

            }
            return null
        }

        suspend fun toggleStatus(ip: String): PowerStatus? {
            val service = AisFactory.makeSocketService(ip)
            val request = service.toggleStatus()
            try {
                val response = request.await()
                return response.Power
            } catch (e: Exception) {

            }
            return null
        }

         suspend fun setupNew(name: String, ssid: String, password: String): Boolean{
            val service = AisFactory.makeSocketService("192.168.4.1")
            val query = "Backlog FriendlyName1 $name; SSId1 $ssid; Password1 $password"//URLEncoder.encode("Backlog FriendlyName1 $name; SSId1 $ssid; Password1 $password","UTF-8")
            val request = service.setup(query)
            try {
                request.await()
                return true
            } catch (e: Exception) {

            }
            return false
        }
    }
}