package pl.sviete.dom.devices.ui.details

import android.content.Context
import android.content.Intent
import pl.sviete.dom.devices.models.AisDeviceType
import pl.sviete.dom.devices.ui.boxdetails.BoxDetailsActivity
import pl.sviete.dom.devices.ui.devicedetails.DeviceDetailsActivity

class DetailsFabric {
    companion object {
        fun openDetialsView(context: Context, id: Long, type: AisDeviceType?){
            val intent = when(type)
            {
                AisDeviceType.Box ->
                    Intent(context, BoxDetailsActivity::class.java).apply {
                        putExtra(BoxDetailsActivity.ARG_DEVICE_ITEM_ID, id)
                    }
                else ->
                    Intent(context, DeviceDetailsActivity::class.java).apply {
                        putExtra(DeviceDetailsActivity.ARG_DEVICE_ITEM_ID, id)
                    }
            }
            context.startActivity(intent)
        }
    }
}