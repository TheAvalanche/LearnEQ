package lv.kartishev.eq;
/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build;

public class PlaybackEngine {

    static long mEngineHandle = 0;

    // Load native library
    static {
        System.loadLibrary("hello-oboe");
    }

    static boolean create(Context context){

        if (mEngineHandle == 0){
            setDefaultStreamValues(context);
            mEngineHandle = native_createEngine(context.getAssets());
        }
        return (mEngineHandle != 0);
    }

    private static void setDefaultStreamValues(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);

            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
        }
    }

    static void delete(){
        if (mEngineHandle != 0){
            native_deleteEngine(mEngineHandle);
        }
        mEngineHandle = 0;
    }

    static void setToneOn(boolean isToneOn){
        if (mEngineHandle != 0) native_setToneOn(mEngineHandle, isToneOn);
    }

    static void setEQ(boolean isEqOn){
        if (mEngineHandle != 0) native_setEQ(mEngineHandle, isEqOn);
    }

    // Native methods
    private static native long native_createEngine(AssetManager assetManager);
    private static native void native_deleteEngine(long engineHandle);
    private static native void native_setToneOn(long engineHandle, boolean isToneOn);
    private static native void native_setEQ(long engineHandle, boolean isEqOn);
    private static native void native_setDefaultStreamValues(int sampleRate, int framesPerBurst);
}
