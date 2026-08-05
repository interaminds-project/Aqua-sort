package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

data class BgmTrack(
    val id: String,
    val name: String,
    val style: String,
    val keyName: String,
    val chords: List<DoubleArray>,
    val melodySequence: List<Pair<Double, Double>>,
    val cycleDurationSec: Double = 64.0
)

data class BgmTrackInfo(
    val id: String,
    val name: String,
    val style: String,
    val keyName: String
)

data class BgmState(
    val currentTrackIndex: Int = 0,
    val currentTrackName: String = "Aquatic Serenity",
    val currentTrackStyle: String = "Water Meditation",
    val currentTrackKey: String = "C Major / A Minor",
    val isPlaying: Boolean = false,
    val isAutoPlaylist: Boolean = true,
    val bgmVolume: Float = 0.8f,
    val crossfadeActive: Boolean = false,
    val playlist: List<BgmTrackInfo> = emptyList()
)

object SoothingAudioEngine {
    private const val SAMPLE_RATE = 22050
    private var bgmJob: Job? = null
    private var isBgmRunning = false

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
        set(value) {
            field = value
            if (value) startBgm() else stopBgm()
        }

    // Playlist tracks definitions
    val tracks = listOf(
        BgmTrack(
            id = "aquatic_serenity",
            name = "Aquatic Serenity",
            style = "Water Meditation",
            keyName = "C Major / A Minor",
            chords = listOf(
                doubleArrayOf(130.81, 196.00, 246.94, 329.63, 392.00),
                doubleArrayOf(110.00, 164.81, 196.00, 261.63, 329.63),
                doubleArrayOf(87.31, 130.81, 164.81, 220.00, 261.63),
                doubleArrayOf(98.00, 146.83, 220.00, 246.94, 293.66)
            ),
            melodySequence = listOf(
                0.8 to 523.25, 2.4 to 659.25, 4.0 to 783.99, 6.2 to 659.25, 8.5 to 587.33, 10.8 to 523.25, 13.2 to 392.00,
                16.8 to 440.00, 18.4 to 523.25, 20.2 to 659.25, 22.5 to 783.99, 25.0 to 659.25, 27.4 to 523.25, 29.8 to 440.00,
                32.8 to 349.23, 34.5 to 440.00, 36.2 to 523.25, 38.6 to 659.25, 41.0 to 523.25, 43.5 to 440.00, 45.8 to 349.23,
                48.8 to 392.00, 50.5 to 493.88, 52.2 to 587.33, 54.6 to 659.25, 57.0 to 587.33, 59.5 to 493.88, 61.8 to 392.00
            )
        ),
        BgmTrack(
            id = "celestial_flow",
            name = "Celestial Flow",
            style = "Cosmic Chimes",
            keyName = "D Major Lydian",
            chords = listOf(
                doubleArrayOf(146.83, 220.00, 277.18, 369.99, 440.00),
                doubleArrayOf(123.47, 185.00, 220.00, 293.66, 369.99),
                doubleArrayOf(98.00, 146.83, 185.00, 246.94, 293.66),
                doubleArrayOf(110.00, 164.81, 246.94, 277.18, 329.63)
            ),
            melodySequence = listOf(
                1.0 to 739.99, 3.2 to 880.00, 5.5 to 1108.73, 8.0 to 880.00, 10.5 to 739.99, 13.0 to 659.25,
                17.0 to 587.33, 19.5 to 554.37, 22.0 to 659.25, 24.5 to 739.99, 27.0 to 880.00, 29.5 to 1108.73,
                33.0 to 987.77, 35.5 to 880.00, 38.0 to 739.99, 40.5 to 659.25, 43.0 to 587.33, 46.0 to 739.99,
                49.0 to 880.00, 52.0 to 987.77, 55.0 to 880.00, 58.0 to 739.99, 61.0 to 659.25
            )
        ),
        BgmTrack(
            id = "zen_alchemist",
            name = "Zen Alchemist",
            style = "Peaceful Harp",
            keyName = "D Minor Pentatonic",
            chords = listOf(
                doubleArrayOf(146.83, 220.00, 261.63, 329.63, 349.23),
                doubleArrayOf(98.00, 146.83, 174.61, 220.00, 261.63),
                doubleArrayOf(116.54, 174.61, 220.00, 293.66, 349.23),
                doubleArrayOf(130.81, 196.00, 261.63, 293.66, 392.00)
            ),
            melodySequence = listOf(
                1.2 to 587.33, 3.5 to 698.46, 6.0 to 783.99, 8.5 to 880.00, 11.0 to 1046.50, 13.5 to 880.00,
                17.2 to 783.99, 19.8 to 698.46, 22.4 to 587.33, 25.0 to 440.00, 27.5 to 523.25, 30.0 to 587.33,
                33.5 to 698.46, 36.0 to 783.99, 38.5 to 698.46, 41.0 to 587.33, 43.5 to 523.25, 46.0 to 440.00,
                49.2 to 349.23, 51.8 to 392.00, 54.4 to 440.00, 57.0 to 523.25, 60.0 to 587.33
            )
        ),
        BgmTrack(
            id = "starlight_echoes",
            name = "Starlight Echoes",
            style = "Deep Relaxation",
            keyName = "Eb Major Ambient",
            chords = listOf(
                doubleArrayOf(155.56, 233.08, 293.66, 349.23, 440.00),
                doubleArrayOf(130.81, 196.00, 233.08, 311.13, 392.00),
                doubleArrayOf(103.83, 155.56, 207.65, 261.63, 311.13),
                doubleArrayOf(116.54, 174.61, 261.63, 293.66, 349.23)
            ),
            melodySequence = listOf(
                1.5 to 783.99, 4.0 to 932.33, 6.8 to 1174.66, 9.5 to 1046.50, 12.0 to 932.33, 14.5 to 783.99,
                17.8 to 622.25, 20.2 to 698.46, 22.8 to 783.99, 25.5 to 932.33, 28.2 to 1174.66, 31.0 to 1046.50,
                34.0 to 932.33, 36.8 to 783.99, 39.5 to 698.46, 42.0 to 622.25, 45.0 to 783.99, 48.0 to 932.33,
                51.0 to 1046.50, 54.0 to 932.33, 57.5 to 783.99, 61.0 to 622.25
            )
        )
    )

    private val playlistInfoList = tracks.map { BgmTrackInfo(it.id, it.name, it.style, it.keyName) }

    private var activeTrackIndex = 0
    private var pendingTrackIndex: Int? = null
    private var isAutoPlaylistMode = true
    private var bgmVolumeLevel = 0.8f

    private val _bgmState = MutableStateFlow(
        BgmState(
            currentTrackIndex = 0,
            currentTrackName = tracks[0].name,
            currentTrackStyle = tracks[0].style,
            currentTrackKey = tracks[0].keyName,
            isPlaying = false,
            isAutoPlaylist = true,
            bgmVolume = 0.8f,
            playlist = playlistInfoList
        )
    )
    val bgmState: StateFlow<BgmState> = _bgmState.asStateFlow()

    fun init(soundOn: Boolean, musicOn: Boolean) {
        this.isSoundEnabled = soundOn
        this.isMusicEnabled = musicOn
        if (musicOn) {
            startBgm()
        }
    }

    fun nextTrack() {
        val nextIdx = (activeTrackIndex + 1) % tracks.size
        selectTrack(nextIdx)
    }

    fun prevTrack() {
        val prevIdx = if (activeTrackIndex - 1 < 0) tracks.size - 1 else activeTrackIndex - 1
        selectTrack(prevIdx)
    }

    fun selectTrack(index: Int) {
        if (index !in tracks.indices) return
        if (pendingTrackIndex == index || (activeTrackIndex == index && pendingTrackIndex == null)) return
        pendingTrackIndex = index
        updateState()
    }

    fun toggleAutoPlaylist() {
        isAutoPlaylistMode = !isAutoPlaylistMode
        updateState()
    }

    fun setBgmVolume(volume: Float) {
        bgmVolumeLevel = volume.coerceIn(0f, 1f)
        updateState()
    }

    private fun updateState() {
        val track = tracks[activeTrackIndex]
        _bgmState.value = BgmState(
            currentTrackIndex = activeTrackIndex,
            currentTrackName = track.name,
            currentTrackStyle = track.style,
            currentTrackKey = track.keyName,
            isPlaying = isBgmRunning && isMusicEnabled,
            isAutoPlaylist = isAutoPlaylistMode,
            bgmVolume = bgmVolumeLevel,
            crossfadeActive = pendingTrackIndex != null,
            playlist = playlistInfoList
        )
    }

    fun playPourSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val numSamples = (SAMPLE_RATE * 0.35).toInt()
                val buffer = ShortArray(numSamples)
                var phase = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val freq = 360.0 + progress * 240.0 + sin(i * 0.08) * 30.0
                    phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                    val env = (sin(progress * Math.PI) * 0.22).toFloat()
                    val sample = (sin(phase) * env * 32767).toInt().coerceIn(-32768, 32767)
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSelectSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val numSamples = (SAMPLE_RATE * 0.18).toInt()
                val buffer = ShortArray(numSamples)
                var phase = 0.0
                val freq = 880.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                    val env = (exp(-progress * 9.0) * 0.2).toFloat()
                    val sample = (sin(phase) * env * 32767).toInt().coerceIn(-32768, 32767)
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playVictorySound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25)
                val durationPerNote = (SAMPLE_RATE * 0.12).toInt()
                val totalSamples = durationPerNote * notes.size + (SAMPLE_RATE * 0.4).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * durationPerNote
                    var phase = 0.0
                    val len = totalSamples - startSample
                    for (i in 0 until len) {
                        val progress = i.toFloat() / len
                        phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                        val env = (exp(-progress * 4.5) * 0.22).toFloat()
                        val sample = (sin(phase) * env * 32767).toInt()
                        val pos = startSample + i
                        if (pos < buffer.size) {
                            val existing = buffer[pos].toInt()
                            buffer[pos] = (existing + sample).coerceIn(-32768, 32767).toShort()
                        }
                    }
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playMilestoneFanfareSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val notes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25, 783.99, 1046.50, 1318.51)
                val durationPerNote = (SAMPLE_RATE * 0.10).toInt()
                val totalSamples = durationPerNote * notes.size + (SAMPLE_RATE * 0.6).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * durationPerNote
                    var phase = 0.0
                    val len = totalSamples - startSample
                    for (i in 0 until len) {
                        val progress = i.toFloat() / len
                        phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                        val env = (exp(-progress * 3.8) * 0.22).toFloat()
                        val sample = (sin(phase) * env * 32767).toInt()
                        val pos = startSample + i
                        if (pos < buffer.size) {
                            val existing = buffer[pos].toInt()
                            buffer[pos] = (existing + sample).coerceIn(-32768, 32767).toShort()
                        }
                    }
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playTubeCompletedSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Soft magical ascending pentatonic shimmer chime (C6, E6, G6, B6, C7)
                val notes = doubleArrayOf(1046.50, 1318.51, 1567.98, 1975.53, 2093.00)
                val durationPerNote = (SAMPLE_RATE * 0.07).toInt()
                val totalSamples = durationPerNote * notes.size + (SAMPLE_RATE * 0.45).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * durationPerNote
                    var phase = 0.0
                    val len = totalSamples - startSample
                    for (i in 0 until len) {
                        val progress = i.toFloat() / len
                        // Gentle magical shimmer modulation
                        val shimmer = 1.0 + 0.006 * sin(i * 0.025)
                        phase += 2.0 * Math.PI * (freq * shimmer) / SAMPLE_RATE
                        // Exponential bell envelope with soft decay
                        val env = (exp(-progress * 4.8) * 0.15).toFloat()
                        val sample = (sin(phase) * env * 32767).toInt()
                        val pos = startSample + i
                        if (pos < buffer.size) {
                            val existing = buffer[pos].toInt()
                            buffer[pos] = (existing + sample).coerceIn(-32768, 32767).toShort()
                        }
                    }
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playClickSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val numSamples = (SAMPLE_RATE * 0.06).toInt()
                val buffer = ShortArray(numSamples)
                var phase = 0.0
                val freq = 520.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                    val env = (1f - progress) * 0.12f
                    val sample = (sin(phase) * env * 32767).toInt().coerceIn(-32768, 32767)
                    buffer[i] = sample.toShort()
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playCatalystSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val durationPerNote = (SAMPLE_RATE * 0.08).toInt()
                val totalSamples = durationPerNote * notes.size + (SAMPLE_RATE * 0.3).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * durationPerNote
                    var phase = 0.0
                    val len = totalSamples - startSample
                    for (i in 0 until len) {
                        val progress = i.toFloat() / len
                        phase += 2.0 * Math.PI * freq / SAMPLE_RATE
                        val env = (exp(-progress * 6.0) * 0.18).toFloat()
                        val sample = (sin(phase) * env * 32767).toInt()
                        val pos = startSample + i
                        if (pos < buffer.size) {
                            val existing = buffer[pos].toInt()
                            buffer[pos] = (existing + sample).coerceIn(-32768, 32767).toShort()
                        }
                    }
                }
                playBuffer(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playBuffer(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        CoroutineScope(Dispatchers.Default).launch {
            delay(800)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (ignored: Exception) {}
        }
    }

    private fun startBgm() {
        if (isBgmRunning) return
        isBgmRunning = true
        updateState()

        bgmJob = CoroutineScope(Dispatchers.Default).launch {
            var audioTrack: AudioTrack? = null
            try {
                val minBufSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufSize.coerceAtLeast(4096)
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val chunkSamples = 1024
                val buffer = ShortArray(chunkSamples)

                var currentSampleCounter = 0L
                var nextSampleCounter = 0L

                class ActiveNote(val freq: Double, val startSample: Long)
                val activeNotesCurrent = mutableListOf<ActiveNote>()
                val activeNotesNext = mutableListOf<ActiveNote>()

                var lastMelodyIdxCurrent = -1
                var lastMelodyIdxNext = -1

                var crossfadeSampleIndex = 0L
                val crossfadeTotalSamples = (SAMPLE_RATE * 3.5).toLong()

                while (isActive && isMusicEnabled) {
                    val currentTrack = tracks[activeTrackIndex]
                    val cycleDuration = currentTrack.cycleDurationSec

                    val currentSec = currentSampleCounter.toDouble() / SAMPLE_RATE
                    val currentCycleTime = currentSec % cycleDuration

                    val transitionLeadSec = 3.5
                    if (pendingTrackIndex == null && currentCycleTime >= (cycleDuration - transitionLeadSec)) {
                        if (isAutoPlaylistMode) {
                            pendingTrackIndex = (activeTrackIndex + 1) % tracks.size
                        } else {
                            pendingTrackIndex = activeTrackIndex
                        }
                        updateState()
                    }

                    val chunkSpanSec = chunkSamples.toDouble() / SAMPLE_RATE
                    for (i in currentTrack.melodySequence.indices) {
                        val (noteTime, freq) = currentTrack.melodySequence[i]
                        if (currentCycleTime <= noteTime && (currentCycleTime + chunkSpanSec) > noteTime) {
                            if (i != lastMelodyIdxCurrent) {
                                lastMelodyIdxCurrent = i
                                activeNotesCurrent.add(ActiveNote(freq, currentSampleCounter))
                            }
                        }
                    }

                    var targetNextIdx = pendingTrackIndex
                    if (targetNextIdx != null) {
                        val nextTrack = tracks[targetNextIdx]
                        val nextSec = nextSampleCounter.toDouble() / SAMPLE_RATE
                        val nextCycleTime = nextSec % nextTrack.cycleDurationSec
                        for (i in nextTrack.melodySequence.indices) {
                            val (noteTime, freq) = nextTrack.melodySequence[i]
                            if (nextCycleTime <= noteTime && (nextCycleTime + chunkSpanSec) > noteTime) {
                                if (i != lastMelodyIdxNext) {
                                    lastMelodyIdxNext = i
                                    activeNotesNext.add(ActiveNote(freq, nextSampleCounter))
                                }
                            }
                        }
                    }

                    val maxNoteSamples = (SAMPLE_RATE * 4.0).toLong()
                    activeNotesCurrent.removeAll { (currentSampleCounter - it.startSample) > maxNoteSamples }
                    if (targetNextIdx != null) {
                        activeNotesNext.removeAll { (nextSampleCounter - it.startSample) > maxNoteSamples }
                    } else {
                        activeNotesNext.clear()
                    }

                    for (s in 0 until chunkSamples) {
                        val absSample = currentSampleCounter + s
                        val timeSec = absSample.toDouble() / SAMPLE_RATE
                        val cycleTime = timeSec % cycleDuration

                        val chordIdx = (cycleTime / (cycleDuration / 4.0)).toInt().coerceIn(0, 3)
                        val nextChordIdx = (chordIdx + 1) % 4
                        val chordProgress = (cycleTime % (cycleDuration / 4.0)) / (cycleDuration / 4.0)
                        val chordFade = if (chordProgress > 0.85) (chordProgress - 0.85) / 0.15 else 0.0

                        val currChords = currentTrack.chords[chordIdx]
                        val nxtChords = currentTrack.chords[nextChordIdx]

                        var padMixCurrent = 0.0
                        for (idx in currChords.indices) {
                            val f = currChords[idx]
                            val phase = 2.0 * Math.PI * f * timeSec
                            val swell = sin(timeSec * 0.35) * 0.015 + 0.025
                            val weight = 1.0 / (idx * 0.8 + 1.0)
                            padMixCurrent += sin(phase) * weight * swell * (1.0 - chordFade)
                        }
                        for (idx in nxtChords.indices) {
                            val f = nxtChords[idx]
                            val phase = 2.0 * Math.PI * f * timeSec
                            val swell = sin(timeSec * 0.35) * 0.015 + 0.025
                            val weight = 1.0 / (idx * 0.8 + 1.0)
                            padMixCurrent += sin(phase) * weight * swell * chordFade
                        }

                        var melodyMixCurrent = 0.0
                        for (note in activeNotesCurrent) {
                            val noteAgeSample = absSample - note.startSample
                            if (noteAgeSample >= 0) {
                                val noteAgeSec = noteAgeSample.toDouble() / SAMPLE_RATE
                                val f = note.freq
                                val attack = (noteAgeSec / 0.018).coerceAtMost(1.0)
                                val decay = exp(-noteAgeSec * 1.6)
                                val env = attack * decay * 0.055
                                val phase1 = 2.0 * Math.PI * f * noteAgeSec
                                val phase2 = 2.0 * Math.PI * (f * 2.0) * noteAgeSec
                                val sample = sin(phase1) + 0.22 * sin(phase2) * exp(-noteAgeSec * 2.5)
                                melodyMixCurrent += sample * env
                            }
                        }

                        val sampleCurrent = padMixCurrent + melodyMixCurrent

                        var sampleVal = sampleCurrent
                        if (targetNextIdx != null) {
                            val nextTrack = tracks[targetNextIdx]
                            val absNextSample = nextSampleCounter + s
                            val nextTimeSec = absNextSample.toDouble() / SAMPLE_RATE
                            val nextCycleTime = nextTimeSec % nextTrack.cycleDurationSec

                            val nxtChordIdx = (nextCycleTime / (nextTrack.cycleDurationSec / 4.0)).toInt().coerceIn(0, 3)
                            val nxtNxtChordIdx = (nxtChordIdx + 1) % 4
                            val nxtChordProgress = (nextCycleTime % (nextTrack.cycleDurationSec / 4.0)) / (nextTrack.cycleDurationSec / 4.0)
                            val nxtChordFade = if (nxtChordProgress > 0.85) (nxtChordProgress - 0.85) / 0.15 else 0.0

                            val cChords = nextTrack.chords[nxtChordIdx]
                            val nChords = nextTrack.chords[nxtNxtChordIdx]

                            var padMixNext = 0.0
                            for (idx in cChords.indices) {
                                val f = cChords[idx]
                                val phase = 2.0 * Math.PI * f * nextTimeSec
                                val swell = sin(nextTimeSec * 0.35) * 0.015 + 0.025
                                val weight = 1.0 / (idx * 0.8 + 1.0)
                                padMixNext += sin(phase) * weight * swell * (1.0 - nxtChordFade)
                            }
                            for (idx in nChords.indices) {
                                val f = nChords[idx]
                                val phase = 2.0 * Math.PI * f * nextTimeSec
                                val swell = sin(nextTimeSec * 0.35) * 0.015 + 0.025
                                val weight = 1.0 / (idx * 0.8 + 1.0)
                                padMixNext += sin(phase) * weight * swell * nxtChordFade
                            }

                            var melodyMixNext = 0.0
                            for (note in activeNotesNext) {
                                val noteAgeSample = absNextSample - note.startSample
                                if (noteAgeSample >= 0) {
                                    val noteAgeSec = noteAgeSample.toDouble() / SAMPLE_RATE
                                    val f = note.freq
                                    val attack = (noteAgeSec / 0.018).coerceAtMost(1.0)
                                    val decay = exp(-noteAgeSec * 1.6)
                                    val env = attack * decay * 0.055
                                    val phase1 = 2.0 * Math.PI * f * noteAgeSec
                                    val phase2 = 2.0 * Math.PI * (f * 2.0) * noteAgeSec
                                    val sample = sin(phase1) + 0.22 * sin(phase2) * exp(-noteAgeSec * 2.5)
                                    melodyMixNext += sample * env
                                }
                            }

                            val sampleNext = padMixNext + melodyMixNext

                            val normCrossfade = (crossfadeSampleIndex.toDouble() / crossfadeTotalSamples).coerceIn(0.0, 1.0)
                            val fadeOut = cos(normCrossfade * Math.PI / 2.0)
                            val fadeIn = sin(normCrossfade * Math.PI / 2.0)

                            sampleVal = sampleCurrent * fadeOut + sampleNext * fadeIn

                            crossfadeSampleIndex += 1
                            if (crossfadeSampleIndex >= crossfadeTotalSamples) {
                                activeTrackIndex = targetNextIdx
                                pendingTrackIndex = null
                                targetNextIdx = null
                                currentSampleCounter = nextSampleCounter + s
                                nextSampleCounter = 0L
                                crossfadeSampleIndex = 0L
                                lastMelodyIdxCurrent = lastMelodyIdxNext
                                lastMelodyIdxNext = -1
                                activeNotesCurrent.clear()
                                activeNotesCurrent.addAll(activeNotesNext)
                                activeNotesNext.clear()
                                updateState()
                            }
                        }

                        val outputVal = (sampleVal * bgmVolumeLevel * 32767).toInt().coerceIn(-32768, 32767)
                        buffer[s] = outputVal.toShort()
                    }

                    currentSampleCounter += chunkSamples
                    if (targetNextIdx != null) {
                        nextSampleCounter += chunkSamples
                    }

                    audioTrack.write(buffer, 0, chunkSamples)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (ignored: Exception) {}
                isBgmRunning = false
                updateState()
            }
        }
    }

    private fun stopBgm() {
        isBgmRunning = false
        bgmJob?.cancel()
        bgmJob = null
        updateState()
    }
}
