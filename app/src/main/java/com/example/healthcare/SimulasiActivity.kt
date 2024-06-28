package com.example.healthcare

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.health.connect.datatypes.units.BloodGlucose
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "healthcarediabet.tflite"

    private lateinit var resultText: TextView
    private lateinit var pregnancies: EditText
    private lateinit var glucose: EditText
    private lateinit var bloodpressure: EditText
    private lateinit var skinthickness: EditText
    private lateinit var insulin: EditText
    private lateinit var bmi: EditText
    private lateinit var diabetespedigreefunction: EditText
    private lateinit var age: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        pregnancies = findViewById(R.id.pregnancies)
        glucose = findViewById(R.id.glucose)
        bloodpressure = findViewById(R.id.bloodpressure)
        skinthickness = findViewById(R.id.skinthickness)
        insulin = findViewById(R.id.insulin)
        bmi = findViewById(R.id.bmi)
        diabetespedigreefunction = findViewById(R.id.diabetespedigreefunction)
        age = findViewById(R.id.age)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                pregnancies.text.toString(),
                glucose.text.toString(),
                bloodpressure.text.toString(),
                skinthickness.text.toString(),
                insulin.text.toString(),
                bmi.text.toString(),
                diabetespedigreefunction.text.toString(),
                age.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Penyakit Diabetes"
                }else if (result == 1){
                    resultText.text = "Tidak Terkena Penyakit Diabetes"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(9)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String, input8: String): Int{
        val inputVal = FloatArray(8)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        inputVal[7] = input8.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
