package com.example.player

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object AudioSynthesizer {

    private var currentTrack: AudioTrack? = null
    private var isPlaying = false

    /**
     * Plays a pleasant ambient melodic chord sequence using AudioTrack.
     * Guaranteed to work offline with zero external network dependencies.
     */
    suspend fun playSynthesizedMelody(onProgress: (Int, Int) -> Unit, onComplete: () -> Unit) = withContext(Dispatchers.IO) {
        stop()
        isPlaying = true

        val sampleRate = 44100
        val frequencies = doubleArrayOf(440.0, 554.37, 659.25, 880.0, 659.25, 554.37) // A major arpeggio
        val noteDurationSec = 0.5
        val numSamplesPerNote = (sampleRate * noteDurationSec).toInt()
        val totalNotes = frequencies.size * 4 // 12 seconds loop
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        try {
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
                .setBufferSizeInBytes(bufferSize.coerceAtLeast(numSamplesPerNote * 2))
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            currentTrack = track
            track.play()

            var currentSample = 0
            val shortBuffer = ShortArray(numSamplesPerNote)

            for (noteIdx in 0 until totalNotes) {
                if (!isPlaying) break
                val freq = frequencies[noteIdx % frequencies.size]

                for (i in 0 until numSamplesPerNote) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                    val envelope = sin(Math.PI * i / numSamplesPerNote) // Smooth attack and release
                    val sampleVal = (sin(angle) * envelope * 0.7 * Short.MAX_VALUE).toInt()
                    shortBuffer[i] = sampleVal.toShort()
                }

                track.write(shortBuffer, 0, numSamplesPerNote)
                currentSample += numSamplesPerNote
                val currentMs = (currentSample * 1000L / sampleRate).toInt()
                val totalMs = (totalNotes * numSamplesPerNote * 1000L / sampleRate).toInt()
                onProgress(currentMs, totalMs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stop()
            onComplete()
        }
    }

    fun stop() {
        isPlaying = false
        try {
            currentTrack?.pause()
            currentTrack?.flush()
            currentTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        currentTrack = null
    }
}
