package com.example.smartphonelrocker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditTimerDialog : DialogFragment(), NumberPicker.OnValueChangeListener, TextWatcher {

    private lateinit var listener: NoticeDialogListener

    private var isExist: Int = -1

    private var selectedHour: Int = 0
    private var selectedMin: Int = 0
    private var selectedName: String = ""

    private var alarmId: Int = 0
    private var hour: Int = 0
    private var min: Int = 0
    private var name: String = ""

    companion object {
        const val EXIST_ALARM = 1
        const val NON_EXIST_ALARM = 0
    }

    interface NoticeDialogListener {
        fun onSaveClick(
            dialog: DialogFragment,
            alarmId: Int?,
            selectedName: String,
            selectedHour: Int,
            selectedMin: Int
        )
        fun onDeleteClick(dialog: DialogFragment, alarmId: Int)
        fun onCancelClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            Log.d("item", "call EditTimerDialog onAttach.")
            this.listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_edit_timer_dialog, null)!!
        val builder = AlertDialog.Builder(context)

        val bundle = arguments
        if (bundle != null) {
            // 既存アラーム編集時
            this.isExist = EXIST_ALARM
            this.alarmId = bundle.getInt("id")
            this.hour = bundle.getInt("hour")
            this.min = bundle.getInt("min")
            this.name = bundle.getString("name").toString()
        } else {
            // 新規アラーム作成時
            this.isExist = NON_EXIST_ALARM
        }

        // アラーム名
        val alarmNameForm: EditText = dialogView.findViewById<EditText>(R.id.alarmName)
        alarmNameForm.addTextChangedListener(this)

        // 時間
        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        hourPicker.setOnValueChangedListener(this)
        hourPicker.tag = "hourPicker"
        hourPicker.textSize = 80.0F
        val displayHourArray = Array(24) { "%02d".format(it) }
        hourPicker.minValue = 0
        hourPicker.maxValue = displayHourArray.size - 1
        hourPicker.displayedValues = displayHourArray

        val minPicker = dialogView.findViewById<NumberPicker>(R.id.minPicker)
        minPicker.setOnValueChangedListener(this)
        minPicker.tag = "minPicker"
        minPicker.textSize = 80.0F
        val displayMinArray = Array(12) { "%02d".format((it * 5)) }
        minPicker.minValue = 0
        minPicker.maxValue = displayMinArray.size - 1
        minPicker.displayedValues = displayMinArray

        if (this.isExist == EXIST_ALARM) {
            alarmNameForm.setText(this.name)
            hourPicker.value = this.hour
            this.selectedHour = this.hour
            minPicker.value = this.min / 5
            this.selectedMin = this.min
        } else {
            // TODO:calenderからの時刻取得に統一した方が良いかも
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH")
            val currentHour = current.format(formatter).toInt()
            hourPicker.value = currentHour
            this.selectedHour = currentHour
        }

        // View作成
        builder.setView(dialogView)
            .setPositiveButton(R.string.finish) { _, _ ->
                if (this.isExist == 1) {
                    if ((this.name != this.selectedName) || (this.hour != this.selectedHour) || (this.min != this.selectedMin)) {
                        this.listener.onSaveClick(
                            this,
                            this.alarmId,
                            this.selectedName,
                            this.selectedHour,
                            this.selectedMin
                        )
                    }
                } else {
                    if (this.selectedName != "") {
                        this.listener.onSaveClick(
                            this,
                            null,
                            this.selectedName,
                            this.selectedHour,
                            this.selectedMin
                        )
                    } else {
                        // TODO: 直す
                        this.listener.onCancelClick(this)
                    }
                }
            }
            .setNeutralButton(R.string.cancel) { _, _ ->
                this.listener.onCancelClick(this)
            }

        if (this.isExist == EXIST_ALARM) {
            builder.setNegativeButton(R.string.delete) { _, _ ->
                this.listener.onDeleteClick(this, this.alarmId)
            }
                .setTitle(R.string.edit_timer)
        } else {
            builder.setTitle(R.string.add_timer)
        }

        return builder.create()
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (picker?.tag == "hourPicker") {
            this.selectedHour = newVal
        } else {
            this.selectedMin = newVal * 5
        }
//        Log.d("item", "%d:%d".format(this.selectedHour, this.selectedMin))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null) {
            this.selectedName = ""
        } else {
            this.selectedName = s.toString()
        }
//        Log.d("item", this.selectedName)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }
}