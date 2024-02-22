package com.example.mytempapplication

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.mytempapplication.databinding.ActivityMedicationInformationBinding
import com.example.mytempapplication.notificationscheduling.NotificationSchedulerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.hours

class MedicationInformationActivity : AppCompatActivity(), CoroutineScope {
    private val apiClient: ApiClient = ApiClient
    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()
    private var name: String? = null
    private var dosage = 0
    private var minutes: ArrayList<Int> = ArrayList()
    private var hours: ArrayList<Int> = ArrayList()
    private var medicationDosageDesc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMedicationInformationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding) {

            searchButton.setOnClickListener {
                launch {
                    val apiMedicineResponse = apiClient.get(binding.searchBarET.text.toString())
                    apiMedicineResponse?.let {
                        with(binding) {
                            textViewMedName.text = it.name
                            name = it.name
                            textViewMedDetails.text = it.hasPart[0].description
                        }
                        var count = 0
                        for (part in apiMedicineResponse.hasPart) {
                            count++
                            Log.d("OUTER HASPART:", "$count")
                            if (part.headline.lowercase().contains("how and when to")) {
                                medicationDosageDesc = "The NHS states the following advice about dosage information: \n${part.description}"
                                Log.d("PART DESCRIPTION", part.description)
                                val regex = "\\d+ [a-zA-Z\\s]*day"
                                val match = Regex(regex).find(part.description)
                                match?.let { matchResult ->
                                    dosage = try {
                                        matchResult.value[0].digitToInt()
                                    } catch (e: Exception) {
                                        0
                                    }
                                }
                                Log.d("REGEX FOUND:", "${match?.value?.get(0)}")
                                Log.d("REGEX FOUND:", "${match?.value?.get(0)}")
                            }
                            if (dosage > 0) {
                                break
                            }
                        }
                        if (medicationDosageDesc.isEmpty()){
                            TVdosageInfo.text = "No clear medication regimen detected"
                        }
                        else{
                            TVdosageInfo.text = medicationDosageDesc
                            bReminder.visibility = View.VISIBLE
                        }
                            TVdosageInfo.visibility = View.VISIBLE
                    }
                }
            }

            val startForResult = activityCallback()
            bReminder.setOnClickListener {

                startForResult.launch(Intent(this@MedicationInformationActivity, NotificationSchedulerActivity::class.java).also {
                    it.putExtra("Medication Name", name)
                    it.putExtra("Frequency", dosage.toString())
                })

                bConfirmSchedule.visibility = View.VISIBLE

            }

            bConfirmSchedule.setOnClickListener {
                if (hours.isEmpty() || minutes.isEmpty()) {
                    Log.d(
                        "HOURS OR MINUTES IS EMPTY:",
                        "Hours size: ${hours.size} and minutes size: ${minutes.size}"
                    )
                    setResult(Activity.RESULT_CANCELED)
                } else {
                    val returnIntent = Intent().also { intent ->
                        intent.putExtra("Medication Name", name)
                        intent.putExtra("Description", "")
                        intent.putIntegerArrayListExtra("hours", hours)
                        intent.putIntegerArrayListExtra("minutes", minutes)
                    }
                    setResult(Activity.RESULT_OK, returnIntent)
                }
                finish()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (name != null){
            val returnIntent = Intent().also { intent ->
                intent.putExtra("Medication Name", name)
                intent.putExtra("Description", "")
                intent.putIntegerArrayListExtra("hours", hours)
                intent.putIntegerArrayListExtra("minutes", minutes)
            }
            setResult(Activity.RESULT_OK, returnIntent)
        }else{
            setResult(Activity.RESULT_CANCELED)
        }
        super.onBackPressed()
    }

    private fun activityCallback(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            Log.d("INSIDE activityCallback", "WOO")
            if (result.resultCode != RESULT_CANCELED) {
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
//                    Log.d("RETURNED INTENT HOURS", intent.toString())
                    if (intent == null) {
                        Log.d("INTENT IS EMPTY", "intent empty")
                    }
                    hours = intent?.getIntegerArrayListExtra("hours") ?: ArrayList()
                    Log.d("hours size", hours.size.toString())
                    minutes = intent?.getIntegerArrayListExtra("minutes") ?: ArrayList()
                    Log.d("minutes size", minutes.size.toString())
                }
            }
        }
    }


    fun setHoursAndMinutes(hours: ArrayList<Int>, minutes: ArrayList<Int>) {
        this.hours = hours
        this.minutes = minutes
        Log.d("hours size", hours.size.toString())
        Log.d("minutes size", minutes.size.toString())
    }
}


















//
//class PopupDialogFragment(
//    private val context: MedicationInformationActivity,
//    private var name: String,
//    private var dosage: Int = 0,
//    private var hours: ArrayList<Int>,
//    private var minutes: ArrayList<Int>
//) :
//    DialogFragment() {
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return activity?.let { fragmentActivity ->
//            name = fragmentActivity.intent.getStringExtra("name").toString()
//            dosage = fragmentActivity.intent.getIntExtra("dosage", 0)
//            // Use the Builder class for convenient dialog construction.
//            val builder = AlertDialog.Builder(fragmentActivity)
//            var message =
//                "Unable to detect a clear medication regimen. Please visit the NHS website for more information."
//            val quantity = if (dosage == 1) "time" else "times"
//            if (dosage > 0) {
//                message =
//                    "According to the NHS, the typical dosage for $name is $dosage $quantity a day. Would you like to create a reminder?"
//            }
//            val startForActivity = registerForActivityResult(
//                ActivityResultContracts.StartActivityForResult()
//            ) { result: ActivityResult ->
//                Log.d("INSIDE activityCallback", "WOO")
//                if (result.resultCode != RESULT_CANCELED) {
//                    if (result.resultCode == RESULT_OK) {
//                        val intent = result.data
////                    Log.d("RETURNED INTENT HOURS", intent.toString())
//                        if (intent == null) {
//                            Log.d("INTENT IS EMPTY", "intent empty")
//                        }
//                        context.setHoursAndMinutes(
//                            intent?.getIntegerArrayListExtra("hours") ?: ArrayList(),
//                            intent?.getIntegerArrayListExtra("minutes") ?: ArrayList()
//                        )
////                        hours = intent?.getIntegerArrayListExtra("hours") ?: ArrayList()
//                        Log.d("hours size", hours.size.toString())
////                        minutes = intent?.getIntegerArrayListExtra("minutes") ?: ArrayList()
//                        Log.d("minutes size", minutes.size.toString())
//
//                    }
//                }
//            }
//
//
//
//            builder.setMessage(message)
//                .setPositiveButton("Start") { dialog, id ->
//                    startForActivity.launch(
//                        Intent(
//                            activity,
//                            NotificationSchedulerActivity::class.java
//                        ).also { intent ->
//                            intent.putExtra("Medication Name", name)
//                            intent.putExtra("Frequency", dosage.toString())
//                        }
//                    )
//                }
//                .setNegativeButton("Cancel") { dialog, id ->
//                    // User cancelled the dialog.
//                    dialog.dismiss()
//                }
//            // Create the AlertDialog object and return it.
//            builder.create()
//        } ?: throw IllegalStateException("Activity cannot be null")
//
//    }
//
//    private fun activityCallback(): ActivityResultLauncher<Intent> {
//        return registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result: ActivityResult ->
//            Log.d("INSIDE activityCallback", "WOO")
//            if (result.resultCode != RESULT_CANCELED) {
//                if (result.resultCode == RESULT_OK) {
//                    val intent = result.data
////                    Log.d("RETURNED INTENT HOURS", intent.toString())
//                    if (intent == null) {
//                        Log.d("INTENT IS EMPTY", "intent empty")
//                    }
//                    hours = intent?.getIntegerArrayListExtra("hours") ?: ArrayList()
//                    Log.d("hours size", hours.size.toString())
//                    minutes = intent?.getIntegerArrayListExtra("minutes") ?: ArrayList()
//                    Log.d("minutes size", minutes.size.toString())
//
//                }
//            }
//        }
//    }
//}
//
//

