package pl.sviete.dom.devices.ui.mainview

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.models.PowerStatus

class MainGridAdapter (
    private val mDevices: ArrayList<DeviceViewModel>,
    private  val presenter: MainView.Presenter
) : BaseAdapter() {

    override fun getCount(): Int {
        return mDevices.size
    }

    override fun getItemId(position: Int): Long {
        return mDevices[position].uid.toLong()
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
                presenter.showDeviceDetail(device)
            }

            view.setOnClickListener {
                presenter.toggleDeviceState(device)
            }
            view.findViewById<ImageView>(R.id.img_device).setOnClickListener {
                presenter.toggleDeviceState(device)
            }
        }
        //view!!.findViewById<ImageButton>(R.id.btnDeviceDetails).tag = position
        val nameTextView = view!!.findViewById(R.id.device_list_item_name) as TextView

        nameTextView.text = device.name
        nameTextView.setOnClickListener {
            presenter.toggleDeviceState(device)
        }

        view.setBackgroundResource(getResourceForStatus(device.status))

        //imageView.setImageResource(device.getImageResource())

        return view
    }

    private fun getResourceForStatus(status: PowerStatus): Int{
        return when (status) {
            PowerStatus.On -> R.drawable.device_list_item_border_on
            PowerStatus.Off -> R.drawable.device_list_item_border_off
            else -> R.drawable.device_list_item_border_unknown
        }
    }
}