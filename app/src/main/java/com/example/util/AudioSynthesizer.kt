package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AudioSynthesizer {
  private val scope = CoroutineScope(Dispatchers.Default)
  var soundEnabled: Boolean = true

  fun playChime() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 880.0, durationMs = 350, decay = 0.008)
    }
  }

  fun playBlueLanternGlow() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 1046.5, durationMs = 400, decay = 0.006)
    }
  }

  fun playChainClink() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 320.0, durationMs = 120, decay = 0.025)
    }
  }

  fun playTempleBell() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 220.0, durationMs = 800, decay = 0.004)
    }
  }

  fun playKoiFlutter() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 1320.0, durationMs = 250, decay = 0.012)
    }
  }

  fun playWeatherUnmake() {
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 587.33, durationMs = 500, decay = 0.005)
    }
  }

  private fun playTone(frequency: Double, durationMs: Int, decay: Double) {
    try {
      val sampleRate = 22050
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val time = i.toDouble() / sampleRate
        val envelope = exp(-decay * i)
        val sample = (sin(2.0 * PI * frequency * time) * envelope * 0.7 * Short.MAX_VALUE).toInt()
        buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }

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
      // Release after playing
      Thread.sleep((durationMs + 100).toLong())
      audioTrack.stop()
      audioTrack.release()
    } catch (_: Exception) {
      // Ignore audio failure gracefully
    }
  }
}
