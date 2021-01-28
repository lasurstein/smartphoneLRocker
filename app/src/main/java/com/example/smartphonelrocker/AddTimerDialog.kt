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
        fun onNumberPickerDialogPositiveClick(dialog: DialogFragment, selectedHour : Int, selectedMin: Int, alarmName: String)
        fun onNumberPickerDialogNegativeClick(dialog: DialogFragment)
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


        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        hourPicker.tag = "hourPicker"
        val displayHourArray = Array(24){"%02d".format(it)}
        hourPicker.minValue = 0  // NumberPickerの最小値設定
        hourPicker.maxValue = displayHourArray.size - 1 // NumberPickerの最大値設定
        hourPicker.displayedValues = displayHourArray
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH")
        val currentHour = current.format(formatter).toInt()
        hourPicker.value = currentHour

        val minPicker = dialogView.findViewById<NumberPicker>(R.id.minPicker)
        minPicker.tag = "minPicker"
        val displayMinArray = Array(12){"%02d".format((it * 5))}
        minPicker.minValue = 0  // NumberPickerの最小値設定
        minPicker.maxValue = displayMinArray.size - 1 // NumberPickerの最大値設定
        minPicker.displayedValues = displayMinArray

        builder.setView(dialogView)
            .setTitle(R.string.title_add_timer)
            .setPositiveButton(R.string.finish_add_timer) { _, _ ->
                if (this.alarmName != "") {
                    this.listener.onNumberPickerDialogPositiveClick(this, this.selectedHour, this.selectedMin, this.alarmName)
                } else {
                    // TODO: 直す
                    this.listener.onNumberPickerDialogNegativeClick(this)
                }
            }
            .setNegativeButton(R.string.cancel_add_timer) { _, _ ->
                this.listener.onNumberPickerDialogNegativeClick(this)
            }

        return builder.create()
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (picker?.tag == "hourPicker") {
            this.selectedHour = newVal
        } else {
            this.selectedMin = newVal * 5
        }
        Log.d("item", newVal.toString())

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        this.alarmName = s
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