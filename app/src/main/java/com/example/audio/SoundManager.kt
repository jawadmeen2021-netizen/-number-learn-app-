package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class SoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arLocale = Locale("ar")
            val result = tts?.setLanguage(arLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.setSpeechRate(0.85f)
            tts?.setPitch(1.15f) // Friendlier higher pitch for children
            isTtsReady = true
        }
    }

    fun speak(text: String) {
        if (!soundEnabled) return
        if (isTtsReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ID_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    // Audio synthesizer for crisp, instant kids sound effects
    fun playPopSound() {
        if (!soundEnabled) return
        triggerHaptic(30)
        scope.launch {
            playTone(frequency = 550.0, durationMs = 60, shape = "pop")
        }
    }

    fun playCorrectSound() {
        if (!soundEnabled) return
        triggerHaptic(50)
        scope.launch {
            // Cheerful ascending arpeggio C5 - E5 - G5 - C6
            playTone(523.25, 80, "sine")
            playTone(659.25, 80, "sine")
            playTone(783.99, 90, "sine")
            playTone(1046.50, 160, "bell")
        }
    }

    fun playWrongSound() {
        if (!soundEnabled) return
        triggerHaptic(70)
        scope.launch {
            // Gentle warm boop
            playTone(330.0, 90, "sine")
            playTone(260.0, 140, "sine")
        }
    }

    fun playStarSound() {
        if (!soundEnabled) return
        triggerHaptic(40)
        scope.launch {
            // Sparkle chime
            playTone(880.0, 70, "bell")
            playTone(1174.66, 90, "bell")
            playTone(1760.0, 150, "bell")
        }
    }

    fun playFanfareSound() {
        if (!soundEnabled) return
        triggerHaptic(80)
        scope.launch {
            playTone(523.25, 100, "bell")
            playTone(659.25, 100, "bell")
            playTone(783.99, 120, "bell")
            playTone(1046.50, 280, "bell")
        }
    }

    private fun playTone(frequency: Double, durationMs: Int, shape: String = "sine") {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val rawVal = when (shape) {
                    "pop" -> {
                        // Quick pitch drop
                        val decay = 1.0 - (i.toDouble() / numSamples)
                        sin(2.0 * Math.PI * (frequency * decay) * t) * decay
                    }
                    "bell" -> {
                        val decay = Math.exp(-4.0 * (i.toDouble() / numSamples))
                        (sin(2.0 * Math.PI * frequency * t) + 0.3 * sin(4.0 * Math.PI * frequency * t)) * decay
                    }
                    else -> {
                        val envelope = when {
                            i < numSamples * 0.1 -> i / (numSamples * 0.1)
                            i > numSamples * 0.7 -> 1.0 - (i - numSamples * 0.7) / (numSamples * 0.3)
                            else -> 1.0
                        }
                        sin(2.0 * Math.PI * frequency * t) * envelope
                    }
                }
                buffer[i] = (rawVal * Short.MAX_VALUE * 0.65).toInt().toShort()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 30)
            track.release()
        } catch (_: Exception) {
            // Gracefully ignore audio errors on headless environments
        }
    }

    private fun triggerHaptic(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
            // Ignore haptic failures
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
