package pl.sviete.dom.devices.ui.adddevicecreator.connectdevice

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_creator_connect.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.aiscontrollers.AisDeviceConfigurator
import pl.sviete.dom.devices.models.AisDeviceType

private const val ARG_DEVICE_NAME = "device_name"
private const val ARG_DEVICE_SSID = "device_ssid"
private const val ARG_AP_NAME = "app_name"
private const val ARG_AP_PASSWORD = "app_password"

class ConnectDeviceFragment : Fragment(), ConnectDeviceView.View  {

    override val presenter: ConnectDeviceView.Presenter = ConnectDevicePresenter(this, this)

    private var mDeviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mDeviceName = it.getString(ARG_DEVICE_NAME)
            val deviceSsid = it.getString(ARG_DEVICE_SSID)
            val appName = it.getString(ARG_AP_NAME)
            val appPassword= it.getString(ARG_AP_PASSWORD)
            presenter.pairDevice(deviceSsid!!, appName!!, appPassword!!, mDeviceName!!)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        txt_connect_device_name.text = mDeviceName!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_connect, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attach(context as ConnectDeviceView.OnConnectDevice)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun onPairError(errorCode: AisDeviceConfigurator.ErrorCode) {
        activity?.runOnUiThread {
            var text = resources.getString(R.string.unknown_error)
            if (errorCode != AisDeviceConfigurator.ErrorCode.OK)
                text += errorCode.text(resources)
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }
    }

    override fun setIconForDevice(iconResourceId: Int) {
        activity?.runOnUiThread {
            img_connect_device.setImageResource(iconResourceId)
        }
    }

    override fun onStep(step: ConnectStep) {
        activity?.runOnUiThread {
            txt_step.text = resources.getString(
                when (step) {
                    ConnectStep.ConnectToDevice -> R.string.connect_to_device
                    ConnectStep.SendConfiguration -> R.string.send_configuration
                    ConnectStep.NetworkScan1 -> R.string.network_scan_1
                    ConnectStep.NetworkScan2 -> R.string.network_scan_2
                    ConnectStep.NetworkScan3 -> R.string.network_scan_3
                    ConnectStep.Waiting -> R.string.waiting_for_device
                }
            )
        }
    }

    private fun AisDeviceConfigurator.ErrorCode.text(resources: Resources): String {
        if (this == AisDeviceConfigurator.ErrorCode.TIMEOUT)
            return System.getProperty("line.separator")!! + resources.getString(R.string.connect_ap_timeout)
        return ""
    }



    companion object {
        @JvmStatic
        fun newInstance(deviceName: String, deviceSsid: String, apName: String, apPassword: String) =
            ConnectDeviceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DEVICE_NAME, deviceName)
                    putString(ARG_DEVICE_SSID, deviceSsid)
                    putString(ARG_AP_NAME, apName)
                    putString(ARG_AP_PASSWORD, apPassword)
                }
            }
    }
}
