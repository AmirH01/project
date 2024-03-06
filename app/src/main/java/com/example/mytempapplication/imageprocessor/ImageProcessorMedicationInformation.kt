package com.example.mytempapplication.imageprocessor

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.mytempapplication.databinding.ActivityMedicationInformationBinding
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.Exception


class ImageProcessorMedicationInformation(
    private val applicationContext: Context,
    private val uri: Uri,
    private val binding: ActivityMedicationInformationBinding
) {

    companion object {
        private val numericMap = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var text: Task<Text>

    operator fun invoke() {
        runBlocking {
            async {
                val image = InputImage.fromFilePath(applicationContext, this@ImageProcessorMedicationInformation.uri)
                text = recognizer.process(image).addOnSuccessListener {
                    Log.i("SUCCESSFUL", it.text)

                    val medicationName = getMedicationName()
                    val frequency = getFrequency().toString()

                    Log.d("MEDICATION NAME", medicationName)
                    binding.searchBarET.setText(medicationName)

                    Log.d("FREQUENCY", frequency)
//                    binding.frequencyET.setText(frequency)
                }.addOnFailureListener {
                    Log.e("FAILED", it.message.toString())
                }
            }.await()
        }
    }

    fun getFrequency(): Int {
        for (block in text.result.textBlocks) {
            for (line in block.lines) {
                val elements: List<String> = line.elements.map { it.text.lowercase() }
                val index = elements.indexOf("take")
                if (index == -1) {
                    continue
                }
                try {
                    return if (elements[index + 1] !in numericMap)
                        numericMap[elements[index + 2]]!!.toInt()
                    else {
                        numericMap[elements[index + 1]]!!.toInt()
                    }
                } catch (e: Exception) {
                    Log.e("EXCEPTION", e.stackTrace.toString())
                }
            }
        }
        return 0
    }

    fun getMedicationName(): String {
        try {
            for (block in text.result.textBlocks) {
                for (line in block.lines) {
                    val elements: List<String> = line.elements.map { it.text.lowercase() }

                    val index = elements.indexOf("28")
                    if (index == -1) {
                        continue
                    }
                    try {
                        return if (elements[index + 1] in listOf("tablet", "tablets")) {
                            elements[index + 2]
                        } else {
                            elements[index + 1]
                        }
                    } catch (e: Exception) {
                        Log.e("Exception", e.message.toString())
                        continue
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.message.toString())
        }
        return ""
    }

}
