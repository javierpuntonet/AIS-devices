package pl.sviete.dom.devices.aiscontrollers

import android.util.Log
import pl.sviete.dom.devices.aiscontrollers.models.BoxInfo
import java.lang.Exception

class BoxRestController {
    companion object {

        private val tag = BoxRestController::class.java.simpleName

        suspend fun getInfo(ip: String): BoxInfo? {
            val service = AisFactory.makeBoxService(ip)
            val request = service.getInfo()
            try {
                return request.await()
            } catch (e: Exception) {
                Log.e(BoxRestController.tag, "getInfo", e)
            }
            return null
        }
    }
}