package pl.sviete.dom.devices.ui.mainview

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

class SquareConstraintLayout  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}