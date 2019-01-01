package pl.sviete.dom.devices.ui.mainview

import android.content.Context
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import android.view.ViewGroup
import pl.sviete.dom.devices.db.AisDeviceEntity
import android.view.LayoutInflater
import pl.sviete.dom.devices.R

class MainGridAdapter (
    val mContext: Context,
    val mDevices: ArrayList<AisDeviceEntity>
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
            val layoutInflater = LayoutInflater.from(mContext)
            view = layoutInflater.inflate(R.layout.device_list_item, null)
        }

        val nameTextView = view!!.findViewById(R.id.device_list_item_name) as TextView

        nameTextView.text = device.name
        //imageView.setImageResource(device.getImageResource())

        return view
    }
}