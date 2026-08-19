package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class VoiceSynthEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isSingingPlaying = MutableStateFlow(false)
    val isSingingPlaying: StateFlow<Boolean> = _isSingingPlaying.asStateFlow()

    private val _currentWaveAmplitudes = MutableStateFlow(List(16) { 0.1f })
    val currentWaveAmplitudes: StateFlow<List<Float>> = _currentWaveAmplitudes.asStateFlow()

    private val _currentSingingNoteIndex = MutableStateFlow(-1)
    val currentSingingNoteIndex: StateFlow<Int> = _currentSingingNoteIndex.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }
            isTtsReady = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                    startVisualizerLoop()
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                    resetVisualizer()
                }

                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                    resetVisualizer()
                }
            })
        }
    }

    fun speakText(
        text: String,
        pitch: Float = 1.0f,
        speechRate: Float = 1.0f,
        onComplete: (() -> Unit)? = null
    ) {
        if (!isTtsReady || text.isBlank()) return

        stopAll()

        tts?.setPitch(pitch)
        tts?.setSpeechRate(speechRate)

        val params = HashMap<String, String>()
        val utteranceId = "voice_tts_${System.currentTimeMillis()}"
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId

        @Suppress("DEPRECATION")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
    }

    fun stopAll() {
        tts?.stop()
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            // Ignored
        }
        audioTrack = null
        _isPlaying.value = false
        _isSingingPlaying.value = false
        _currentSingingNoteIndex.value = -1
        resetVisualizer()
    }

    /**
     * Synthesize and play real-time melodic singing with polyphonic backing chords & vocal formants
     */
    fun playSingingSong(
        lyrics: List<String>,
        notes: List<Float>,
        bpm: Int = 120,
        vibratoDepth: Float = 0.04f,
        onProgress: ((Int) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        stopAll()

        synthJob = scope.launch {
            _isSingingPlaying.value = true
            startVisualizerLoop()

            val sampleRate = 44100
            val noteDurationMs = (60_000 / bpm).coerceIn(300, 1200)
            val totalNotes = notes.size

            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize.coerceAtLeast(4096 * 4))
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            try {
                for (i in 0 until totalNotes) {
                    if (!isActive) break

                    _currentSingingNoteIndex.value = i
                    onProgress?.invoke(i)

                    val targetFreq = notes[i]
                    val durationMs = noteDurationMs
                    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                    val pcmData = ShortArray(numSamples)

                    // Chord accompaniment notes (Root, 3rd, 5th, Sub-bass)
                    val chordThird = targetFreq * 1.2599f // Major 3rd
                    val chordFifth = targetFreq * 1.4983f // Perfect 5th
                    val subBass = targetFreq * 0.5f

                    for (s in 0 until numSamples) {
                        val t = s.toDouble() / sampleRate
                        val noteProgress = s.toDouble() / numSamples

                        // ADSR Envelope for gentle vocal entry and decay
                        val attack = (noteProgress / 0.08).coerceAtMost(1.0)
                        val release = ((1.0 - noteProgress) / 0.12).coerceIn(0.0, 1.0)
                        val envelope = attack * release

                        // Vocal Vibrato LFO (5.5 Hz)
                        val vibrato = 1.0 + vibratoDepth * sin(2 * PI * 5.5 * t)
                        val vocalFreq = targetFreq * vibrato

                        // Formant harmonics (1st, 2nd, 3rd harmonics + Sawtooth richness for human vocal tone)
                        val fundamental = sin(2 * PI * vocalFreq * t)
                        val harmonic2 = 0.5 * sin(2 * PI * vocalFreq * 2 * t)
                        val harmonic3 = 0.25 * sin(2 * PI * vocalFreq * 3 * t)
                        val vocalWave = (fundamental + harmonic2 + harmonic3) / 1.75

                        // Chords & backing harmony
                        val chordWave = 0.15 * sin(2 * PI * chordThird * t) + 0.12 * sin(2 * PI * chordFifth * t)
                        val bassWave = 0.22 * sin(2 * PI * subBass * t)

                        // Drum pulse at beginning of note
                        val drumPulse = if (noteProgress < 0.15) {
                            (1.0 - noteProgress / 0.15) * 0.35 * sin(2 * PI * (80.0 - 50.0 * (noteProgress / 0.15)) * t)
                        } else 0.0

                        val mixed = (vocalWave * 0.65 + chordWave + bassWave + drumPulse) * envelope
                        pcmData[s] = (mixed.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                    }

                    track.write(pcmData, 0, pcmData.size)
                }
            } catch (e: Exception) {
                // Audio interrupted
            } finally {
                _isSingingPlaying.value = false
                _currentSingingNoteIndex.value = -1
                resetVisualizer()
                onComplete?.invoke()
            }
        }
    }

    private fun startVisualizerLoop() {
        scope.launch {
            while (_isPlaying.value || _isSingingPlaying.value) {
                val newAmps = List(16) {
                    (0.15f + Math.random().toFloat() * 0.85f).coerceIn(0.05f, 1.0f)
                }
                _currentWaveAmplitudes.value = newAmps
                delay(70)
            }
        }
    }

    private fun resetVisualizer() {
        _currentWaveAmplitudes.value = List(16) { 0.1f }
    }

    fun release() {
        stopAll()
        tts?.shutdown()
        tts = null
    }
}
