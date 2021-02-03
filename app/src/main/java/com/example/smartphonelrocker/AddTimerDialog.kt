package com.example.smartphonelrocker

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.util.*

class AddTimerDialog : DialogFragment(), NumberPicker.OnValueChangeListener, TextWatcher {
    private lateinit var listener : NoticeDialogListener
    private var selectedHour : Int = 0
    private var selectedMin : Int = 0
    private var alarmName : String = ""

    interface NoticeDialogListener {
        fun onPositiveClick(dialog: DialogFragment, selectedHour : Int, selectedMin: Int, alarmName: String)
        fun onNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            Log.d("item", "call onAttach.")
            this.listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_add_timer_dialog, null)!!
        val builder = AlertDialog.Builder(context)

        val alarmNameEdit: EditText = dialogView.findViewById<EditText>(R.id.alarmName)
        alarmNameEdit.addTextChangedListener(this)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        hourPicker.setOnValueChangedListener(this)
        hourPicker.tag = "hourPicker"
        hourPicker.textSize = 80.0F
        val displayHourArray = Array(24){"%02d".format(it)}
        hourPicker.minValue = 0  // NumberPickerの最小値設定
        hourPicker.maxValue = displayHourArray.size - 1 // NumberPickerの最大値設定
        hourPicker.displayedValues = displayHourArray
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        val currentHour = current.format(formatter).toInt()
        hourPicker.value = currentHour
        this.selectedHour = currentHour

        val minPicker = dialogView.findViewById<NumberPicker>(R.id.minPicker)
        minPicker.setOnValueChangedListener(this)
        minPicker.tag = "minPicker"
        minPicker.textSize = 80.0F
        val displayMinArray = Array(12){"%02d".format((it * 5))}
        minPicker.minValue = 0  // NumberPickerの最小値設定
        minPicker.maxValue = displayMinArray.size - 1 // NumberPickerの最大値設定
        minPicker.displayedValues = displayMinArray

        builder.setView(dialogView)
            .setTitle(R.string.title_add_timer)
            .setPositiveButton(R.string.finish_add_timer) { _, _ ->
                if (this.alarmName != "") {
                    this.listener.onPositiveClick(this, this.selectedHour, this.selectedMin, this.alarmName)
                } else {
                    // TODO: 直す
                    this.listener.onNegativeClick(this)
                }
            }
            .setNegativeButton(R.string.cancel_add_timer) { _, _ ->
                this.listener.onNegativeClick(this)
            }

        return builder.create()
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (picker?.tag == "hourPicker") {
            this.selectedHour = newVal
        } else {
            this.selectedMin = newVal * 5
        }
        Log.d("item", "%d:%d".format(this.selectedHour, this.selectedMin))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        this.alarmName = s.toString()
        Log.d("item", this.alarmName)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }

//    fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
//        // Do something with the time chosen by the user
//    }


}