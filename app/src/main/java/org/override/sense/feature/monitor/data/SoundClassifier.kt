package org.override.sense.feature.monitor.data

import android.content.Context
import org.override.sense.feature.monitor.domain.SoundCategory
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class ClassificationResult(
    val label: String,
    val score: Float,
    val category: SoundCategory,
    val index: Int
)

class SoundClassifier(
    private val context: Context
) : AutoCloseable {
    companion object {
        private const val MODEL_FILENAME = "1.tflite"
        // Based on the CSV we fetched:
        // 394 -> Fire alarm
        // 390, 391, 317, 318, 319 -> Sirens/Emergency
        // 20 -> Baby cry
        // 349, 350, 353 -> Doorbell, Knock
        
        private val CRITICAL_INDICES = setOf(394, 390, 391, 317, 318, 319, 393, 382) // Added 393 (Smoke), 382 (Alarm)
        private val WARNING_INDICES = setOf(349, 350, 353, 392)
        private val INFO_INDICES = setOf(20, 19, 14) // Baby cry, Crying, Baby laughter
    }

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    init {
        setup()
    }
    
    override fun close() {
        interpreter?.close()
        interpreter = null
    }

    private fun setup() {
        try {
            val model = loadModelFile(context, MODEL_FILENAME)
            interpreter = Interpreter(model)
            labels = loadLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels(): List<String> {
        val labelList = mutableListOf<String>()
        try {
            // Placeholder for label loading if needed
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return labelList
    }

    fun classify(audioData: FloatArray): List<ClassificationResult> {
        if (interpreter == null) return emptyList()

        // YAMNet Input: [1, 15600]
        // Output: [1, 521]
        
        // Ensure input is the right size (pad or trim)
        val input = FloatArray(15600)
        if (audioData.size >= 15600) {
            System.arraycopy(audioData, 0, input, 0, 15600)
        } else {
            System.arraycopy(audioData, 0, input, 0, audioData.size)
        }

        val outputBuffer = Array(1) { FloatArray(521) }
        interpreter?.run(input, outputBuffer)

        val scores = outputBuffer[0]
        val results = mutableListOf<ClassificationResult>()

        // Find top scores or specific interesting categories
        for (i in scores.indices) {
            val score = scores[i]
            if (score > 0.3f) { // Lowered threshold to 0.3f
                val category = getCategoryForIndex(i)
                if (category != null) {
                    results.add(
                        ClassificationResult(
                            label = getLabelForIndex(i),
                            score = score,
                            category = category,
                            index = i
                        )
                    )
                }
            }
        }

        return results.sortedByDescending { it.score }
    }

    private fun getCategoryForIndex(index: Int): SoundCategory? {
        return when (index) {
            in CRITICAL_INDICES -> SoundCategory.CRITICAL
            in WARNING_INDICES -> SoundCategory.WARNING
            in INFO_INDICES -> SoundCategory.INFO
            else -> null // We ignore other sounds for this app requirements
        }
    }

    private fun getLabelForIndex(index: Int): String {
        // Mapping based on the CSV fetched earlier
        return when (index) {
            394 -> "Alarma de Incendio"
            390 -> "Sirena"
            391 -> "Sirena de Defensa Civil"
            317 -> "Sirena de Policía"
            318 -> "Sirena de Ambulancia"
            319 -> "Sirena de Bomberos"
            392 -> "Zumbador (Buzzer)"
            393 -> "Detector de Humo"
            349 -> "Timbre de Puerta"
            350 -> "Ding-dong"
            353 -> "Tocan la Puerta"
            20 -> "Bebé Llorando"
            19 -> "Llanto"
            14 -> "Risa de Bebé"
            else -> "Sonido Detectado ($index)"
        }
    }
}
