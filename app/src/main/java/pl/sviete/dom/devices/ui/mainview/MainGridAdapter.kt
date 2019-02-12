package pl.sviete.dom.devices.ui.mainview

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus
import android.widget.TextView
import pl.sviete.dom.devices.models.AisDeviceType

class MainGridAdapter (
    private val mDevices: ArrayList<DeviceViewModel>,
    private  val presenter: MainView.Presenter
) : BaseAdapter() {

    override fun getCount(): Int {
        return mDevices.size
    }

    override fun getItemId(position: Int): Long {
        val device = mDevices[position]
        return if (device.uid != null) device.uid.toLong() else -1
    }

    override fun getItem(position: Int): Any? {
        return mDevices[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val device = getItem(position) as DeviceViewModel
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            val layoutInflater = LayoutInflater.from(parent.context)
            view = layoutInflater.inflate(R.layout.device_list_item, null)

            holder = ViewHolder()
            holder.detailsButton = view.findViewById(R.id.btnDeviceDetails)
            holder.imageView = view.findViewById(R.id.img_device)
            holder.nameText = view.findViewById(R.id.device_list_item_name)
            view.tag = holder
        }
        else{
            holder = view.tag as ViewHolder
        }

        view!!.setOnClickListener {
            presenter.toggleDeviceState(device)
        }

        holder.detailsButton!!.setImageResource(getResourceForFounded(device.isFounded))
        holder.detailsButton!!.setOnClickListener {
            presenter.showDeviceDetail(device)
        }

        holder.imageView!!.setOnClickListener {
            presenter.toggleDeviceState(device)
        }

        holder.nameText!!.text = device.name
        holder.nameText!!.setOnClickListener {
            presenter.toggleDeviceState(device)
        }

        view.setBackgroundResource(getResourceForStatus(device.status))

        holder.imageView!!.setImageResource(getResourceForType(device.type))

        return view
    }

    private fun getResourceForStatus(status: PowerStatus): Int{
        return when (status) {
            PowerStatus.On -> R.drawable.device_list_item_border_on
            PowerStatus.Off -> R.drawable.device_list_item_border_off
            else -> R.drawable.device_list_item_border_unknown
        }
    }

    private fun getResourceForType(type: AisDeviceType?): Int {
        return when (type) {
            AisDeviceType.Bulb -> R.drawable.bulb
            AisDeviceType.Socket -> R.drawable.socket
            else -> R.drawable.logo
        }
    }

    private fun getResourceForFounded(isFounded: Boolean) : Int {
        return if (isFounded) R.drawable.plus else R.drawable.dots_vertical
    }

    internal inner class ViewHolder {
        var imageView: ImageView? = null
        var nameText: TextView? = null
        var detailsButton: ImageButton? = null
    }
}