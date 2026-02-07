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

    @SuppressLint("MissingPermission") // Permission checked by caller/UI
    fun startRecording(): Flow<FloatArray> = flow {
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        val bufferSize = maxOf(minBufferSize, MODEL_INPUT_SIZE * 2) // Ensure plenty of space

        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            logger.e("AudioRecorder", "AudioRecord failed to initialize")
            return@flow
        }

        try {
            audioRecord.startRecording()
            isRecording.set(true)
            logger.d("AudioRecorder", "Recording started")

            val buffer = ShortArray(MODEL_INPUT_SIZE)

            while (currentCoroutineContext().isActive && isRecording.get()) {
                val readResult = audioRecord.read(buffer, 0, MODEL_INPUT_SIZE)

                if (readResult > 0) {
                    // Check for silence/zeros to debug mic input
                    var maxAmplitude = 0
                    for (i in 0 until readResult) {
                        val abs = kotlin.math.abs(buffer[i].toInt())
                        if (abs > maxAmplitude) maxAmplitude = abs
                    }
                    if (maxAmplitude < 100) {
                        logger.d("AudioRecorder", "Low amplitude (Silence?): $maxAmplitude")
                    }

                    // Convert ShortArray to FloatArray normalized to [-1.0, 1.0]
                    val floatBuffer = FloatArray(readResult)
                    for (i in 0 until readResult) {
                        floatBuffer[i] = buffer[i] / 32768f
                    }
                    emit(floatBuffer)
                } else {
                    logger.e("AudioRecorder", "Error reading audio: $readResult")
                }
            }
        } catch (e: Exception) {
            logger.e("AudioRecorder", "Exception during recording", e)
        } finally {
            try {
                if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.stop()
                    audioRecord.release()
                }
            } catch (e: Exception) {
                logger.e("AudioRecorder", "Error stopping recording", e)
            }
            isRecording.set(false)
            logger.d("AudioRecorder", "Recording stopped")
        }
    }

    fun stopRecording() {
        isRecording.set(false)
    }
}
