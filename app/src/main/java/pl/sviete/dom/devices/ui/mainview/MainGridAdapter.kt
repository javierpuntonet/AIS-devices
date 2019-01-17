package pl.sviete.dom.devices.ui.mainview

import android.view.View
import android.view.ViewGroup
import pl.sviete.dom.devices.db.AisDeviceEntity
import android.view.LayoutInflater
import android.widget.*
import pl.sviete.dom.devices.R

class MainGridAdapter (
    val mDevices: ArrayList<AisDeviceEntity>,
    private val onClick: (AisDeviceEntity) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return mDevices.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any? {
        return null
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

        nameTextView.text = device.name
        //imageView.setImageResource(device.getImageResource())

        return view
    }
}