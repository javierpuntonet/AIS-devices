package pl.sviete.dom.devices.ui.mainview

import android.content.Context
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import android.view.ViewGroup
import pl.sviete.dom.devices.db.AisDeviceEntity

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
        val dummyTextView = TextView(mContext)
        val device = mDevices[position]
        if (device != null) {
            dummyTextView.text = device.name
        }
        return dummyTextView
    }
}