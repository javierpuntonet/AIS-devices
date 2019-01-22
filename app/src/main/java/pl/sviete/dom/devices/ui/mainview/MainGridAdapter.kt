package pl.sviete.dom.devices.ui.mainview

import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.db.AisDeviceEntity
import android.view.LayoutInflater
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.AisFactory
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import retrofit2.HttpException

class MainGridAdapter (
    val mDevices: ArrayList<DeviceViewModel>,
    private val onClick: (DeviceViewModel) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return mDevices.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any? {
        return mDevices[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = mDevices[position]

        var view = convertView
        if (view == null) {
            val layoutInflater = LayoutInflater.from(parent.context)
            view = layoutInflater.inflate(R.layout.device_list_item, null)
            view.findViewById<ImageButton>(R.id.btnDeviceDetails).setOnClickListener {
                onClick(mDevices[position])
            }
        }
        //view!!.findViewById<ImageButton>(R.id.btnDeviceDetails).tag = position
        val nameTextView = view!!.findViewById(R.id.device_list_item_name) as TextView

        nameTextView.text = device.name + ":" + device.status
        //imageView.setImageResource(device.getImageResource())

        return view
    }
}