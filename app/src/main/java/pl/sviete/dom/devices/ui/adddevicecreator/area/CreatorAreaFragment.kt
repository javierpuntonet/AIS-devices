package pl.sviete.dom.devices.ui.adddevicecreator.area

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.fragment_creator_area.*
import pl.sviete.dom.devices.R
import pl.sviete.dom.devices.helpers.AisDeviceHelper
import pl.sviete.dom.devices.models.AisDeviceType
import android.text.InputType
import android.widget.EditText
import android.app.AlertDialog
import android.widget.ArrayAdapter
import pl.sviete.dom.devices.ui.areas.AreaViewModel
import android.view.KeyEvent.KEYCODE_BACK
import android.view.*

private const val ARG_NAME = "NAME"
private const val ARG_TYPE = "TYPE"
private const val ARG_ID = "ID"

class CreatorAreaFragment : Fragment(), CreatorAreaView.View {
    override val presenter: CreatorAreaView.Presenter = CreatorAreaPresenter(this, this)

    private var mDeviceName: String? = null
    private var mDeivceType: AisDeviceType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mDeviceName = it.getString(ARG_NAME)
            mDeivceType = AisDeviceType.fromInt(it.getInt(ARG_TYPE))
            presenter.storeInitData(it.getLong(ARG_ID))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        txt_dev_name_creator_area.text = mDeviceName
        img_dev_type_creator_area.setImageResource(AisDeviceHelper.getResourceForType(mDeivceType))

        btn_finish_creator_area.setOnClickListener {
            presenter.finishClick(spinner_area_creator.selectedItem as AreaViewModel)
        }

        btn_add_area_creator.setOnClickListener {
            addArea()
        }

        view!!.run {
            setFocusableInTouchMode(true)
            requestFocus()
            setOnKeyListener { _, keyCode, _ ->
                activity!!.finish()
                (keyCode == KEYCODE_BACK)
            }
        }
        presenter.loadView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creator_area, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attach(context as CreatorAreaView.OnFinishCreatorArea)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detach()
    }

    override fun refreshAreas(areas: List<AreaViewModel>, selectArea: Long) {
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, areas)
        spinner_area_creator.adapter = adapter
        selectArea(selectArea)
    }

    override fun selectArea(selectArea: Long) {
        val position = (spinner_area_creator.adapter as ArrayAdapter<AreaViewModel>).getPosition(AreaViewModel(selectArea, ""))
        spinner_area_creator.setSelection(position)
    }

    private fun addArea(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.name_new_area)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton(R.string.add) { _, _ ->
            run {
                val areaName = input.text.toString()
                if (areaName.isNotEmpty())
                    presenter.addArea(areaName)
            }}
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    companion object {

        @JvmStatic
        fun newInstance(deviceId: Long, deviceName: String, deviceType: AisDeviceType) =
            CreatorAreaFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, deviceId)
                    putString(ARG_NAME, deviceName)
                    putInt(ARG_TYPE, deviceType.value)
                }
            }
    }
}
