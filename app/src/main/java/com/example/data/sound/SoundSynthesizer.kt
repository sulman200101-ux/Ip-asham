package com.example.data.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time procedural audio synthesizer for fun kid-friendly game sounds.
 * Generates pop, click, chime, star fanfare, and error boings without external audio files.
 */
object SoundSynthesizer {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    fun playPop() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequencies = doubleArrayOf(300.0, 600.0), durationMs = 70, type = ToneType.EXPONENTIAL_DECAY)
        }
    }

    fun playSnap() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequencies = doubleArrayOf(523.25, 659.25), durationMs = 90, type = ToneType.EXPONENTIAL_DECAY)
        }
    }

    fun playSuccess() {
        if (!isSoundEnabled) return
        scope.launch {
            // Happy arpeggio (C5 - E5 - G5 - C6)
            playChords(listOf(523.25, 659.25, 783.99, 1046.50), stepMs = 80, toneDurationMs = 150)
        }
    }

    fun playFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            // Grand victory fanfare
            playChords(listOf(523.25, 659.25, 783.99, 1046.50, 1318.51), stepMs = 90, toneDurationMs = 250)
        }
    }

    fun playStar() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequencies = doubleArrayOf(880.0, 1174.66, 1760.0), durationMs = 180, type = ToneType.SPARKLE)
        }
    }

    fun playBoing() {
        if (!isSoundEnabled) return
        scope.launch {
            // Wobbly boing for tower tilt / mistakes
            generateTone(frequencies = doubleArrayOf(220.0, 180.0, 140.0), durationMs = 150, type = ToneType.WOBBLE)
        }
    }

    fun playClick() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequencies = doubleArrayOf(800.0), durationMs = 40, type = ToneType.EXPONENTIAL_DECAY)
        }
    }

    private enum class ToneType {
        EXPONENTIAL_DECAY,
        SPARKLE,
        WOBBLE
    }

    private fun playChords(notes: List<Double>, stepMs: Long, toneDurationMs: Int) {
        notes.forEachIndexed { _, freq ->
            generateTone(doubleArrayOf(freq), toneDurationMs, ToneType.EXPONENTIAL_DECAY)
            try {
                Thread.sleep(stepMs)
            } catch (_: InterruptedException) {
            }
        }
    }

    private fun generateTone(frequencies: DoubleArray, durationMs: Int, type: ToneType) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            var sample = 0.0

            for (f in frequencies) {
                sample += sin(2.0 * Math.PI * f * time)
            }
            sample /= frequencies.size

            val envelope = when (type) {
                ToneType.EXPONENTIAL_DECAY -> (1.0 - progress) * (1.0 - progress)
                ToneType.SPARKLE -> (1.0 - progress) * (0.8 + 0.2 * sin(50.0 * time))
                ToneType.WOBBLE -> (1.0 - progress) * (0.7 + 0.3 * sin(25.0 * time))
            }

            buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.7).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 30)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore audio device fallback errors
        }
    }
}
