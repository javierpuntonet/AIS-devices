package pl.sviete.dom.devices.ui.areas

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import pl.sviete.dom.devices.R

class AreaDialog {
    companion object {
        fun showAddNewArea(context: Context, addArea: (name: String) -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.name_new_area)
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(R.string.add) { _, _ ->
                run {
                    val areaName = input.text.toString()
                    if (areaName.isNotEmpty())
                        addArea(areaName)
                }
            }
            builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }
}