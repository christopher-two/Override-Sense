package org.override.sense.feature.monitor.data

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.override.sense.core.common.logging.Logger
import java.util.concurrent.atomic.AtomicBoolean

class AudioRecorder(
    private val logger: Logger
) {
    companion object {
        const val SAMPLE_RATE = 16000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        // YAMNet expects 0.975 seconds of audio at 16kHz = 15600 samples.
        // We'll read in chunks that fit this or are slightly larger/smaller depending on buffer size.
        // Let's ensure the buffer is large enough.
        const val MODEL_INPUT_SIZE = 15600
    }

    private val isRecording = AtomicBoolean(false)
    @Volatile
    private var audioRecord: AudioRecord? = null

    @SuppressLint("MissingPermission") // Permission checked by caller/UI
    fun startRecording(
        microphoneSensitivity: Float = 1.0f,
        minAmplitudeThreshold: Int = 100
    ): Flow<FloatArray> = flow {
        // Ensure we're not already recording
        if (isRecording.getAndSet(true)) {
            logger.w("AudioRecorder", "Recording already in progress, skipping new start")
            return@flow
        }

        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = maxOf(minBufferSize, MODEL_INPUT_SIZE * 2) // Ensure plenty of space

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            logger.e("AudioRecorder", "AudioRecord failed to initialize")
            isRecording.set(false)
            return@flow
        }

        audioRecord = recorder

        try {
            recorder.startRecording()
            logger.d("AudioRecorder", "Recording started with sensitivity: $microphoneSensitivity, threshold: $minAmplitudeThreshold")

            val buffer = ShortArray(MODEL_INPUT_SIZE)

            while (currentCoroutineContext().isActive && isRecording.get()) {
                val readResult = recorder.read(buffer, 0, MODEL_INPUT_SIZE)

                if (readResult > 0) {
                    // Check for silence/zeros to debug mic input
                    var maxAmplitude = 0
                    for (i in 0 until readResult) {
                        val abs = kotlin.math.abs(buffer[i].toInt())
                        if (abs > maxAmplitude) maxAmplitude = abs
                    }
                    
                    if (maxAmplitude < minAmplitudeThreshold) {
                        logger.d("AudioRecorder", "Low amplitude (Silence?): $maxAmplitude")
                        // Skip processing if below threshold
                        continue
                    }

                    // Convert ShortArray to FloatArray normalized to [-1.0, 1.0]
                    // Apply microphone sensitivity as gain
                    val floatBuffer = FloatArray(readResult)
                    for (i in 0 until readResult) {
                        val normalizedValue = buffer[i] / 32768f
                        floatBuffer[i] = (normalizedValue * microphoneSensitivity).coerceIn(-1.0f, 1.0f)
                    }
                    emit(floatBuffer)
                } else {
                    logger.e("AudioRecorder", "Error reading audio: $readResult")
                    break // Exit on read error
                }
            }
        } catch (e: Exception) {
            logger.e("AudioRecorder", "Exception during recording", e)
        } finally {
            cleanupRecorder()
        }
    }

    fun stopRecording() {
        logger.d("AudioRecorder", "Stop recording requested")
        isRecording.set(false)
    }

    private fun cleanupRecorder() {
        try {
            val recorder = audioRecord
            if (recorder != null) {
                if (recorder.state == AudioRecord.STATE_INITIALIZED) {
                    if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                        recorder.stop()
                    }
                    recorder.release()
                    logger.d("AudioRecorder", "AudioRecord released")
                }
                audioRecord = null
            }
        } catch (e: Exception) {
            logger.e("AudioRecorder", "Error cleaning up recorder", e)
        } finally {
            isRecording.set(false)
            logger.d("AudioRecorder", "Recording stopped and cleaned up")
        }
    }
}
