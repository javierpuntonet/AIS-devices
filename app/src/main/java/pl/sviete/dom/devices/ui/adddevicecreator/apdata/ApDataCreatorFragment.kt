package pl.sviete.dom.devices.ui.adddevicecreator.apdata

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_creator_ap_data.*
import pl.sviete.dom.devices.R
import android.text.method.PasswordTransformationMethod

private const val ARG_DEVICE_SSID = "device_ssid"

class ApDataCreatorFragment : Fragment(), ApDataView.View {
    override val presenter: ApDataView.Presenter = ApDataPresenter(this, this)

    private var mFirstLoad = true
    private var mSpinnerAdapter : ArrayAdapter<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_ap_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_cancel.setOnClickListener{
            presenter.onCancel()
        }

        btn_accept.setOnClickListener{
            val name = spinner_ap.selectedItem.toString()
            val password = txt_ap_password.text.toString()

            presenter.onAccept(name, password, chkSavePassword.isChecked)
        }

        btn_ap_data_refresh.setOnClickListener {
            presenter.refreshAPList()
        }

        chkShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked)
                txt_ap_password.transformationMethod = null
            else
                txt_ap_password.transformationMethod = PasswordTransformationMethod()
        }

        arguments?.let {
            presenter.loadData(it.getString(ARG_DEVICE_SSID))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.onDetach()
    }

    override fun setPassword(password: String) {
        txt_ap_password.setText(password)
    }

    override fun setAPDataSource(aps: List<String>) {
        if (context != null) {
            mSpinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, aps)
            spinner_ap.adapter = mSpinnerAdapter
            spinner_ap.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    txt_ap_password.setText("")
                    if (spinner_ap.selectedItem != null) {
                        presenter.onAPSelected(spinner_ap.selectedItem.toString())
                    }
                }
            }
        }
    }

    override fun selectAP(apName: String?) {
        if (apName.isNullOrEmpty()) {
            spinner_ap?.setSelection(0)
        }
        else {
            var idx = mSpinnerAdapter!!.getPosition(apName)
            if (idx < 0)
                idx = 0
            spinner_ap?.setSelection(idx)
        }
        if (mFirstLoad)
            mFirstLoad = false
        else
            spinner_ap.performClick()
    }

    companion object {
        @JvmStatic
        fun newInstance(ssidToAdd: String) = ApDataCreatorFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_DEVICE_SSID, ssidToAdd)
            }
        }
    }
}
