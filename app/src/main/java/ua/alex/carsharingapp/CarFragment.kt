package ua.alex.carsharingapp


import android.app.Fragment
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.BufferedReader

/**
 * A simple [Fragment] subclass.
 *
 */
class CarFragment : Fragment(), ColorPickerDialogListener {

    lateinit var colorButton: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorButton = view!!.findViewById(R.id.color_button)
        colorButton.setOnClickListener {
//            ColorPickerDialog.newBuilder().setColor(0).show(activity)
            val drawable = (colorButton.background)
            val colorDrawable = if (drawable is ColorDrawable)
                drawable.color
            else 0
            AmbilWarnaDialog(activity, colorDrawable, object: AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
                }

            }).show()
        }
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        view.findViewById<Button>(R.id.color_button).setBackgroundColor(color)
    }

    override fun onDialogDismissed(dialogId: Int) {
    }
}
