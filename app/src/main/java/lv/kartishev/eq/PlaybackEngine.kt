package lv.kartishev.eq

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioManager
import android.os.Build

object PlaybackEngine {
    var mEngineHandle: Long = 0
    fun create(context: Context): Boolean {
        if (mEngineHandle == 0L) {
            setDefaultStreamValues(context)
            mEngineHandle = native_createEngine(context.assets)
        }
        return mEngineHandle != 0L
    }

    private fun setDefaultStreamValues(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val myAudioMgr = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
            val defaultSampleRate = sampleRateStr.toInt()
            val framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
            val defaultFramesPerBurst = framesPerBurstStr.toInt()
            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst)
        }
    }

    fun delete() {
        if (mEngineHandle != 0L) {
            native_deleteEngine(mEngineHandle)
        }
        mEngineHandle = 0
    }

    fun setToneOn(isToneOn: Boolean) {
        if (mEngineHandle != 0L) native_setToneOn(mEngineHandle, isToneOn)
    }

    fun setEQ(isEqOn: Boolean) {
        if (mEngineHandle != 0L) native_setEQ(mEngineHandle, isEqOn)
    }

    fun changeEQ() {
        if (mEngineHandle != 0L) native_changeEQ(mEngineHandle)
    }

    val frequency: Float
        get() = if (mEngineHandle != 0L) native_getFrequency(mEngineHandle) else 0.0f

    // Native methods
    private external fun native_createEngine(assetManager: AssetManager): Long
    private external fun native_deleteEngine(engineHandle: Long)
    private external fun native_setToneOn(engineHandle: Long, isToneOn: Boolean)

    private external fun native_getFrequency(engineHandle: Long): Float
    private external fun native_setEQ(engineHandle: Long, isEqOn: Boolean)

    private external fun native_changeEQ(engineHandle: Long)
    private external fun native_setDefaultStreamValues(sampleRate: Int, framesPerBurst: Int)

    // Load native library
    init {
        System.loadLibrary("hello-oboe")
    }
}